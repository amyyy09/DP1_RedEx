"use client";

import React, { useMemo, useState, useEffect, useRef } from "react";
import dynamic from "next/dynamic";
import Topbar from "../app/components/layout/Topbar";
import Sidebar from "../app/components/layout/Sidebar";
import ConfigurationModal from "../app/components/map/ConfigurationModal";
import { flightPlans } from "../app/data/flightPlans";
import { PlaneProps } from "./types/Planes";
import { timeToMinutes, minutesToTime } from "./utils/timeHelper";
import { start } from "repl";

const Home: React.FC = () => {
  const [planes, setPlanes] = useState<PlaneProps[]>(flightPlans);
  const [showModal, setShowModal] = useState(true);
  const [startSimulation, setStartSimulation] = useState(false);
  const controlClock = useRef(new Date()); // Initialize with the current date and time
  const startTime = useRef(0);
  const [simulationMode, setSimulationMode] = useState("");
  const [startDate, setStartDate] = useState("");
  const [startHour, setStartHour] = useState("");
  const [vuelos, setVuelos] = useState<Vuelo[]>([]);

  const speedFactor = 288; // Real-time seconds per simulated second
  const totalSimulatedSeconds = 7 * 24 * 60 * 60; // One week in seconds

  // State to store the display time
  const [displayTime, setDisplayTime] = useState("");

  useEffect(() => {
    if (!startSimulation) return;

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
      // startDate.setHours(0, 0, 0, 0); // Set the time to 00:00:00

      // Add the simulated time to the start date
      const simulatedDate = new Date(startDateSim.getTime() + simulatedTime * 1000);
      controlClock.current = simulatedDate;

      // Wrap around every 24 hours
      const simulatedMinutes = (simulatedDate.getHours() * 60 + simulatedDate.getMinutes()) % (24 * 60);

      // Update the display time
      setDisplayTime(simulatedDate.toLocaleString(undefined, { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false }));

      // Stop the simulation after the total simulated seconds have passed
      if (simulatedTime >= totalSimulatedSeconds) {
        // console.log("simulatedTime", simulatedTime);
        // console.log("totalSimulatedSeconds", totalSimulatedSeconds);
        setStartSimulation(false);
        const peruTime = new Date().toLocaleTimeString("en-US", {
          timeZone: "America/Lima",
        });
        console.log(`Simulation stopped at ${peruTime} Peru time`);
      }
    };

    // Call updateSimulatedTime every second
    const intervalId = setInterval(
      updateSimulatedTime,
      500/speedFactor
    );

    // Clean up on unmount
    return () => {
      clearInterval(intervalId);
    };
  }, [startSimulation]);

  useEffect(() => {
    console.log("PLANES HAVE CHANGED!");
  }, [planes]);

  const Map = useMemo(
    () =>
      dynamic(() => import("../app/components/map/Map"), {
        loading: () => <p>A map is loading</p>,
        ssr: false,
      }),
    []
  );

  const handleApplyConfiguration = () => {
    setShowModal(false);
    setStartSimulation(true);
    // console.log("Simulation started");
  };

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <Map
          planes={startSimulation ? planes : []}
          startTime={startTime}
          startDate={startDate}
          startHour={startHour}
          speedFactor={speedFactor}
        />
        <p>Simulated time: {displayTime}</p>
        {/* Pass planes only if simulation starts */}
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
            setVuelos={setVuelos}
          />
        )}{" "}
        {/* Show modal */}
      </div>
    </div>
  );
};

export default Home;
