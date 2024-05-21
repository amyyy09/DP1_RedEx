'use client'

import React, { useMemo, useState } from 'react';
import dynamic from 'next/dynamic';

const Home: React.FC = () => {
  const [planes, setPlanes] = useState([
    {
      origin: { lat: 40.7128, lng: -74.0060 },
      destination: { lat: 34.0522, lng: -118.2437 },
      name: 'Flight 123',
      duration: 5000, // 5 seconds
    },
    {
      origin: { lat: 51.5074, lng: -0.1278 },
      destination: { lat: 55.7558, lng: 37.6176 },
      name: 'Flight 456',
      duration: 3000, // 3 seconds
    }
    // Add more plane objects here
  ]);

  const Map = useMemo(() => dynamic(
    () => import('./components/Map'),
    { 
      loading: () => <p>A map is loading</p>,
      ssr: false
    }
  ), []);

  return (
    <div>
      <h1>Plane Movements</h1>
      <Map planes={planes}/>
    </div>
  );
};

export default Home;