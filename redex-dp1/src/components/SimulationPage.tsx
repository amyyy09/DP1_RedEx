"use client";

import React, { useMemo, useState, useEffect, useRef } from "react";
import dynamic from "next/dynamic";
import ConfigurationModal from "@/components/map/ConfigurationModal";
import { Vuelo } from "@/types/Planes";
import "@/styles/SimulatedTime.css";
import SimulatedTimeContainer from "./map/SimulatedTimeContainer";

const SimulationPage: React.FC = () => {
  const [showModal, setShowModal] = useState(true);
  const [startSimulation, setStartSimulation] = useState(false);
  const startTime = useRef(0);
  const [simulationMode, setSimulationMode] = useState("");
  const [startDate, setStartDate] = useState("");
  const [startHour, setStartHour] = useState("");
  const vuelos = useRef<Vuelo[]>([]);
  const [loading, setLoading] = useState(false);
  const simulatedDate = useRef(new Date());

  const speedFactor = 288; // Real-time seconds per simulated second
  const totalSimulatedSeconds = 7 * 24 * 60 * 60; // One week in seconds

  // State to store the display time
  const [displayTime, setDisplayTime] = useState("");

  useEffect(() => {
    if (!startSimulation) return;

    console.log("Simulation started");

    // Set the start time
    startTime.current = Date.now();

    // Update the simulated time
    const updateSimulatedTime = () => {
      if (!startSimulation) return;

      const currentTime = Date.now();
      const elapsedTime = (currentTime - startTime.current) / 1000; // in seconds
      const simulatedTime = elapsedTime * speedFactor;
      // Create a new Date object for the start of the simulation
      const startDateSim = new Date(startDate + "T" + startHour + ":00");

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
        setStartSimulation(false);
        const peruTime = new Date().toLocaleTimeString("en-US", {
          timeZone: "America/Lima",
        });
        console.log(`Simulation stopped at ${peruTime} Peru time`);
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
      dynamic(() => import("@/components/map/Map"), {
        loading: () => <p>A map is loading</p>,
        ssr: false,
      }),
    []
  );

  const handleApplyConfiguration = () => {
    setShowModal(false);
    setStartSimulation(true);
    setLoading(false);
    console.log("Simulation started");
  };

  return (
    <div style={{ display: "flex", flex: 1 }}>
      <Map
        planes={startSimulation ? vuelos : { current: [] }}
        startTime={startTime}
        startDate={startDate}
        startHour={startHour}
        speedFactor={speedFactor}
        startSimulation={startSimulation}
      />
      {/* Contenedor para el tiempo simulado */}
      <SimulatedTimeContainer displayTime={displayTime} />
      {showModal && (
        <ConfigurationModal
          onApply={handleApplyConfiguration}
          startDate={startDate}
          setStartDate={setStartDate}
          startTime={startHour}
          setStartTime={setStartHour}
          simulationMode={simulationMode}
          setSimulationMode={setSimulationMode}
          vuelos={vuelos}
          loading={loading}
          setLoading={setLoading}
        />
      )}
    </div>
  );
};

export default SimulationPage;