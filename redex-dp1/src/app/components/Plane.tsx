import React, { useState, useEffect } from "react";
import { Marker, Popup, Polyline, useMap } from "react-leaflet";
import L, { LatLngExpression } from "leaflet";

const planeIcon = L.icon({
  iconUrl: "images/plane.svg",
  iconSize: [20, 20], // size of the icon
});

interface PlaneProps {
  origin: { lat: number; lng: number };
  destination: { lat: number; lng: number };
  name: string;
  duration: number; // duration in milliseconds for the animation
}

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
