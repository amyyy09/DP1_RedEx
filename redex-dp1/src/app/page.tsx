"use client";

import React, { useMemo, useState, useEffect } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import CurrentTimeDisplay from "./components/map/CurrentTimeDisplay";
import { Vuelo } from "./types/Planes";
import "./styles/SimulatedTime.css";
import { start } from "repl";

// Datos hardcodeados para pruebas
const hardcodedVuelos: Vuelo[] = [
  {
    cantPaquetes: 50,
    capacidad: 180,
    status: 1,
    indexPlan: 0,
    horaSalida: [2024, 6, 12, 6, 14, 0], // Año, mes, día, hora, minuto, segundo
    horaLlegada: [2024, 6, 12, 20, 38, 0], // Año, mes, día, hora, minuto, segundo
    aeropuertoOrigen: "SPIM", // Código de ciudad de ejemplo
    aeropuertoDestino: "RPLL", // Código de ciudad de ejemplo
    idVuelo: "247-2024-06-11",
  },
  {
    cantPaquetes: 75,
    capacidad: 200,
    status: 1,
    indexPlan: 1,
    horaSalida: [2024, 6, 12, 1, 10, 0], // Año, mes, día, hora, minuto, segundo
    horaLlegada: [2024, 6, 12, 15, 46, 0], // Año, mes, día, hora, minuto, segundo
    aeropuertoOrigen: "SPIM", // Código de ciudad de ejemplo
    aeropuertoDestino: "SLLP", // Código de ciudad de ejemplo
    idVuelo: "625-2024-06-11",
  },
];

const DayToDay: React.FC = () => {
  const [vuelos, setVuelos] = useState<Vuelo[]>(hardcodedVuelos);
  console.log(vuelos);
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
      <Topbar />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <Map
          planes={{ current: vuelos }} // Pasa los vuelos hardcodeados directamente
          startTime={{ current: Date.now() }} // Asigna un tiempo de inicio ficticio
          startDate={new Date().toISOString().split("T")[0]} // Asigna la fecha actual
          startHour={new Date().toTimeString().split(" ")[0].substring(0, 5)} // Asigna la hora actual
          speedFactor={1} // Supone 1 como un marcador de posición, ajustar según sea necesario
          startSimulation={true} // Siempre inicia la simulación
        />
        <CurrentTimeDisplay /> {/* Añade el componente de visualización de la hora */}
      </div>
    </div>
  );
};

export default DayToDay;