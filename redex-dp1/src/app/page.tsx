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
  const [planes, setPlanes] = useState<PlaneProps[]>([]);
  const [showModal, setShowModal] = useState(true);
  const [startSimulation, setStartSimulation] = useState(false);
  const [controlClock, setControlClock] = useState(0); // Control clock in minutes

  // Update control clock every real-time second
  useEffect(() => {
    if (!startSimulation) return; // Don't start the clock if startSimulation is not true
  
    const intervalId = setInterval(() => {
      setControlClock((prevClock) => (prevClock + 5) % (24 * 60)); // Wrap around every 24 hours
    }, 1000); // Every real-time second
  
    const timeoutId = setTimeout(() => {
      clearInterval(intervalId); // Stop the interval after 10 seconds
    }, 10000); // 10 real-time seconds
  
    return () => {
      clearInterval(intervalId); // Clean up on unmount
      clearTimeout(timeoutId); // Clear the timeout on unmount
    };
  }, [startSimulation]);

  // Update planes based on control clock
  useEffect(() => {
    if (!startSimulation) return;

    setPlanes((prevPlanes) => {
      let newPlanes: PlaneProps[] = [...prevPlanes];

      console.log("controlClock", minutesToTime(controlClock));
      console.log("newPlanes before departure", newPlanes);

      let flag = false;

      // Add planes with departureTime equal to control clock
      const departingPlanes = flightPlans.filter(
        (plan) =>
          timeToMinutes(plan.departureTime) <= controlClock &&
          !newPlanes.includes(plan)
      );
      if (departingPlanes.length > 0) {
        newPlanes = [...newPlanes, ...departingPlanes];
        flag = true;
      }
        

      // console.log("departingPlanes", departingPlanes);

      // Remove planes whose arrivalTime plus one hour has passed
      let ninitial = newPlanes.length;
      newPlanes = newPlanes.filter((plane) => {
        const arrivalTimeMinutes = timeToMinutes(plane.arrivalTime);
        const oneHourLater = (arrivalTimeMinutes + 30) % (24 * 60); // Wrap around every 24 hours
        return controlClock < oneHourLater;
      });

      if (newPlanes.length !== ninitial) flag = true;

      console.log("newPlanes after arrival", newPlanes);

      if (flag) return newPlanes;
      return prevPlanes;
    });
  }, [controlClock, startSimulation]);

  // useEffect(() => {
  //   if (!startSimulation) return;
  
  //   let currentTime = controlClock;
  //   let currentPlanes: PlaneProps[] = [...planes];
  
  //   while(currentTime) { // Run the loop for 5 iterations
  //     currentTime = (currentTime + 2) % (24 * 60); // Wrap around every 24 hours
  
  //     // Add planes with departureTime equal to currentTime
  //     const departingPlanes = flightPlans.filter(
  //       (plan) =>
  //         timeToMinutes(plan.departureTime) <= currentTime &&
  //         !currentPlanes.includes(plan)
  //     );
  //     currentPlanes = [...currentPlanes, ...departingPlanes];
  
  //     // Remove planes whose arrivalTime plus one hour has passed
  //     currentPlanes = currentPlanes.filter((plane) => {
  //       const arrivalTimeMinutes = timeToMinutes(plane.arrivalTime);
  //       const oneHourLater = (arrivalTimeMinutes + 30) % (24 * 60); // Wrap around every 24 hours
  //       return currentTime < oneHourLater;
  //     });

  //     setPlanes(currentPlanes);
  //   }
  
  //   setControlClock(currentTime);
    
  // }, [startSimulation]);

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
        <Map planes={startSimulation ? planes : []} />{" "}
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
