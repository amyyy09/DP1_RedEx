"use client";
import React from "react";
import Topbar from "@/components/layout/Topbar";
import SimulationPage from "@/components/SimulationPage";

const SimulacionPage: React.FC = () => {
  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar />
      <div style={{ display: "flex", flex: 1 }}>
        <SimulationPage />
      </div>
    </div>
  );
};

export default SimulacionPage;
