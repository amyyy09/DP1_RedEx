import React, { useState, useEffect } from "react";
import { Marker, Popup, Polyline, useMap } from "react-leaflet";
import L, { LatLngExpression } from "leaflet";
import { PlaneProps } from "../../types/Planes";

const planeIcon = L.icon({
  iconUrl: "./icons/plane.svg",
  iconSize: [20, 20], // size of the icon
});

const Plane: React.FC<PlaneProps> = ({
  origin,
  destiny,
  departureTime,
  arrivalTime,
  capacidad,
  controlClock,
}) => {
  const [position, setPosition] = useState<LatLngExpression>([
    origin.coords.lat,
    origin.coords.lng,
  ]);
  const map = useMap();

  const [duration, setDuration] = useState(0);

  useEffect(() => {
    if (controlClock === undefined) return;

    const timeToMinutes = (time: string) => {
      const [hours, minutes] = time.split(":").map(Number);
      return hours * 60 + minutes;
    };

    const departureTimeMinutes = timeToMinutes(departureTime);
    const arrivalTimeMinutes = timeToMinutes(arrivalTime);

    // If controlClock is greater than departureTime, don't start the animation
    if (controlClock > departureTimeMinutes) {
      setIsVisible(true);
    } else {
      return;
    }

    // If controlClock is greater than arrivalTime, end the animation
    if (controlClock > arrivalTimeMinutes) {
      setIsVisible(false);
      return;
    }

    // Calculate progress based on controlClock, departureTime and arrivalTime
    const progress =
      (controlClock - departureTimeMinutes) /
      (arrivalTimeMinutes - departureTimeMinutes);

    const newLat =
      origin.coords.lat + (destiny.coords.lat - origin.coords.lat) * progress;
    const newLng =
      origin.coords.lng + (destiny.coords.lng - origin.coords.lng) * progress;

    setPosition([newLat, newLng] as LatLngExpression);
  }, [origin, destiny, controlClock, departureTime, arrivalTime]);

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
          positions={[
            [origin.coords.lat, origin.coords.lng],
            [destiny.coords.lat, destiny.coords.lng],
          ]}
          pathOptions={{ color: "black", weight: 1, dashArray: "5,10" }}
        />
      )}
      {isVisible && (
        <Marker position={position} icon={planeIcon}>
          <Popup>
            <div>
              <h2>Detalles de vuelo</h2>
              <p>
                <strong>Origen:</strong> {origin.name}
              </p>
              <p>
                <strong>Destino:</strong> {destiny.name}
              </p>
              <p>
                <strong>Hora de salida:</strong> {departureTime}
              </p>
              <p>
                <strong>Hora de llegada:</strong> {arrivalTime}
              </p>
              <p>
                <strong>Capacidad:</strong> {capacidad}
              </p>
            </div>
          </Popup>
        </Marker>
      )}
    </>
  );
};

export default Plane;
