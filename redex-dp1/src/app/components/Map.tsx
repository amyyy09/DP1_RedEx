// components/PlaneMap.tsx
'use client';

import React from 'react';
import { MapContainer, TileLayer, Marker, Popup, ZoomControl } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import Sidebar from './Sidebar';
import Topbar from './Topbar';


const customIcon = new L.Icon({
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
  shadowSize: [41, 41],
});


const cities = [
  { name: "Bogota", coords: [4.711, -74.0721] },
  { name: "Quito", coords: [-0.1807, -78.4678] },
  { name: "Caracas", coords: [10.4806, -66.9036] },
  { name: "Brasilia", coords: [-15.8267, -47.9218] },
  { name: "Lima", coords: [-12.0464, -77.0428] },
  { name: "La Paz", coords: [-16.5000, -68.1500] },
  { name: "Santiago de Chile", coords: [-33.4489, -70.6693] },
  { name: "Buenos Aires", coords: [-34.6037, -58.3816] },
  { name: "Asunción", coords: [-25.2637, -57.5759] },
  { name: "Montevideo", coords: [-34.9011, -56.1645] },
  { name: "Tirana", coords: [41.3275, 19.8187] },
  { name: "Berlin", coords: [52.5200, 13.4050] },
  { name: "Viena", coords: [48.2082, 16.3738] },
  { name: "Bruselas", coords: [50.8503, 4.3517] },
  { name: "Minsk", coords: [53.9045, 27.5615] },
  { name: "Sofia", coords: [42.6977, 23.3219] },
  { name: "Praga", coords: [50.0755, 14.4378] },
  { name: "Zagreb", coords: [45.8150, 15.9819] },
  { name: "Copenhague", coords: [55.6761, 12.5683] },
  { name: "Amsterdam", coords: [52.3676, 4.9041] },
  { name: "Delhi", coords: [28.7041, 77.1025] },
  { name: "Seul", coords: [37.5665, 126.9780] },
  { name: "Bangkok", coords: [13.7563, 100.5018] },
  { name: "Dubai", coords: [25.276987, 55.296249] },
  { name: "Beijing", coords: [39.9042, 116.4074] },
  { name: "Tokyo", coords: [35.6895, 139.6917] },
  { name: "Kuala Lumpur", coords: [3.1390, 101.6869] },
  { name: "Singapore", coords: [1.3521, 103.8198] },
  { name: "Jakarta", coords: [-6.2088, 106.8456] },
  { name: "Manila", coords: [14.5995, 120.9842] },
];

const Map: React.FC = () => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column' }}>
      <Topbar />
      <div style={{ display: 'flex', flex: 1 }}>
        <Sidebar />
        <MapContainer 
          center={[0, 0]} 
          zoom={3} 
          style={{ height: 'calc(100vh - 50px)', width: 'calc(100vw - 50px)' }}
          zoomControl={false}
        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          />
          {cities.map((city, idx) => (
            <Marker key={idx} position={city.coords} icon={customIcon}>
              <Popup>
                {city.name}
              </Popup>
            </Marker>
          ))}
          {/* Add custom zoom control */}
          <ZoomControl position="topright" />
        </MapContainer>
      </div>
    </div>
  );
};

export default Map;
