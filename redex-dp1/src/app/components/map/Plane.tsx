import React, { useState, useEffect } from "react";
import { Marker, Popup, Polyline, useMap } from "react-leaflet";
import L, { LatLngExpression } from "leaflet";
import { PlaneProps } from "../../types/Planes";
import { citiesByCode } from "@/app/data/cities";

const planeIcon = L.icon({
  iconUrl: "./icons/plane.svg",
  iconSize: [20, 20], // size of the icon
});

const Plane: React.FC<PlaneProps> = ({
  vuelo,
  startTime,
  startDate,
  startHour,
  simulatedDate,
  speedFactor,
}) => {
  const [position, setPosition] = useState<LatLngExpression>([0, 0]);
  const map = useMap();

  const [duration, setDuration] = useState(0);

  useEffect(() => {
    if (vuelo === undefined) return;
    if (simulatedDate === undefined) return;

    console.log("simulatedDate at Plane", simulatedDate.current);
    // console.log("currentTime at Plane", currentTime);

    const systemTimezoneOffset = new Date().getTimezoneOffset();

    const origin = citiesByCode[vuelo.aeropuertoOrigen];
    const destiny = citiesByCode[vuelo.aeropuertoDestino];

    // Get the origin and destiny city's GMT offsets in minutes
    const originGMTOffset = origin.GMT * 60;
    const destinyGMTOffset = destiny.GMT * 60;

    // Convert the departure and arrival times to the system's timezone
    const horaSalida = new Date(vuelo.horaSalida);
    horaSalida.setUTCHours(
      horaSalida.getUTCHours() + originGMTOffset - systemTimezoneOffset
    );

    const horaLlegada = new Date(vuelo.horaLlegada);
    horaLlegada.setUTCHours(
      horaLlegada.getUTCHours() + destinyGMTOffset - systemTimezoneOffset
    );

    if (simulatedDate.current && simulatedDate.current >= horaLlegada) {
      setIsVisible(false);
      return;
    }

    // if (simulatedDate < vuelo.horaSalida) {
    //   setIsVisible(false);
    //   return;
    // }

    if (
      simulatedDate.current &&
      simulatedDate.current >= horaSalida &&
      simulatedDate.current < horaLlegada
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
  }, [vuelo, startTime, speedFactor, simulatedDate]);

  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (duration === 0) return;

    const timer = setTimeout(() => {
      setIsVisible(false);
    }, duration + 500);

    // Cleanup function to clear the timeout if the component unmounts before the duration
    return () => clearTimeout(timer);
  }, [duration]);

  if (!isVisible) {
    return null;
  }

  // set the view of the map to the plane's position
  //   useEffect(() => {
  //     if (map) {
  //       map.setView(position);
  //     }
  //   }, [position, map]);

  return (
    <>
      {isVisible && (
        <Polyline
          positions={
            [
              // [origin.coords.lat, origin.coords.lng],
              // [destiny.coords.lat, destiny.coords.lng],
            ]
          }
          pathOptions={{ color: "black", weight: 1, dashArray: "5,10" }}
        />
      )}
      {isVisible && (
        <Marker position={position} icon={planeIcon}>
          <Popup>
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
                {vuelo.horaSalida.toLocaleString(undefined, {
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
                <strong>Hora de llegada:</strong>{" "}
                {vuelo.horaLlegada.toLocaleString(undefined, {
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
                <strong>Capacidad:</strong> {vuelo.capacidad}
              </p>
              <p>
                <strong>Cantidad de paquetes:</strong> {vuelo.cantPaquetes}
              </p>
            </div>
          </Popup>
        </Marker>
      )}
    </>
  );
};

export default Plane;
