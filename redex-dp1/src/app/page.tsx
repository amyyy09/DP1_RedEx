"use client";

import React, { useMemo, useState, useEffect } from "react";
import dynamic from "next/dynamic";
import Topbar from "../app/components/layout/Topbar";
import Sidebar from "../app/components/layout/Sidebar";
import ConfigurationModal from "../app/components/map/ConfigurationModal";
import { flightPlans } from "../app/data/flightPlans";
import { PlaneProps } from "./types/Planes";
import { timeToMinutes, minutesToTime } from "./utils/timeHelper";

const Home: React.FC = () => {
  const [planes, setPlanes] = useState<PlaneProps[]>(flightPlans);
  const [showModal, setShowModal] = useState(true);
  const [startSimulation, setStartSimulation] = useState(false);
  const [controlClock, setControlClock] = useState(0); // Control clock in minutes

  const speedFactor = 252; 
  const interval = 604800  / speedFactor;

  // Update control clock every real-time second
  useEffect(() => {
    if (!startSimulation) return; // Don't start the clock if startSimulation is not true

    // Convert to Peru time
    const peruTime = new Date().toLocaleTimeString('en-US', { timeZone: 'America/Lima' });

    console.log(`Simulation started at ${peruTime} Peru time`);

    const intervalId = setInterval(() => {
      setControlClock((prevClock) => {
        const newClock = (prevClock + 0.5) % (24 * 60); // Wrap around every 24 hours
    
        // If newClock is a multiple of 20, log a message
        if (newClock % 120 === 0) {
          console.log(`120 minutes of simulation time have passed at ${new Date().toLocaleTimeString('en-US', { timeZone: 'America/Lima' })} Peru time`);
          console.log(`Control clock: ${minutesToTime(newClock)}`); // Convert the control clock to HH:MM format
        }
    
        return newClock;
      });
    }, 500/speedFactor); // Every real-time second

    const timeoutId = setTimeout(() => {
      clearInterval(intervalId); // Stop the interval after 10 seconds
      setStartSimulation(false); // Set startSimulation to false

      const peruTime = new Date().toLocaleTimeString('en-US', { timeZone: 'America/Lima' });
      console.log(`Simulation stopped at ${peruTime} Peru time`);

    }, interval*1000);

    return () => {
      clearInterval(intervalId); // Clean up on unmount
      clearTimeout(timeoutId); // Clear the timeout on unmount
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
  };

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <Map
          planes={startSimulation ? planes : []}
          controlClock={controlClock}
        />
        {/* Pass planes only if simulation starts */}
        {showModal && (
          <ConfigurationModal onApply={handleApplyConfiguration} />
        )}{" "}
        {/* Show modal */}
      </div>
    </div>
  );
};

export default Home;
