import React, { useEffect, useState } from "react";
import "@/app/styles/MoreInfoComponent.css";
import { Airport, Vuelo } from "@/app/types/Planes";
import CurrentTimeDisplay from "./CurrentTimeDisplay";

const MoreInfo = ({
  onClose,
  planes,
  airports,
  startTime,
  startDate,
  startHour,
  speedFactor,
  startSimulation,
  dayToDay,
  vuelosInAir,
}: {
  onClose: () => void;
  planes: React.RefObject<Vuelo[]>;
  airports: React.RefObject<Airport[]>;
  startTime: React.RefObject<number>;
  startDate: string;
  startHour: string;
  speedFactor: number;
  startSimulation: boolean;
  dayToDay: boolean;
  vuelosInAir: React.RefObject<number>;
}) => {
  const [currentTime, setCurrentTime] = useState<string | null>(null);
  const [elapsedTime, setElapsedTime] = useState<string | null>(null);

  useEffect(() => {
    const updateCurrentTime = () => {
      const now = new Date();
      const formattedTime = now.toLocaleString(undefined, {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: false,
      });
      setCurrentTime(formattedTime);
      const elapsed = startTime.current ? now.getTime() - startTime.current : 0;
      const elapsedSeconds = Math.floor(elapsed / 1000);
      const elapsedMinutes = Math.floor(elapsedSeconds / 60);
      const elapsedHours = Math.floor(elapsedMinutes / 60);
      const elapsedString = `${(elapsedHours % 24)
        .toString()
        .padStart(2, "0")} : ${(elapsedMinutes % 60)
        .toString()
        .padStart(2, "0")} : ${(elapsedSeconds % 60)
        .toString()
        .padStart(2, "0")}`;
      setElapsedTime(elapsedString);
    };

    updateCurrentTime();
    const intervalId = setInterval(updateCurrentTime, 1000);

    return () => clearInterval(intervalId);
  }, []);

  return (
    <div className="more-info-container">
      <div className="more-info-header">
        <h2>
          <strong>Más información</strong>
        </h2>
        <button className="close-button-more-info" onClick={onClose}>
          &times;
        </button>
      </div>
      <div className="more-info-content">
        {!dayToDay && (
          <div>
            <p>Hora actual:</p>
            <div className="time-display">{currentTime}</div>
            <p>Tiempo transcurrido:</p>
            <div className="time-display">{elapsedTime}</div>
          </div>
        )}
        <div>
          <p>
            <span style={{ fontWeight: "bold" }}>Vuelos en el aire: </span>
            {vuelosInAir.current}
          </p>
        </div>
      </div>
    </div>
  );
};
export default MoreInfo;
