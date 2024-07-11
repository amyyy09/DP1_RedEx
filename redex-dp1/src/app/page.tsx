"use client";

import React, { useMemo, useContext, useEffect, useState } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import CurrentTimeDisplay from "./components/map/CurrentTimeDisplay";
import Notification from "./components/notificacion/Notification";
import "./styles/SimulatedTime.css";
import { OperationContext } from "./context/operation-provider";
import { citiesByCode } from "@/app/data/cities";
import { FlightPlan } from "@/app/types/FlightPlan ";
import { Paquete} from "@/app/types/envios";
import { Envio} from "@/app/types/envios";
import { Vuelo} from "@/app/types/Planes";
import { Airport} from "@/app/types/Planes";
import "./styles/popupPlanDeVuelo.css";
import MoreInfo from "./components/map/MoreInfo";
import EnvioDetails from "./components/map/EnvioDetails";

const DayToDay: React.FC = () => {
  // const vuelos = useContext(OperationContext); // Obtiene los vuelos del contexto

  const [startSimulation, setStartSimulation] = useState(false); // Inicia la simulación
  const [simulationEnd, setSimulationEnd] = useState(false);
  const {
    flights,
    updateFlights,
    startInterval,
    flightsOnAir,
    packages,
    airports,
    startTime,
  } = useContext(OperationContext);
  const speedFactor = 1; // Factor de velocidad de la simulación
  const dayToDay = true; // Indica que se trata de una simulación de día a día
  const [mapCenter, setMapCenter] = useState<[number, number] | null>(null);
  const [highlightedPlaneId, setHighlightedPlaneId] = useState<string | null>(
    null
  );
  const [selectedPackageId, setSelectedPackageId] = useState<string | null>(
    null
  );
  const [forceOpenPopup, setForceOpenPopup] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [flightPlan, setFlightPlan] = useState<FlightPlan[]>([]);
  const [showFlightPlanPopup, setShowFlightPlanPopup] = useState(false);
  const [showMoreInfo, setShowMoreInfo] = useState(false);
  const [selectedPlaneId, setSelectedPlaneId] = useState<string | null>(null);
  const [highlightedAirportCode, setHighlightedAirportCode] = useState<
    string | null
  >(null);
  const [selectedAirport, setSelectedAirport] = useState<any | null>(null);
  const [envioFound, setEnvioFound] = useState<any[] | null>(null);
  const [showEnvioDetails, setShowEnvioDetails] = useState(false);

  // const hardcodedPaquetes: Paquete[] = [
  //   {
  //     status: 3,
  //     horaInicio: [2024, 7, 10, 14, 54],
  //     aeropuertoOrigen: "SLLP",
  //     aeropuertoDestino: "VIDP",
  //     id: "SLLP000026962-1",
  //     ruta: "113;1616;2742",
  //     ubicacion: "113",
  //   },
  //   {
  //     status: 3,
  //     horaInicio: [2024, 7, 10, 14, 54],
  //     aeropuertoOrigen: "SLLP",
  //     aeropuertoDestino: "VIDP",
  //     id: "SLLP000026960-1",
  //     ruta: "113;1602;2740",
  //     ubicacion: "113",
  //   },
  // ];

  // const hardcodedEnvios: Envio[] = [
  //   {
  //     idEnvio: "ENV1",
  //     fechaHoraOrigen: "2024-07-10T10:00:00Z",
  //     zonaHorariaGMT: -5,
  //     codigoIATAOrigen: "SLLP",
  //     codigoIATADestino: "VIDP",
  //     cantPaquetes: 2,
  //     paquetes: hardcodedPaquetes,
  //   },
  // ];

  // const hardcodedAirports: Airport[] = [
  //   new Airport({
  //     codigoIATA: "SLLP",
  //     ciudad: "La Paz",
  //     pais: "Bolivia",
  //     continente: "South America",
  //     alias: "El Alto International Airport",
  //     zonaHorariaGMT: -4,
  //     almacen: {
  //       capacidad: 100,
  //       cantPaquetes: 2,
  //       paquetes: hardcodedPaquetes,
  //     },
  //     latitud: "-16.5133",
  //     longitud: "-68.1923",
  //   }),
  // ];

  // const hardcodedFlights: Vuelo[] = [
  //   new Vuelo({
  //     cantPaquetes: 2,
  //     capacidad: 180,
  //     status: 0,
  //     indexPlan: 113,
  //     horaSalida: [2024, 7, 10, 13, 4],
  //     horaLlegada: [2024, 7, 11, 4, 0],
  //     aeropuertoOrigen: "SLLP",
  //     aeropuertoDestino: "SBBR",
  //     paquetes: hardcodedPaquetes,
  //     idVuelo: "113-2024-07-10",
  //     enAire: true,
  //   }),
  // ];

  // useEffect(() => {
  //   // Asignar datos hardcodeados al contexto
  //   flights.current = hardcodedFlights;
  //   packages.current = hardcodedPaquetes;
  //   airports.current = hardcodedAirports;
    
  //   console.log("setInterval");
  //   console.log("flights inicio", flights);
  //   console.log("packages inicio", packages);
  //   console.log("airports inicio", airports);
  //   startInterval();
  //   setStartSimulation(true);
  // }, [startInterval]);

  const handleCloseEnvioDetails = () => {
    setShowEnvioDetails(false);
    setEnvioFound(null);
  };
  
  //quiero tener los vuelos hardcodeados de arriba
  //flights.current = hardcodedVuelos;
  const handleSearch = (id: string) => {
    console.log("Buscando paquete con ID:", id);

    const filteredVuelos = flights.current.filter(
      (vuelo: any) => vuelo.enAire === true
    );
    
    // Buscar el paquete en los vuelos
    const foundVuelo = filteredVuelos.find((vuelo: Vuelo) =>
      vuelo.paquetes.some((paquete) => paquete.id === id)
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

    // Si no se encuentra en los vuelos, buscar en los aeropuertos
    const foundAirport = airports.current.find((airport: Airport) =>
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
        setHighlightedAirportCode(foundAirport.codigoIATA); // Set highlighted airport code
        setErrorMessage("");
        return;
      }
    }

    setErrorMessage("ID de paquete no encontrado");
  };

  const handleEnvioSearch = async (id: string) => {
    console.log("Buscando envío con ID:", id);
    const matchingPackages: any = [];

    const filteredVuelos = flights.current.filter(
      (vuelo: any) => vuelo.enAire === true
    );

    filteredVuelos.forEach((vuelo: any) => {
      vuelo.paquetes.forEach((paquete: any) => {
        if (paquete.id.startsWith(`${id}-`)) {
          paquete.ubicacion = vuelo.indexPlan.toString();
          matchingPackages.push(paquete);
        }
      });
    });

    airports.current.forEach((airport: any) => {
      const foundPackages = airport.almacen.paquetes.filter(
        (paquete: any) =>
          paquete.id.startsWith(`${id}`) &&
          !matchingPackages.some(
            (existingPaquete: any) => existingPaquete.id === paquete.id
          )
      );
      matchingPackages.push(...foundPackages);
    });

    packages.current.forEach((paquete: any) => {
      if (paquete.id.startsWith(`${id}-`)) {
        matchingPackages.push(paquete);
      }
    });

    //check for repeated packages and drop the ones with paquete.ubicacion === paquete.origen
    const filteredPackages = matchingPackages.reduce(
      (acc: any, paquete: any) => {
        const isDuplicate = acc.some(
          (existingPaquete: any) => existingPaquete.id === paquete.id
        );
        if (
          !isDuplicate ||
          (isDuplicate && paquete.ubicacion !== paquete.aeropuertoOrigen)
        ) {
          acc.push(paquete);
        }
        return acc;
      },
      []
    );

    console.log("paquetes", packages.current);

    if (matchingPackages.length > 0) {
      // Assuming you have a way to handle the found packages
      // For example, setting them in a state, or processing them further
      console.log("Found packages:", filteredPackages);
      setEnvioFound(matchingPackages);
      setShowEnvioDetails(true);
      // setFoundPackages(matchingPackages); // Example: Update state or handle found packages
    } else {
      setErrorMessage("ID de envío no encontrado");
    }
    return;
  };

  const handleVueloSearch = async (id: number) => {
    console.log("Buscando vuelo con ID:", id);
    // console.log("vuelos búsqueda", flights.current);

    if (flights.current.length === 0) {
      setErrorMessage("No hay vuelos disponibles");
      return;
    }

    const foundVuelo = flights.current.find((vuelo: { indexPlan : number }) =>
      vuelo.indexPlan === id
    );

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
    else{
      setErrorMessage("Vuelo no encontrado");
    }
    
    return;
  };

  const handleAlmacenSearch = async (id: string) => {
    console.log("Buscando almacén con ID:", id);
    const foundAirport = airports.current.find((airport: { ciudad: string }) =>
      airport.ciudad === id
    );

    if (foundAirport) {
      console.log("Almacén encontrado:", foundAirport);
      const city = citiesByCode[foundAirport.codigoIATA];
      if (city) {
        setMapCenter([city.coords.lat, city.coords.lng]);
        setHighlightedPlaneId(null);
        setSelectedPlaneId(null);
        setForceOpenPopup(true);
        setSelectedPackageId(null);
        setSelectedAirport(foundAirport);
        setHighlightedAirportCode(foundAirport.codigoIATA);
        setErrorMessage("");
        return;
      }
    } else {
      setErrorMessage("Almacén no encontrado");
    }
  };

  useEffect(() => {
    // setVuelos(hardcodedVuelos); // Establece los vuelos hardcodeados al montar el componente
    //flights.current = hardcodedVuelos;
    // console.log("flights inicio", flights);
    console.log("setInterval");
    startInterval(); // Inicia el intervalo de actualización
    setStartSimulation(true); // Inicia la simulación al montar el componente
  }, []);

  const Map = useMemo(
    () =>
      dynamic(() => import("./components/map/Map"), {
        loading: () => <p>A map is loading...</p>,
        ssr: false,
      }),
    []
  );

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar
        onSearch={handleSearch}
        envioSearch={handleEnvioSearch}
        vueloSearch={handleVueloSearch}
        almacenSearch={handleAlmacenSearch}
        errorMessage={errorMessage}
      />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <Map
          planes={flights} // Pasa los vuelos hardcodeados directamente
          airports={airports}
          startTime={startTime} // Asigna un tiempo de inicio ficticio
          startDate={""} // Asigna la fecha actual
          startHour={""} // Asigna la hora actual
          speedFactor={speedFactor} // Supone 1 como un marcador de posición, ajustar según sea necesario
          startSimulation={startSimulation} // Siempre inicia la simulación
          dayToDay={dayToDay} // Indica que se trata de una simulación de día a día
          mapCenter={mapCenter} // Pasa el centro del mapa actualizado
          highlightedPlaneId={highlightedPlaneId} // Pasa el ID del avión resaltado
          selectedPackageId={selectedPackageId} // Pasa el ID del paquete seleccionado
          forceOpenPopup={forceOpenPopup}
          setForceOpenPopup={setForceOpenPopup}
          showMoreInfo={showMoreInfo}
          setShowMoreInfo={setShowMoreInfo}
          vuelosInAir={flightsOnAir}
          selectedPlaneId={selectedPlaneId}
          setSelectedPlaneId={setSelectedPlaneId}
          paquetes={packages}
          highlightedAirportCode={highlightedAirportCode}
          setHighlightedAirportCode={setHighlightedAirportCode}
          setStartSimulation={setStartSimulation}
          setSimulationEnd={setSimulationEnd}
        />
        {startSimulation && <CurrentTimeDisplay startTime={startTime.current} />}
        <div style={{ display: "flow" }}>
          {showMoreInfo && (
            <MoreInfo
              onClose={() => setShowMoreInfo(false)}
              planes={flights}
              airports={airports}
              startTime={{ current: Date.now() }}
              startDate={""}
              startHour={""}
              speedFactor={speedFactor}
              startSimulation={startSimulation}
              dayToDay={dayToDay}
              vuelosInAir={flightsOnAir}
            />
          )}
        </div>
      </div>
      {errorMessage && (
        <Notification
          message={errorMessage}
          onClose={() => setErrorMessage("")}
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

export default DayToDay;
