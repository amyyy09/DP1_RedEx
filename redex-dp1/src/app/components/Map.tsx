// components/PlaneMap.tsx
'use client'

import React from 'react';
import { MapContainer, TileLayer } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import Plane from './Plane';
import { PlaneProps } from '../types/Planes';

interface MapProps {
  planes: PlaneProps[];
}

const Map: React.FC<MapProps> = ({ planes }) => {

  return (
    <MapContainer center={[0, 0]} zoom={2} style={{ height: '100vh', width: '100vw' }}>
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      {planes.map((plane, index) => (
        <Plane
          key={index}
          origin={plane.origin}
          destination={plane.destination}
          name={plane.name}
          duration={plane.duration}
        />
      ))}
    </MapContainer>
  );
};

export default Map;