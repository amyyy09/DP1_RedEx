// components/PlaneMap.tsx
"use client";

import React from "react";
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  ZoomControl,
} from "react-leaflet";
import L, { LatLngTuple } from "leaflet";
import "leaflet/dist/leaflet.css";
import Plane from "./Plane";
import { PlaneProps } from "@/app/types/Planes";
import { cities } from "@/app/data/cities";

interface MapProps {
  planes: Vuelo[];
  startTime: React.RefObject<number>;
  startDate: string;
  startHour: string;
  simulatedDate: React.RefObject<Date>;
  speedFactor: number;
}

const customIcon = new L.Icon({
  iconUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png",
  iconRetinaUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png",
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
  shadowSize: [41, 41],
});

const Map: React.FC<MapProps> = ({
  planes,
  startTime,
  startDate,
  startHour,
  simulatedDate,
  speedFactor,
}) => {
  return (
    <MapContainer
      center={[20, 20]}
      zoom={3}
      style={{ height: "calc(100vh - 50px)", width: "calc(100vw - 50px)" }}
      zoomControl={false}
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      {/* Add custom zoom control */}
      <ZoomControl position="topright" />

      {cities.map((city, idx) => (
        <Marker
          key={idx}
          position={[city.coords.lat, city.coords.lng] as LatLngTuple}
          icon={customIcon}
        >
          <Popup>{city.name}</Popup>
        </Marker>
      ))}

      {planes.length > 0 &&
        planes.map((plane, index) => (
          <Plane
            key={index}
            vuelo={plane}
            startTime={startTime}
            startDate={startDate}
            startHour={startHour}
            simulatedDate={simulatedDate}
            speedFactor={speedFactor}
          />
        ))}
    </MapContainer>
  );
};

export default Map;
