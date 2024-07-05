"use client";

import React, { useMemo, useState, useEffect, useRef } from "react";
import dynamic from "next/dynamic";
import Topbar from "../components/layout/Topbar";
import Sidebar from "../components/layout/Sidebar";
import ConfigurationModal from "../components/map/ConfigurationModal";
import { Airport, Vuelo } from "../types/Planes";
import EndModal from "../components/modal/EndModal";
import { citiesByCode } from "../data/cities";
import "../styles/SimulatedTime.css";
import Notification from "../components/notificacion/Notification";
import MoreInfo from "../components/map/MoreInfo";
import EnvioDetails from "../components/map/EnvioDetails";

const Simulation: React.FC = () => {
  const [showModal, setShowModal] = useState(true);
  const [startSimulation, setStartSimulation] = useState(false);
  const startTime = useRef(0);
  const [simulationMode, setSimulationMode] = useState("");
  const [startDate, setStartDate] = useState("");
  const [startHour, setStartHour] = useState("");
  const vuelos = useRef<Vuelo[]>([]);
  const airports = useRef<Airport[]>([]);
  const airportsHistory = useRef<Airport[][]>([]);
  const [loading, setLoading] = useState(false);
  const simulatedDate = useRef(new Date());
  const [simulationEnd, setSimulationEnd] = useState(false);
  const [simulationTerminated, setSimulationTerminated] = useState(false);
  const [simulationSummary, setSimulationSummary] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");
  const [showMoreInfo, setShowMoreInfo] = useState(false);
  const vuelosInAir = useRef<number>(0);
  const vuelosSaturation = useRef<number>(0);

  const speedFactor = 288; // Real-time seconds per simulated second
  const totalSimulatedSeconds = 7 * 24 * 60 * 60; // One week in seconds
  const dayToDay = false;

  // State to store the display time
  const [displayTime, setDisplayTime] = useState("");

  // States for map center and highlighted plane ID
  const [mapCenter, setMapCenter] = useState<[number, number] | null>(null);
  const [highlightedPlaneId, setHighlightedPlaneId] = useState<string | null>(
    null
  );
  const [forceOpenPopup, setForceOpenPopup] = useState(false);
  const [selectedPackageId, setSelectedPackageId] = useState<string | null>(
    null
  );
  const [selectedAirport, setSelectedAirport] = useState<Airport | null>(null);
  const [envioFound, setEnvioFound] = useState<any[] | null>(null);
  const [showEnvioDetails, setShowEnvioDetails] = useState(false);
  const [selectedPlaneId, setSelectedPlaneId] = useState<string | null>(null); // Nuevo estado para el avión seleccionado


  let isMounted = true;

  useEffect(() => {
    return () => {
      isMounted = false;
    };
  }, []);

  useEffect(() => {
    if (!startSimulation) return;

    console.log("Simulation started");

    // Set the start time
    startTime.current = Date.now();

    // Update the simulated time
    const updateSimulatedTime = () => {
      if (!startSimulation || simulationTerminated) return;

      // console.log("startTime", startTime.current);

      const currentTime = Date.now();
      // console.log("currentTime", currentTime);
      const elapsedTime = (currentTime - startTime.current) / 1000; // in seconds
      const simulatedTime = elapsedTime * speedFactor;
      // Create a new Date object for the start of the simulation
      const startDateSim = new Date(startDate + "T" + startHour + ":00");
      // console.log("startDateSim", startDateSim);

      // Add the simulated time to the start date
      simulatedDate.current = new Date(
        startDateSim.getTime() + simulatedTime * 1000
      );
      // Update the display time
      setDisplayTime(
        simulatedDate.current.toLocaleString(undefined, {
          day: "2-digit",
          month: "2-digit",
          year: "numeric",
          hour: "2-digit",
          minute: "2-digit",
          second: "2-digit",
          hour12: false,
        })
      );

      // Stop the simulation after the total simulated seconds have passed
      if (simulatedTime >= totalSimulatedSeconds) {
        // console.log("simulatedTime", simulatedTime);
        // console.log("totalSimulatedSeconds", totalSimulatedSeconds);
        setStartSimulation(false);
        setSimulationTerminated(true);
        const peruTime = new Date().toLocaleTimeString("en-US", {
          timeZone: "America/Lima",
        });
        console.log(`Simulation stopped at ${peruTime} Peru time`);
        console.log("display time: ", displayTime);
        setSimulationEnd(true);
        fetchSimulationSummary();
      }
    };

    // Call updateSimulatedTime every second
    const intervalId = setInterval(updateSimulatedTime, 100 / speedFactor);

    // Clean up on unmount
    return () => {
      clearInterval(intervalId);
    };
  }, [startSimulation, simulationTerminated]);

  const fetchSimulationSummary = async () => {
    console.log("Fetching simulation summary");
    try {
      const response = await fetch(`${process.env.BACKEND_URL}reporte`);
      if (response.ok) {
        const summary = await response.json();
        setSimulationSummary(summary);
      } else {
        console.error("Error fetching simulation summary");
      }
    } catch (error) {
      console.error("Error fetching simulation summary:", error);
    } finally {
      try {
        const response = await fetch(`${process.env.BACKEND_URL}limpiar`);
        if (response.ok) {
          console.log("Simulation data cleared");
        } else {
          console.error("Error clearing simulation data");
        }
      } catch (error) {
        console.error("Error clearing simulation data:", error);
      }
    }
  };

  const Map = useMemo(
    () =>
      dynamic(() => import("../components/map/Map"), {
        loading: () => <p>A map is loading</p>,
        ssr: false,
      }),
    []
  );

  const handleApplyConfiguration = () => {
    //console.log(vuelos);
    setShowModal(false);
    setStartSimulation(true);
    setLoading(false);
    setDisplayTime("");
    setSimulationTerminated(false);
    console.log("Simulation started");
  };

  const handleEndSimulation = () => {
    setSimulationEnd(false);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleSearch = (id: string) => {
    // Buscar el paquete por ID
    if (simulationTerminated) return;

    const foundVuelo = vuelos.current.find((vuelo) =>
      vuelo.paquetes.some((paquete) => paquete.id === id , vuelo.enAire === true)
    );
    if (foundVuelo) {
      console.log("Paquete encontrado en avión:", foundVuelo);
      const { aeropuertoOrigen } = foundVuelo;
      const city = citiesByCode[aeropuertoOrigen];
      if (city) {
        setMapCenter([city.coords.lat, city.coords.lng]);
        setHighlightedPlaneId(foundVuelo.idVuelo);
        setSelectedPlaneId(foundVuelo.idVuelo); // Selecciona el avión encontrado
        setForceOpenPopup(true);
        setSelectedPackageId(id);
        setSelectedAirport(null);
        setErrorMessage("");
        return; // Salir de la función si se encuentra el paquete en un avión
      }
    }

    // Si no se encuentra en los aviones, buscar en los aeropuertos
    const foundAirport = airports.current.find((airport) =>
      airport.almacen.paquetes.some((paquete) => paquete.id === id)
    );
    if (foundAirport) {
      const city = citiesByCode[foundAirport.codigoIATA];
      if (city) {
        setMapCenter([city.coords.lat, city.coords.lng]);
        setHighlightedPlaneId(null); // No hay un avión específico
        setForceOpenPopup(true); // Forzar abrir el popup para el aeropuerto
        setSelectedPackageId(id);
        setSelectedAirport(foundAirport);
        setErrorMessage("");
        return;
      }
    }

    setErrorMessage("ID de paquete no encontrado");
  };

  const handleEnvioSearch = (id: string) => {
    console.log("Buscando envío con ID:", id);

    if (simulationTerminated) return;

    const matchingPackages: any = [];

    const filteredVuelos = vuelos.current.filter(
      (vuelo) => vuelo.enAire === true
    );

    filteredVuelos.forEach((vuelo) => {
      const foundPackages = vuelo.paquetes.filter((paquete) =>
        paquete.id.startsWith(`${id}-`)
      );
      matchingPackages.push(...foundPackages);
    });

    airports.current.forEach((airport) => {
      const foundPackages = airport.almacen.paquetes.filter((paquete) =>
        paquete.id.startsWith(`${id}-`)
      );
      matchingPackages.push(...foundPackages);
    });

    if (matchingPackages.length > 0) {
      // Assuming you have a way to handle the found packages
      // For example, setting them in a state, or processing them further
      console.log("Found packages:", matchingPackages);
      setEnvioFound(matchingPackages);
      setShowEnvioDetails(true);
      // setFoundPackages(matchingPackages); // Example: Update state or handle found packages
    } else {
      setErrorMessage("ID de envío no encontrado");
    }
    return;
  };


  const handleCloseEnvioDetails = () => {
    setShowEnvioDetails(false);
    setEnvioFound(null);
  };

  const handleVueloSearch = (id: string) => {
    console.log("Buscando vuelo con ID:", id);

    if (simulationTerminated) return;

    const foundVuelo = vuelos.current.find((vuelo) => vuelo.idVuelo === id);

    if (foundVuelo) {
      console.log("Vuelo encontrado:", foundVuelo);
      const { aeropuertoOrigen } = foundVuelo;
      const city = citiesByCode[aeropuertoOrigen];
      if (city) {
        setMapCenter([city.coords.lat, city.coords.lng]);
        setHighlightedPlaneId(foundVuelo.idVuelo);
        setSelectedPlaneId(foundVuelo.idVuelo); // Selecciona el avión encontrado
        setForceOpenPopup(true);
        setSelectedPackageId(null); // Deselecciona cualquier paquete
        setSelectedAirport(null); // Deselecciona cualquier aeropuerto
        setErrorMessage("");
        return;
      }
    }

    setErrorMessage("ID de vuelo no encontrado");

  

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar
        onSearch={handleSearch}
        envioSearch={handleEnvioSearch}
        vueloSearch={handleVueloSearch}
        errorMessage={errorMessage}
      />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <div
          style={{
            display: "flex",
            flex: 1,
            position: "relative",
            overflow: "hidden",
          }}
        >
          <Map
            planes={startSimulation ? vuelos : { current: [] }}
            airports={startSimulation ? airports : { current: [] }}
            startTime={startTime}
            startDate={startDate}
            startHour={startHour}
            speedFactor={speedFactor}
            startSimulation={startSimulation}
            dayToDay={dayToDay}
            mapCenter={mapCenter}
            highlightedPlaneId={highlightedPlaneId}
            forceOpenPopup={forceOpenPopup}
            selectedPackageId={selectedPackageId}
            setForceOpenPopup={setForceOpenPopup}
            airportsHistory={airportsHistory}
            showMoreInfo={showMoreInfo}
            setShowMoreInfo={setShowMoreInfo}
            vuelosInAir={vuelosInAir}
            selectedPlaneId={selectedPlaneId} 
            setSelectedPlaneId={setSelectedPlaneId} 
          />
          {/* Contenedor para el tiempo simulado */}
          {startSimulation && (
            <div className="simulated-time-container">
              Fecha de simulación: {displayTime}
            </div>
          )}
          {errorMessage && (
            <Notification
              message={errorMessage}
              onClose={() => setErrorMessage("")}
            />
          )}
          {showModal && (
            <ConfigurationModal
              onApply={handleApplyConfiguration}
              onClose={handleCloseModal}
              startDate={startDate}
              setStartDate={setStartDate}
              startTime={startHour}
              setStartTime={setStartHour}
              simulationMode={simulationMode}
              setSimulationMode={setSimulationMode}
              vuelos={vuelos}
              airports={airports}
              loading={loading}
              setLoading={setLoading}
              isMounted={isMounted}
              airportsHistory={airportsHistory}
            />
          )}{" "}
          {simulationEnd && simulationSummary && (
            <EndModal
              onClose={handleEndSimulation}
              simulatedStartDate={startDate}
              simulatedStartHour={startHour}
              simulatedEndDate={displayTime}
              summary={simulationSummary}
            />
          )}
        </div>
        {/* Botón para reabrir el ConfigurationModal, visible solo cuando no está en simulación */}
        {!startSimulation && !showModal && (
          <button
            className="open-config-modal-button"
            onClick={() => setShowModal(true)}
          >
            Configuración
          </button>
        )}
      </div>
      {showMoreInfo && (
        <MoreInfo
          onClose={() => setShowMoreInfo(false)}
          planes={startSimulation ? vuelos : { current: [] }}
          airports={startSimulation ? airports : { current: [] }}
          startTime={startTime}
          startDate={startDate}
          startHour={startHour}
          speedFactor={speedFactor}
          startSimulation={startSimulation}
          dayToDay={dayToDay}
          vuelosInAir={vuelosInAir}
        />
      )}
      {showEnvioDetails && (
        <EnvioDetails
          paquetes={envioFound || []}
          onClose={handleCloseEnvioDetails}
        />
      )}
    </div>
  );
};

export default Simulation;
