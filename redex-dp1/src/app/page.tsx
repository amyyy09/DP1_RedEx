"use client";

import React, { useMemo, useState, useEffect } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import CurrentTimeDisplay from "./components/map/CurrentTimeDisplay";
import Notification from "./components/notificacion/Notification";
import { Vuelo } from "./types/Planes";
import "./styles/SimulatedTime.css";
import { start } from "repl";
import { citiesByCode } from "@/app/data/cities";
// Datos hardcodeados para pruebas
const hardcodedVuelos: Vuelo[] = [
  {
    cantPaquetes: 50,
    capacidad: 180,
    status: 1,
    indexPlan: 2,
    horaSalida: [2024, 6, 16, 1, 14, 0], // Año, mes, día, hora, minuto, segundo
    horaLlegada: [2024, 6, 16, 15, 38, 0], // Año, mes, día, hora, minuto, segundo
    aeropuertoOrigen: "SBBR", // Código de ciudad de ejemplo
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
    horaSalida: [2024, 6, 16, 1, 14, 0], // Año, mes, día, hora, minuto, segundo
    horaLlegada: [2024, 6, 16, 15, 38, 0], // Año, mes, día, hora, minuto, segundo
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
  const [vuelos, setVuelos] = useState<Vuelo[]>(hardcodedVuelos);
  const [mapCenter, setMapCenter] = useState<[number, number] | null>(null);
  const [highlightedPlaneId, setHighlightedPlaneId] = useState<string | null>(null);
  const [selectedPackageId, setSelectedPackageId] = useState<string | null>(null);
  const [forceOpenPopup, setForceOpenPopup] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleSearch = (id: string) => {
    // Buscar el paquete por ID
    const foundVuelo = vuelos.find((vuelo) =>
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
    setVuelos(hardcodedVuelos); // Establece los vuelos hardcodeados al montar el componente
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
      <Topbar onSearch={handleSearch} errorMessage={errorMessage} />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <Map
          planes={{ current: vuelos }} // Pasa los vuelos hardcodeados directamente
          startTime={{ current: Date.now() }} // Asigna un tiempo de inicio ficticio
          startDate={new Date().toISOString().split("T")[0]} // Asigna la fecha actual
          startHour={new Date().toTimeString().split(" ")[0].substring(0, 5)} // Asigna la hora actual
          speedFactor={1} // Supone 1 como un marcador de posición, ajustar según sea necesario
          startSimulation={true} // Siempre inicia la simulación
          mapCenter={mapCenter} // Pasa el centro del mapa actualizado
          highlightedPlaneId={highlightedPlaneId} // Pasa el ID del avión resaltado
          selectedPackageId={selectedPackageId} // Pasa el ID del paquete seleccionado
          forceOpenPopup={forceOpenPopup}
          setForceOpenPopup={setForceOpenPopup}
        />
        <CurrentTimeDisplay /> {/* Añade el componente de visualización de la hora */}
      </div>
      {errorMessage && (
        <Notification message={errorMessage} onClose={() => setErrorMessage("")} />
      )}
    </div>
  );
};

export default DayToDay;