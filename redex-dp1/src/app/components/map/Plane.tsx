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
  capacidad  
}) => {
  const [position, setPosition] = useState<LatLngExpression>([
    origin.coords.lat,
    origin.coords.lng,
  ]);
  const map = useMap();

  const [duration, setDuration] = useState(0);

useEffect(() => {
  const timeToMinutes = (time: string) => {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
  };

  const departureTimeMinutes = timeToMinutes(departureTime);
  let arrivalTimeMinutes = timeToMinutes(arrivalTime);

  // If the arrival time is less than the departure time, add 24 hours (in minutes) to the arrival time
  if (arrivalTimeMinutes < departureTimeMinutes) {
    arrivalTimeMinutes += 24 * 60;
  }

  const durationMinutes = arrivalTimeMinutes - departureTimeMinutes;

  // Convert the duration to real-time seconds using the given rule
  setDuration((durationMinutes / 4.2) * 1000); // Multiply by 1000 to convert seconds to milliseconds
}, [departureTime, arrivalTime]);

  useEffect(() => {
    if (duration === 0) return;

    let startTime: number | null = null;

    const animate = (timestamp: number) => {
      if (!startTime) startTime = timestamp;
      const progress = Math.min((timestamp - startTime) / duration, 1);

      const newLat = origin.coords.lat + (destiny.coords.lat - origin.coords.lat) * progress;
      const newLng = origin.coords.lng + (destiny.coords.lng - origin.coords.lng) * progress;

      setPosition([newLat, newLng] as LatLngExpression);

      if (progress < 1) {
        requestAnimationFrame(animate);
      }
    };

    requestAnimationFrame(animate);
  }, [origin, destiny, duration]);

  const [isVisible, setIsVisible] = useState(true);

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
      <Polyline
        positions={[
          [origin.coords.lat, origin.coords.lng],
          [destiny.coords.lat, destiny.coords.lng],
        ]}
        pathOptions={{ color: "black", weight: 1, dashArray: "5,10" }}
      />
      <Marker position={position} icon={planeIcon}>
        <Popup>{capacidad}</Popup>
      </Marker>
    </>
  );
};

export default Plane;
