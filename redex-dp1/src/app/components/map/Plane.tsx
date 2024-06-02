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
  speedFactor,
  startSimulation
}) => {
  const [position, setPosition] = useState<LatLngExpression>([0, 0]);
  const [isVisible, setIsVisible] = useState(false);
  const simulatedDate = React.useRef<Date>();
  const map = useMap();

  const [duration, setDuration] = useState(0);

  useEffect(() => {
    if (!startSimulation) return;

    console.log("plane started");

    // Update the simulated time
    const updateSimulatedTime = () => {
      if (!startSimulation || !startTime.current) return;

      // console.log("startTime", startTime.current);

      const currentTime = Date.now();
      // console.log("currentTime", currentTime);
      const elapsedTime = (currentTime - startTime.current) / 1000; // in seconds
      // console.log("elapsedTime", elapsedTime);
      const simulatedTime = elapsedTime * speedFactor;
      // console.log("simulatedTime", simulatedTime);
      // Create a new Date object for the start of the simulation
      const startDateSim = new Date(startDate + "T" + startHour + ":00");
      // console.log("startDateSim", startDateSim);

      // Add the simulated time to the start date
      simulatedDate.current = new Date(
        startDateSim.getTime() + simulatedTime * 1000
      );

      // console.log("simulatedDate.current", simulatedDate.current);

      const systemTimezoneOffset = new Date().getTimezoneOffset();

      const origin = citiesByCode[vuelo.aeropuertoOrigen];
      const destiny = citiesByCode[vuelo.aeropuertoDestino];

      // Get the origin and destiny city's GMT offsets in minutes
      const originGMTOffset = origin.GMT;
      const destinyGMTOffset = destiny.GMT;

      // Convert the departure and arrival times to the system's timezone
      const horaSalida = new Date(vuelo.horaSalida);
      console.log("horaSalida vuelo", vuelo.horaSalida);
      console.log("horaSalida inicial", horaSalida);
      // console.log("systemTimezoneOffset", systemTimezoneOffset);
      // console.log("horaSalida hour", horaSalida.getUTCHours()+ originGMTOffset - systemTimezoneOffset);

      horaSalida.setUTCHours(
        horaSalida.getUTCHours() - originGMTOffset 
      );
      console.log("offset", originGMTOffset);
      console.log("horaSalida after", horaSalida);

      const horaLlegada = new Date(vuelo.horaLlegada);
      console.log("horaLlegada inicial", horaLlegada);
      horaLlegada.setUTCHours(
        horaLlegada.getUTCHours() - destinyGMTOffset
      );
      console.log("offset", destinyGMTOffset);
      console.log("horaLlegada after", horaLlegada);

      if (
        simulatedDate.current &&
        (simulatedDate.current > horaLlegada ||
          simulatedDate.current < horaSalida)
      ) {
        setIsVisible(false);
        console.log("Plane is not visible");
        console.log("simulatedDate.current", simulatedDate.current);
        // console.log("horaLlegada aquí", horaLlegada);
        return;
      }

      if (
        simulatedDate.current &&
        simulatedDate.current >= horaSalida &&
        simulatedDate.current <= horaLlegada
      ) {
        console.log("Plane is visible");
        console.log("simulatedDate.current", simulatedDate.current);
        console.log("horaSalida", horaSalida);
        setIsVisible(true);
      }

      const progress =
        ((simulatedDate.current?.getTime() ?? 0) - horaSalida.getTime()) /
        (horaLlegada.getTime() - horaSalida.getTime());

      const newLat =
        origin.coords.lat +
        (destiny.coords.lat - origin.coords.lat) * progress;

      const newLng =
        origin.coords.lng +
        (destiny.coords.lng - origin.coords.lng) * progress;

      setPosition([newLat, newLng] as LatLngExpression);
    };

    // Call updateSimulatedTime every second
    const intervalId = setInterval(updateSimulatedTime, 100 / speedFactor);

    // Clean up on unmount
    return () => {
      clearInterval(intervalId);
    };
  }, [startSimulation]);

  // const updatePlanePosition = () => {
  //   if (simulatedDate === undefined) return;
  //   // console.log("simulatedDate at Plane", simulatedDate.current);
  //   // console.log("currentTime at Plane", currentTime);

  //   const systemTimezoneOffset = new Date().getTimezoneOffset();

  //   const origin = citiesByCode[vuelo.aeropuertoOrigen];
  //   const destiny = citiesByCode[vuelo.aeropuertoDestino];

  //   // Get the origin and destiny city's GMT offsets in minutes
  //   const originGMTOffset = origin.GMT;
  //   const destinyGMTOffset = destiny.GMT;

  //   // Convert the departure and arrival times to the system's timezone
  //   const horaSalida = new Date(vuelo.horaSalida);
  //   console.log("horaSalida", horaSalida);
  //   // console.log("systemTimezoneOffset", systemTimezoneOffset);
  //   // console.log("horaSalida hour", horaSalida.getUTCHours()+ originGMTOffset - systemTimezoneOffset);

  //   horaSalida.setUTCHours(
  //     horaSalida.getUTCHours() + originGMTOffset - systemTimezoneOffset / 100
  //   );

  //   console.log("horaSalida after", horaSalida);

  //   const horaLlegada = new Date(vuelo.horaLlegada);
  //   horaLlegada.setUTCHours(
  //     horaLlegada.getUTCHours() + destinyGMTOffset - systemTimezoneOffset
  //   );

  //   if (
  //     simulatedDate.current &&
  //     (simulatedDate.current >= horaLlegada ||
  //       simulatedDate.current < horaSalida)
  //   ) {
  //     setIsVisible(false);
  //     console.log("Plane is not visible");
  //     console.log("simulatedDate.current", simulatedDate.current);
  //     console.log("horaSalida aquí", horaSalida);
  //     return;
  //   }

  //   if (
  //     simulatedDate.current &&
  //     simulatedDate.current >= horaSalida &&
  //     simulatedDate.current < horaLlegada
  //   ) {
  //     console.log("Plane is visible");
  //     console.log("simulatedDate.current", simulatedDate.current);
  //     console.log("horaSalida", horaSalida);
  //     setIsVisible(true);
  //   }

  //   const progress =
  //     ((simulatedDate.current?.getTime() ?? 0) - horaSalida.getTime()) /
  //     (horaLlegada.getTime() - horaSalida.getTime());

  //   const newLat =
  //     origin.coords.lat + (destiny.coords.lat - origin.coords.lat) * progress;
  //   const newLng =
  //     origin.coords.lng + (destiny.coords.lng - origin.coords.lng) * progress;

  //   setPosition([newLat, newLng] as LatLngExpression);
  // };

  // useEffect(() => {
  //   if (vuelo === undefined) return;
  //   if(startTime === undefined) return;
  //   if(startDate === undefined) return;

  //   updatePlanePosition();

  // }, [vuelo, startTime, startDate]);

  useEffect(() => {
    if (vuelo === undefined) return;
    if(startTime === undefined) return;
    if(startDate === undefined) return;
    if(!startSimulation) return;
    // updatePlanePosition();
    
  }, [vuelo, startTime, startDate,startSimulation]);

  // Periodically update the plane's position
  useEffect(() => {
    // const intervalId = setInterval(updatePlanePosition, 1000 / speedFactor);
    // return () => clearInterval(intervalId);
  }, [simulatedDate.current, speedFactor]);

  // useEffect(() => {
  //   if (duration === 0) return;

  //   const timer = setTimeout(() => {
  //     setIsVisible(false);
  //   }, duration + 500);

  //   // Cleanup function to clear the timeout if the component unmounts before the duration
  //   return () => clearTimeout(timer);
  // }, [duration]);

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
              [citiesByCode[vuelo.aeropuertoOrigen].coords.lat, citiesByCode[vuelo.aeropuertoOrigen].coords.lng],
              [citiesByCode[vuelo.aeropuertoDestino].coords.lat, citiesByCode[vuelo.aeropuertoDestino].coords.lng],
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
