"use client";

import React, { useMemo, useState, useEffect, useRef, use } from "react";
import dynamic from "next/dynamic";
import Topbar from "../components/layout/Topbar";
import Sidebar from "../components/layout/Sidebar";
import ConfigurationModal from "../components/map/ConfigurationModal";
import { Vuelo } from "../types/Planes";
import EndModal from "../components/modal/EndModal";
import { citiesByCode } from "../data/cities";
import "../styles/SimulatedTime.css";

const Simulation: React.FC = () => {
  const [showModal, setShowModal] = useState(true);
  const [startSimulation, setStartSimulation] = useState(false);
  const startTime = useRef(0);
  const [simulationMode, setSimulationMode] = useState("");
  const [startDate, setStartDate] = useState("");
  const [startHour, setStartHour] = useState("");
  const vuelos = useRef<Vuelo[]>([]);
  const [loading, setLoading] = useState(false);
  const simulatedDate = useRef(new Date());
  const [simulationEnd, setSimulationEnd] = useState(false);

  const speedFactor = 288; // Real-time seconds per simulated second
  const totalSimulatedSeconds = 7 * 24 * 60 * 60; // One week in seconds
  const dayToDay = false;

  // State to store the display time
  const [displayTime, setDisplayTime] = useState("");

  // States for map center and highlighted plane ID
  const [mapCenter, setMapCenter] = useState<[number, number] | null>(null);
  const [highlightedPlaneId, setHighlightedPlaneId] = useState<string | null>(null);
  const [forceOpenPopup, setForceOpenPopup] = useState(false);
  const [selectedPackageId, setSelectedPackageId] = useState<string | null>(null);

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
      if (!startSimulation) return;

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
        const peruTime = new Date().toLocaleTimeString("en-US", {
          timeZone: "America/Lima",
        });
        console.log(`Simulation stopped at ${peruTime} Peru time`);
        setSimulationEnd(true);
      }
    };

    // Call updateSimulatedTime every second
    const intervalId = setInterval(updateSimulatedTime, 100 / speedFactor);

    // Clean up on unmount
    return () => {
      clearInterval(intervalId);
    };
  }, [startSimulation]);

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
    const foundVuelo = vuelos.current.find((vuelo) =>
      vuelo.paquetes.some((paquete) => paquete.id === id)
    );
    if (foundVuelo) {
      const { aeropuertoOrigen } = foundVuelo;
      const city = citiesByCode[aeropuertoOrigen];
      if (city) {
        setMapCenter([city.coords.lat, city.coords.lng]);
        setHighlightedPlaneId(foundVuelo.idVuelo);
        setForceOpenPopup(true);
        setSelectedPackageId(id);
      }
    }
  };

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar onSearch={handleSearch} />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <div style={{ display: "flex", flex: 1, position: "relative", overflow: "hidden" }}>
          <Map
            planes={startSimulation ? vuelos : { current: [] }}
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
          />
          {/* Contenedor para el tiempo simulado */}
          {startSimulation && (
            <div className="simulated-time-container">
              Fecha de simulación: {displayTime}
            </div>
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
              loading={loading}
              setLoading={setLoading}
              isMounted={isMounted}
            />
          )}{" "}
          {simulationEnd && (
            <EndModal
              onClose={handleEndSimulation}
              simulatedStartDate={startDate}
              simulatedStartHour={startHour}
              simulatedEndDate={displayTime}
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
    </div>
  );
};

export default Simulation;
