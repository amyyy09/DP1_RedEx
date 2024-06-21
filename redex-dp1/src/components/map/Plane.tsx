import React, { useState, useEffect } from "react";
import { Polyline, useMap } from "react-leaflet";
import L, { LatLngExpression } from "leaflet";
import { PlaneProps } from "@/types/Planes";
import { citiesByCode } from "@/utils/data/cities";
import { arrayToTime } from "@/utils/timeHelper";
import RotatedMarker from "./RotatedMarker";

const calculateRotationAngle = (
  origin: { lat: number; lng: number },
  destination: { lat: number; lng: number }
): number => {
  return (
    Math.atan2(destination.lat - origin.lat, destination.lng - origin.lng) *
      (180 / Math.PI) +
    87
  );
};

const planeIcon = L.icon({
  iconUrl: "./icons/plane.svg",
  iconSize: [20, 20],
});

const Plane: React.FC<PlaneProps> = ({
  vuelo,
  index,
  listVuelos,
  startTime,
  startDate,
  startHour,
  speedFactor,
  startSimulation,
}) => {
  const [position, setPosition] = useState<LatLngExpression>([0, 0]);
  const [isVisible, setIsVisible] = useState(false);
  const simulatedDate = React.useRef<Date>();

  useEffect(() => {
    if (!startSimulation) return;

    const updateSimulatedTime = () => {
      if (!startSimulation || !startTime.current) return;

      const currentTime = Date.now();
      const elapsedTime = (currentTime - startTime.current) / 1000; // in seconds
      const simulatedTime = elapsedTime * speedFactor;
      const startDateSim = new Date(startDate + "T" + startHour + ":00");

      simulatedDate.current = new Date(
        startDateSim.getTime() + simulatedTime * 1000
      );

      const origin = citiesByCode[vuelo.aeropuertoOrigen];
      const destiny = citiesByCode[vuelo.aeropuertoDestino];

      const originGMTOffset = origin.GMT;
      const destinyGMTOffset = destiny.GMT;

      const horaSalida = arrayToTime(vuelo.horaSalida);
      horaSalida.setUTCHours(horaSalida.getUTCHours() - originGMTOffset);

      const horaLlegada = arrayToTime(vuelo.horaLlegada);
      horaLlegada.setUTCHours(horaLlegada.getUTCHours() - destinyGMTOffset);

      if (
        simulatedDate.current &&
        (simulatedDate.current > horaLlegada ||
          simulatedDate.current < horaSalida)
      ) {
        setIsVisible(false);

        if (simulatedDate.current > horaLlegada) {
          clearInterval(intervalId);
        }
        return;
      }

      if (
        simulatedDate.current &&
        simulatedDate.current >= horaSalida &&
        simulatedDate.current <= horaLlegada
      ) {
        setIsVisible(true);
      }

      const progress =
        ((simulatedDate.current?.getTime() ?? 0) - horaSalida.getTime()) /
        (horaLlegada.getTime() - horaSalida.getTime());

      const newLat =
        origin.coords.lat + (destiny.coords.lat - origin.coords.lat) * progress;

      const newLng =
        origin.coords.lng + (destiny.coords.lng - origin.coords.lng) * progress;

      setPosition([newLat, newLng] as LatLngExpression);
    };

    const intervalId = setInterval(updateSimulatedTime, 100 / speedFactor);

    return () => {
      clearInterval(intervalId);
    };
  }, [startSimulation]);

  const rotationAngle = calculateRotationAngle(
    citiesByCode[vuelo.aeropuertoOrigen].coords,
    citiesByCode[vuelo.aeropuertoDestino].coords
  );

  if (!isVisible) {
    return null;
  }

  return (
    <>
      {isVisible && (
        <Polyline
          positions={[
            [
              citiesByCode[vuelo.aeropuertoOrigen].coords.lat,
              citiesByCode[vuelo.aeropuertoOrigen].coords.lng,
            ],
            [
              citiesByCode[vuelo.aeropuertoDestino].coords.lat,
              citiesByCode[vuelo.aeropuertoDestino].coords.lng,
            ],
          ]}
          pathOptions={{ color: "white", weight: 1, dashArray: "5,10" }}
        />
      )}
      {isVisible && (
        <RotatedMarker
          position={position}
          icon={planeIcon}
          rotationAngle={rotationAngle}
          popupContent={
            <div>
              <h2>Detalles de vuelo</h2>
              <p>
                <strong>Origen:</strong>{" "}
                {citiesByCode[vuelo.aeropuertoOrigen].name}
              </p>
              <p>
                <strong>Destino:</strong>{" "}
                {citiesByCode[vuelo.aeropuertoDestino].name}
              </p>
              <p>
                <strong>Hora de salida:</strong>{" "}
                {arrayToTime(vuelo.horaSalida).toLocaleString(undefined, {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                  second: "2-digit",
                  hour12: false,
                })}
              </p>
              <p>
                <strong>GMT origen:</strong>
                {citiesByCode[vuelo.aeropuertoOrigen].GMT}
              </p>
              <p>
                <strong>Hora de llegada:</strong>{" "}
                {arrayToTime(vuelo.horaLlegada).toLocaleString(undefined, {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                  second: "2-digit",
                  hour12: false,
                })}
              </p>
              <p>
                <strong>GMT destino:</strong>
                {citiesByCode[vuelo.aeropuertoDestino].GMT}
              </p>
              <p>
                <strong>Capacidad:</strong> {vuelo.capacidad}
              </p>
              <p>
                <strong>Cantidad de paquetes:</strong> {vuelo.cantPaquetes}
              </p>
            </div>
          }
        />
      )}
    </>
  );
};

export default Plane;
