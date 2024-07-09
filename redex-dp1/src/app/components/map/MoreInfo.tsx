import React, { useEffect, useRef, useState } from "react";
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
  const [averageCapacityInAir, setAverageCapacityInAir] = useState<
    number | null
  >(null);
  const [averageCapacityInAirports, setAverageCapacityInAirports] = useState<
    number | null
  >(null);
  const sortedAirports = useRef<Airport[]>([]);

  useEffect(() => {
    const updateCurrentTime = () => {
      if (!dayToDay) {
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
        const elapsed = startTime.current
          ? now.getTime() - startTime.current
          : 0;
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
      } else {
        clearInterval(intervalId);
      }
    };

    const updateSimulatedTime = () => {
      if (planes.current && planes.current.length > 0) {
        // Filter planes that are currently in the air
        if (planes.current && planes.current.length > 0) {
          // Filter planes that are currently in the air
          const planesInAir = planes.current.filter((plane) => plane.enAire);

          if (planesInAir.length > 0) {
            const totalPlanesInAir = planesInAir.length;
            const totalCapacityInAir = planesInAir.reduce(
              (acc, plane) => acc + plane.capacidad,
              0
            );
            const totalPackagesInAir = planesInAir.reduce(
              (acc, plane) => acc + plane.cantPaquetes,
              0
            );

            const saturationOfCapacityInAir =
              (totalPackagesInAir / totalCapacityInAir) * 100;
            // Use saturationOfCapacityInAir as needed
            setAverageCapacityInAir(saturationOfCapacityInAir);
          }
        }
      }

      if (airports.current && airports.current.length > 0) {
        const temp = airports.current
        .map(airport => ({
          ...airport,
          ratio: airport.almacen.cantPaquetes / airport.almacen.capacidad
        }))
        .sort((a, b) => b.ratio - a.ratio) // Sort in descending order by ratio
        .slice(0, 20);
        const totalAirportCapacity = temp.reduce(
          (acc, airport) => acc + airport.almacen.capacidad,
          0
        );
        const totalAirportPackages = temp.reduce(
          (acc, airport) => acc + airport.almacen.cantPaquetes,
          0
        );
        const saturationOfCapacityInAirports =
          (totalAirportPackages / totalAirportCapacity) * 100;
        // Use saturationOfCapacityInAirports as needed
        setAverageCapacityInAirports(saturationOfCapacityInAirports);

        // Sort airports by saturation
        sortedAirports.current = airports.current.sort((a, b) => {
          const ratioA = a.almacen.cantPaquetes / a.almacen.capacidad;
          const ratioB = b.almacen.cantPaquetes / b.almacen.capacidad;
          return ratioB - ratioA; // For descending order
        });
      }
    };

    const intervalId = setInterval(updateCurrentTime, 1000);
    const intervalId2 = setInterval(updateSimulatedTime, 1000 / speedFactor);

    return () => {
      clearInterval(intervalId);
      clearInterval(intervalId2);
    };
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
      <hr />
      <div className="more-info-content">
        {!dayToDay && (
          <div className="more-info-section">
            <p>Hora actual:</p>
            <div className="time-display">{currentTime}</div>
            <p>Tiempo transcurrido:</p>
            <div className="time-display">{elapsedTime}</div>
          </div>
        )}
        {/* add a divider line*/}
        <hr />
        <div className="more-info-section">
          <p>
            <span className="font-bold">Vuelos en el aire: </span>
            {vuelosInAir.current
              ? vuelosInAir.current < 0
                ? "0"
                : vuelosInAir.current
              : "0"}
          </p>
          <p className="mt-2">
            <span className="font-bold">Saturacion de aviones: </span>
            {averageCapacityInAir ? averageCapacityInAir.toFixed(2) : "-"}%
          </p>
        </div>
        <hr />
        <div className="more-info-section">
          <p>
            <span className="font-bold">
              Saturacion de aeropuertos:{" "}
            </span>
            {averageCapacityInAirports
              ? averageCapacityInAirports.toFixed(2)
              : "-"}
            %
          </p>
        </div>
        <hr />
        <div className="more-info-section">
          <h2 className="font-bold">Aeropuertos más saturados</h2>
          <div className="mt-2 max-h-[95px] overflow-y-auto">
            {sortedAirports.current.slice(0, 5).map((airport, index) => (
              <div key={index} className={`mb-2 ${index === 4 ? "mb-0" : ""}`}>
                <p>
                  <strong>{airport.ciudad}</strong>
                </p>
                <p>Capacidad: {airport.almacen.capacidad}</p>
                <p>Paquetes: {airport.almacen.cantPaquetes.toFixed(0)}</p>
                <p>
                  Saturación: {""}
                  {(
                    (airport.almacen.cantPaquetes /
                      (airport.almacen.capacidad)) *
                    100
                  ).toFixed(2)}
                  %{" "}
                </p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
export default MoreInfo;
