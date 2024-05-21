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
  destination,
  name,
  duration,
}) => {
  const [position, setPosition] = useState<LatLngExpression>([
    origin.lat,
    origin.lng,
  ]);
  const map = useMap();

  useEffect(() => {
    let startTime: number | null = null;

    const animate = (timestamp: number) => {
      if (!startTime) startTime = timestamp;
      const progress = Math.min((timestamp - startTime) / duration, 1);

      const newLat = origin.lat + (destination.lat - origin.lat) * progress;
      const newLng = origin.lng + (destination.lng - origin.lng) * progress;

      setPosition([newLat, newLng] as LatLngExpression);

      if (progress < 1) {
        requestAnimationFrame(animate);
      }
    };

    requestAnimationFrame(animate);
  }, [origin, destination, duration]);

  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
    }, duration + 700);

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
          [origin.lat, origin.lng],
          [destination.lat, destination.lng],
        ]}
        pathOptions={{ color: "black", weight: 1, dashArray: "5,10" }}
      />
      <Marker position={position} icon={planeIcon}>
        <Popup>{name}</Popup>
      </Marker>
    </>
  );
};

export default Plane;
