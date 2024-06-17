"use client";

import React, { useMemo, useContext, useEffect, useState } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import CurrentTimeDisplay from "./components/map/CurrentTimeDisplay";
import Notification from "./components/notificacion/Notification";
import { Vuelo } from "./types/Planes";
import "./styles/SimulatedTime.css";
import { OperationContext } from "./context/operation-provider";
import { citiesByCode } from "@/app/data/cities";
// Datos hardcodeados para pruebas
const hardcodedVuelos: Vuelo[] = [
  {
    cantPaquetes: 50,
    capacidad: 180,
    status: 1,

    indexPlan: 2,
    horaSalida: [2024, 6, 16, 16, 14, 0], // Año, mes, día, hora, minuto, segundo
    horaLlegada: [2024, 6, 17, 22, 38, 0], // Año, mes, día, hora, minuto, segundo
    aeropuertoOrigen: "SPIM", // Código de ciudad de ejemplo
    aeropuertoDestino: "SGAS", // Código de ciudad de ejemplo
    paquetes: [
      {
        status: 0,
        id: "000000004",
      },
      {
        status: 0,
        id: "000000005",
      },
      {
        status: 0,
        id: "000000006",
      },
      {
        status: 0,
        id: "000000007",
      },
      {
        status: 0,
        id: "000000008",
      },
      {
        status: 0,
        id: "000000009",
      },
      {
        status: 0,
        id: "000000010",
      },
    ],
    idVuelo: "247-2024-06-12",

  },
  {
    cantPaquetes: 50,
    capacidad: 180,
    status: 1,
    indexPlan: 2,
    horaSalida: [2024, 6, 16, 16, 14, 0], // Año, mes, día, hora, minuto, segundo
    horaLlegada: [2024, 6, 17, 23, 38, 0], // Año, mes, día, hora, minuto, segundo
    aeropuertoOrigen: "SPIM", // Código de ciudad de ejemplo
    aeropuertoDestino: "SLLP", // Código de ciudad de ejemplo
    paquetes: [
      {
        status: 0,
        id: "000000001",
      },
      {
        status: 0,
        id: "000000002",
      },
    ],
    idVuelo: "247-2024-06-11",
  },
];

const DayToDay: React.FC = () => {
  // const vuelos = useContext(OperationContext); // Obtiene los vuelos del contexto

  const [startSimulation, setStartSimulation] = useState(false); // Inicia la simulación
  const { flights, updateFlights } = useContext(OperationContext);
  const speedFactor = 1; // Factor de velocidad de la simulación
  const dayToDay = true; // Indica que se trata de una simulación de día a día
  const [mapCenter, setMapCenter] = useState<[number, number] | null>(null);
  const [highlightedPlaneId, setHighlightedPlaneId] = useState<string | null>(null);
  const [selectedPackageId, setSelectedPackageId] = useState<string | null>(null);
  const [forceOpenPopup, setForceOpenPopup] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState("");
 
  
  const handleSearch = (id: string) => {
    // Buscar el paquete por ID
    console.log("vuelos búsqueda",flights.current);
    const foundVuelo = flights.current.find((vuelo: { paquetes: any[]; }) =>
      vuelo.paquetes.some((paquete) => paquete.id === id)
    );
    if (foundVuelo) {
      const { aeropuertoOrigen } = foundVuelo;
      const city = citiesByCode[aeropuertoOrigen];
      if (city) {
        setMapCenter([city.coords.lat, city.coords.lng]);
        setHighlightedPlaneId(foundVuelo.idVuelo);
        setSelectedPackageId(id);
        setForceOpenPopup(true);
        setErrorMessage("");
      }
    } else {
      setErrorMessage("ID de paquete no existente");
    }
  };

  useEffect(() => {
    // setVuelos(hardcodedVuelos); // Establece los vuelos hardcodeados al montar el componente
    flights.current = hardcodedVuelos;
    console.log("flights inicio",flights);
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

  const addFlights = () => {
    // Add your flights here
    const newFlights : Vuelo[] = [
      {
        cantPaquetes: 75,
        capacidad: 200,
        status: 1,
        indexPlan: 1,
        horaSalida: [2024, 6, 16, 16, 29, 0], // Año, mes, día, hora, minuto, segundo
        horaLlegada: [2024, 6, 17, 15, 46, 0], // Año, mes, día, hora, minuto, segundo
        aeropuertoOrigen: "SPIM", // Código de ciudad de ejemplo
        aeropuertoDestino: "ZBAA", // Código de ciudad de ejemplo
        paquetes: [
          {
            status: 0,
            id: "000000003",
          },
          {
            status: 0,
            id: "000000011",
          },
        ],
        idVuelo: "626-2024-06-11",
      },
    ];

    flights.current.push(newFlights[0]);
    updateFlights();
    console.log("flights ",flights.current);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar onSearch={handleSearch} errorMessage={errorMessage} />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <Map
          planes={flights} // Pasa los vuelos hardcodeados directamente
          startTime={{ current: Date.now() }} // Asigna un tiempo de inicio ficticio
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
        />
        <CurrentTimeDisplay />{" "}
        {/* Añade el componente de visualización de la hora */}
        <button onClick={addFlights}>Add Flights</button>
      </div>
      {errorMessage && (
        <Notification message={errorMessage} onClose={() => setErrorMessage("")} />
      )}
    </div>
  );
};

export default DayToDay;
