// pages/home/page.tsx
"use client";

import React, { useMemo, useState, useEffect, useRef } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import { Vuelo } from "./types/Planes";
import "./styles/SimulatedTime.css";

const Home = () => {
    const [showModal, setShowModal] = useState(true);
    const [startSimulation, setStartSimulation] = useState(false);
    const startTime = useRef(0);
    const [simulationMode, setSimulationMode] = useState("");
    const [startDate, setStartDate] = useState("");
    const [startHour, setStartHour] = useState("");
    const vuelos = useRef<Vuelo[]>([]);
    const [loading, setLoading] = useState(false);

    const speedFactor = 288; // Real-time seconds per simulated second
    

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
                    method: 'POST', // o 'GET' según sea necesario
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

                const vuelosIds = new Set(vuelos.current?.map((vuelo: Vuelo) => vuelo.idVuelo));
                responseData.forEach((data: Vuelo) => {
                  // Check if the idVuelo of data is already in vuelosIds
                  if (!vuelosIds.has(data.idVuelo)) {
                    // If it's not in vuelosIds, add it to vuelos.current and vuelosIds
                    vuelos.current?.push(data);
                    vuelosIds.add(data.idVuelo);
                  }
                  else{
                    // If it's in vuelosIds, update the Vuelo in vuelos.current
                    const index = vuelos.current?.findIndex((vuelo: Vuelo) => vuelo.idVuelo === data.idVuelo);
                    if (index && index !== -1) {
                      vuelos.current?.splice(index, 1, data);
                    }
                  }
                });
    
                console.log("Vuelos:", vuelos.current);
                // Aquí puedes procesar los datos y actualizar el estado según sea necesario
            } catch (error) {
                console.error('Error fetching API:', error);
            }
        };

        fetchApi(); // Llamada inicial al montar el componente

        const intervalId = setInterval(fetchApi, 20 * 60 * 1000); // Llama a fetchApi cada 20 minutos

        return () => clearInterval(intervalId); // Limpia el intervalo cuando el componente se desmonta
    }, []);

    const Map = useMemo(
        () =>
          dynamic(() => import("./components/map/Map"), {
            loading: () => <p>A map is loading</p>,
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
              planes={startSimulation ? vuelos : { current: [] }}
              startTime={startTime}
              startDate={startDate}
              startHour={startHour}
              speedFactor={speedFactor}
              startSimulation={startSimulation}
            />
          </div>
        </div>
      );
};

export default Home;
