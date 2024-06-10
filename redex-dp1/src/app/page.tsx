"use client";

import React, { useMemo, useState, useEffect, useRef } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import CurrentTimeDisplay from "./components/map/CurrentTimeDisplay"; // Import the new component
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
    const intervalRef = useRef<NodeJS.Timeout | null>(null);
    const timeoutRef = useRef<NodeJS.Timeout | null>(null);
    const intervalTime = 20 * 60 * 1000; // 20 minutes

    const Map = useMemo(
        () =>
          dynamic(() => import("./components/map/Map"), {
            loading: () => <p>A map is loading...</p>,
            ssr: false,
          }),
        []
    );

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
            console.log("API Response Data:", responseData);

            const vuelosIds = new Set(vuelos.current.map((vuelo: Vuelo) => vuelo.idVuelo));
            responseData.forEach((data: Vuelo) => {
                if (!vuelosIds.has(data.idVuelo)) {
                    vuelos.current.push(data);
                    vuelosIds.add(data.idVuelo);
                } else {
                    const index = vuelos.current.findIndex((vuelo: Vuelo) => data.idVuelo === vuelo.idVuelo);
                    if (index !== -1) {
                        vuelos.current.splice(index, 1, data);
                    }
                }
            });

            // Save vuelos and last fetch time to local storage
            localStorage.setItem("vuelos", JSON.stringify(vuelos.current));
            localStorage.setItem("lastFetchTime", Date.now().toString());

            console.log("Vuelos after update:", vuelos.current);
        } catch (error) {
            console.error('Error fetching API:', error);
        }
    };

    const startInterval = () => {
        intervalRef.current = setInterval(fetchApi, intervalTime);
        console.log("Interval set with ID:", intervalRef.current);
    };

    const stopInterval = () => {
        if (intervalRef.current) {
            clearInterval(intervalRef.current);
            console.log("Interval cleared with ID:", intervalRef.current);
            intervalRef.current = null;
        }
    };

    useEffect(() => {
        // Load state from local storage
        const storedStartPlanning = localStorage.getItem("startPlanning");
        const storedStartTime = localStorage.getItem("startTime");
        const storedStartDate = localStorage.getItem("startDate");
        const storedStartHour = localStorage.getItem("startHour");
        const storedVuelos = localStorage.getItem("vuelos");
        const storedLastFetchTime = localStorage.getItem("lastFetchTime");

        if (storedStartPlanning === "true") {
            setStartPlanning(true);
            startTime.current = parseInt(storedStartTime || "0", 10);
            setStartDate(storedStartDate || "");
            setStartHour(storedStartHour || "");

            if (storedVuelos) {
                vuelos.current = JSON.parse(storedVuelos);
            }

            const now = Date.now();
            const lastFetchTime = storedLastFetchTime ? parseInt(storedLastFetchTime, 10) : now;
            const timeSinceLastFetch = now - lastFetchTime;

            if (timeSinceLastFetch < intervalTime) {
                timeoutRef.current = setTimeout(() => {
                    fetchApi();
                    startInterval();
                }, intervalTime - timeSinceLastFetch);
            } else {
                fetchApi();
                startInterval();
            }
        } else if (storedVuelos) {
            vuelos.current = JSON.parse(storedVuelos);
        }

        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible' && startPlanning) {
                const now = Date.now();
                const lastFetchTime = parseInt(localStorage.getItem("lastFetchTime") || "0", 10);
                const timeSinceLastFetch = now - lastFetchTime;

                if (timeSinceLastFetch >= intervalTime) {
                    fetchApi();
                    startInterval();
                } else {
                    timeoutRef.current = setTimeout(() => {
                        fetchApi();
                        startInterval();
                    }, intervalTime - timeSinceLastFetch);
                }
            } else {
                stopInterval();
                if (timeoutRef.current) {
                    clearTimeout(timeoutRef.current);
                    timeoutRef.current = null;
                }
            }
        };

        document.addEventListener('visibilitychange', handleVisibilityChange);

        return () => {
            document.removeEventListener('visibilitychange', handleVisibilityChange);
            stopInterval();
            if (timeoutRef.current) {
                clearTimeout(timeoutRef.current);
                timeoutRef.current = null;
            }
        };
    }, [startPlanning]);

    const handleStartPlanning = () => {
        console.log("vuelos.current", vuelos.current)
        const now = new Date();
        startTime.current = now.getTime();
        setStartDate(now.toISOString().split("T")[0]);
        setStartHour(now.toTimeString().split(" ")[0].substring(0, 5));
        setStartPlanning(true);

        // Save state to local storage
        localStorage.setItem("startPlanning", "true");
        localStorage.setItem("startTime", startTime.current.toString());
        localStorage.setItem("startDate", now.toISOString().split("T")[0]);
        localStorage.setItem("startHour", now.toTimeString().split(" ")[0].substring(0, 5));

        // Start the first fetch immediately and set interval
        fetchApi();
        startInterval();

        console.log("Planning started at", now);
    };

    const handleStopPlanning = () => {
        console.log("vuelos.current", vuelos.current)
        setStartPlanning(false);

        // Remove state from local storage
        localStorage.removeItem("startPlanning");
        localStorage.removeItem("startTime");
        localStorage.removeItem("startDate");
        localStorage.removeItem("startHour");
        localStorage.removeItem("lastFetchTime");

        // Clear the interval and timeout
        stopInterval();
        if (timeoutRef.current) {
            clearTimeout(timeoutRef.current);
            console.log("Timeout cleared with ID:", timeoutRef.current);
            timeoutRef.current = null;
        }

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

    return (
        <div style={{ display: "flex", flexDirection: "column" }}>
            <Topbar />
            <div style={{ display: "flex", flex: 1 }}>
                <Sidebar />
                <Map
                    planes={vuelos} // Pass the vuelos ref directly
                    startTime={startTime}
                    startDate={startDate}
                    startHour={startHour}
                    speedFactor={1} // Assuming 1 as a placeholder, adjust as necessary
                    startSimulation={startPlanning}
                />
                <CurrentTimeDisplay /> {/* Add the time display component */}
                <div className="planning-buttons-container">
                    <button
                        className="planning-button"
                        onClick={handleStartPlanning}
                        disabled={startPlanning}
                    >
                        Planificar
                    </button>
                    <button
                        className="planning-button"
                        onClick={handleStopPlanning}
                        disabled={!startPlanning}
                    >
                        Parar Planificador
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DayToDay;
