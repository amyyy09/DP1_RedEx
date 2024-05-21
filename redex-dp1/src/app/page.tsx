"use client";

import React, { useMemo, useState } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";

const Home: React.FC = () => {
  const [planes, setPlanes] = useState([
    {
      origin: { lat: 25.2048, lng: 55.2708 }, // Dubai
      destination: { lat: 1.3521, lng: 103.8198 }, // Singapore
      name: "Flight 123",
      duration: 7000, // 5 seconds
    },
    {
      origin: { lat: -12.0464, lng: -77.0428 },
      destination: { lat: 50.8503, lng: 4.3517 },
      name: "Flight 456",
      duration: 10000, // 3 seconds
    },
    // Add more plane objects here
  ]);

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
        <Map planes={planes} />
      </div>
    </div>
  );
};

export default Home;
