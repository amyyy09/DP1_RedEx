'use client';

import React, { useMemo, useState } from 'react';
import dynamic from 'next/dynamic';
import Topbar from '../app/components/layout/Topbar';
import Sidebar from '../app/components/layout/Sidebar';
import ConfigurationModal from '../app/components/map/ConfigurationModal';

const Home: React.FC = () => {
  const [planes, setPlanes] = useState([
    {
      "origin": {
        "name": "Bogota",
        "coords": {
          "lat": 4.711,
          "lng": -74.0721
        },
        "code": "SKBO",
        "GMT": -5,
        "capacidad": 430
      },
      "destiny": {
        "name": "Quito",
        "coords": {
          "lat": -0.1807,
          "lng": -78.4678
        },
        "code": "SEQM",
        "GMT": -5,
        "capacidad": 410
      },
      "departureTime": "22:34",
      "arrivalTime": "00:21",
      "capacidad": 300
    },
    {
      "origin": {
        "name": "Caracas",
        "coords": {
          "lat": 10.4806,
          "lng": -66.9036
        },
        "code": "SVMI",
        "GMT": -4,
        "capacidad": 400
      },
      "destiny": {
        "name": "Minsk",
        "coords": {
          "lat": 53.9045,
          "lng": 27.5615
        },
        "code": "UMMS",
        "GMT": 3,
        "capacidad": 400
      },
      "departureTime": "08:54",
      "arrivalTime": "11:42",
      "capacidad": 360
    },
    // Add more plane objects here
  ]);
  const [showModal, setShowModal] = useState(true);
  const [startSimulation, setStartSimulation] = useState(false);

  const Map = useMemo(
    () =>
      dynamic(() => import('../app/components/map/Map'), {
        loading: () => <p>A map is loading</p>,
        ssr: false,
      }),
    []
  );

  const handleApplyConfiguration = () => {
    setShowModal(false);
    setStartSimulation(true);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column' }}>
      <Topbar />
      <div style={{ display: 'flex', flex: 1 }}>
        <Sidebar />
        <Map planes={startSimulation ? planes : []} /> {/* Pass planes only if simulation starts */}
        {showModal && <ConfigurationModal onApply={handleApplyConfiguration} />} {/* Show modal */}
      </div>
    </div>
  );
};

export default Home;