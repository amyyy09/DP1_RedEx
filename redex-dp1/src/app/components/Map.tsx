// components/PlaneMap.tsx
'use client'

import React, { useState } from 'react';
import { MapContainer, TileLayer } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import Plane from './Plane';

const Map: React.FC = () => {
  const [origin] = useState({ lat: 40.7128, lng: -74.0060 });
  const [destination] = useState({ lat: 34.0522, lng: -118.2437 });
  const [name] = useState('Flight 123');
  const [duration] = useState(5000); // 5 seconds

  return (
    <MapContainer center={[0, 0]} zoom={2} style={{ height: '100vh', width: '100vw' }}>
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      <Plane
        origin={origin}
        destination={destination}
        name={name}
        duration={duration}
      />
    </MapContainer>
  );
};

export default Map;
