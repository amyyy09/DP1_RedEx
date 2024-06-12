"use client";

import React from "react";

interface SimulatedTimeProps {
  displayTime?: string; // Hacemos que displayTime sea opcional
}

const SimulatedTimeContainer: React.FC<SimulatedTimeProps> = ({
  displayTime,
}) => {
  return (
    <div className="simulated-time-container">
      Tiempo de la simulación: {displayTime || "--:--:--"}
    </div>
  );
};

export default SimulatedTimeContainer;
