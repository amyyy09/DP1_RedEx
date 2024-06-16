"use client";

import React, { useMemo, useContext, useEffect, useState } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import CurrentTimeDisplay from "./components/map/CurrentTimeDisplay";
import { Vuelo } from "./types/Planes";
import "./styles/SimulatedTime.css";
import { OperationContext } from "./context/operation-provider";

// Datos hardcodeados para pruebas
const hardcodedVuelos: Vuelo[] = [
  {
    cantPaquetes: 50,
    capacidad: 180,
    status: 1,
    indexPlan: 0,
    horaSalida: [2024, 6, 16, 9, 30, 0], // Año, mes, día, hora, minuto, segundo
    horaLlegada: [2024, 6, 17, 20, 38, 0], // Año, mes, día, hora, minuto, segundo
    aeropuertoOrigen: "SPIM", // Código de ciudad de ejemplo
    aeropuertoDestino: "RPLL", // Código de ciudad de ejemplo
    idVuelo: "247-2024-06-11",
  },
  {
    cantPaquetes: 75,
    capacidad: 200,
    status: 1,
    indexPlan: 1,
    horaSalida: [2024, 6, 16, 9, 29, 0], // Año, mes, día, hora, minuto, segundo
    horaLlegada: [2024, 6, 17, 15, 46, 0], // Año, mes, día, hora, minuto, segundo
    aeropuertoOrigen: "SPIM", // Código de ciudad de ejemplo
    aeropuertoDestino: "LKPR", // Código de ciudad de ejemplo
    idVuelo: "625-2024-06-11",
  },
];

const DayToDay: React.FC = () => {
  // const vuelos = useContext(OperationContext); // Obtiene los vuelos del contexto

  const [startSimulation, setStartSimulation] = useState(false); // Inicia la simulación
  const { flights, updateFlights } = useContext(OperationContext);
  const speedFactor = 1; // Factor de velocidad de la simulación
  const dayToDay = true; // Indica que se trata de una simulación de día a día

  // console.log(flights.current);
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
        horaSalida: [2024, 6, 16, 9, 29, 0], // Año, mes, día, hora, minuto, segundo
        horaLlegada: [2024, 6, 16, 15, 46, 0], // Año, mes, día, hora, minuto, segundo
        aeropuertoOrigen: "SPIM", // Código de ciudad de ejemplo
        aeropuertoDestino: "SLLP", // Código de ciudad de ejemplo
        idVuelo: "626-2024-06-11",
      },
    ];

    flights.current.push(newFlights[0]);
    updateFlights();
    console.log("flights ",flights.current);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar />
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
        />
        <CurrentTimeDisplay />{" "}
        {/* Añade el componente de visualización de la hora */}
        {/* <button onClick={addFlights}>Add Flights</button> */}
      </div>
    </div>
  );
};

export default DayToDay;
