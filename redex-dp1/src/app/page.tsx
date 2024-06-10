"use client";

import React, { useMemo, useState, useEffect, useRef } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import { Vuelo } from "./types/Planes";
import "./styles/SimulatedTime.css";
import "./styles/PlanningButtons.css"; // Add this line to import the new CSS file

const DayToDay: React.FC = () => {
    const [startPlanning, setStartPlanning] = useState(false);
    const startTime = useRef<number>(0);
    const [startDate, setStartDate] = useState("");
    const [startHour, setStartHour] = useState("");
    const vuelos = useRef<Vuelo[]>([]);
    const [loading, setLoading] = useState(false);

    const Map = useMemo(
        () =>
          dynamic(() => import("./components/map/Map"), {
            loading: () => <p>A map is loading</p>,
            ssr: false,
          }),
        []
    );

    const handleStartPlanning = () => {
        const now = new Date();
        startTime.current = now.getTime();
        setStartDate(now.toISOString().split("T")[0]);
        setStartHour(now.toTimeString().split(" ")[0].substring(0, 5));
        setStartPlanning(true);
        console.log("Planning started at", now);
    };

    const handleStopPlanning = () => {
        setStartPlanning(false);
        console.log("Planning stopped");
    };

    const formatDateTime = (date: Date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      const seconds = String(date.getSeconds()).padStart(2, "0");
      return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
    };

    useEffect(() => {
        console.log("startday", startDate, "starthour", startHour, "starttime", startTime, "startplanning", startPlanning)
        if (!startPlanning) return;

        const fetchApi = async () => {
            const now = new Date();
            const formattedDate = formatDateTime(now);

            const data = {
                fechahora: formattedDate,
                aeropuertos: [],
                vuelos: [],
            };
            console.log('Request de dia a dia:', data);

            try {
                const response = await fetch('http://localhost:8080/api/pso', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data)
                });

                console.log('Response de dia a dia:', response);

                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }

                const responseData = await response.json();
                console.log("API Response:", responseData);

                const vuelosIds = new Set(vuelos.current.map((vuelo: Vuelo) => vuelo.idVuelo));
                responseData.forEach((data: Vuelo) => {
                  if (!vuelosIds.has(data.idVuelo)) {
                    vuelos.current.push(data);
                    vuelosIds.add(data.idVuelo);
                  } else {
                    const index = vuelos.current.findIndex((vuelo: Vuelo) => vuelo.idVuelo === data.idVuelo);
                    if (index !== -1) {
                      vuelos.current.splice(index, 1, data);
                    }
                  }
                });

                console.log("Vuelos:", vuelos.current);
            } catch (error) {
                console.error('Error fetching API:', error);
            }
        };

        fetchApi(); // Initial call

        const intervalId = setInterval(fetchApi, 20 * 60 * 1000); // Call fetchApi every 20 minutes

        return () => clearInterval(intervalId); // Cleanup interval on component unmount or planning stop
    }, [startPlanning]);

    return (
        <div style={{ display: "flex", flexDirection: "column" }}>
            <Topbar />
            <div style={{ display: "flex", flex: 1 }}>
                <Sidebar />
                <Map
                    planes={startPlanning ? vuelos : { current: [] }}
                    startTime={startTime}
                    startDate={startDate}
                    startHour={startHour}
                    speedFactor={1} // Assuming 1 as a placeholder, adjust as necessary
                    startSimulation={startPlanning}
                />
                <div className="simulated-time-container">
                    {startPlanning ? 'Planificación en curso...' : 'Planificación detenida'}
                </div>
                <div className="planning-buttons-container">
                    <button className="planning-button" onClick={handleStartPlanning}>Planificar</button>
                    <button className="planning-button" onClick={handleStopPlanning}>Parar Planificador</button>
                </div>
            </div>
        </div>
    );
};

export default DayToDay;
