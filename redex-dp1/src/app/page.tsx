'use client';

import React, { useMemo, useState } from 'react';
import dynamic from 'next/dynamic';
import Topbar from '../app/components/layout/Topbar';
import Sidebar from '../app/components/layout/Sidebar';
import ConfigurationModal from '../app/components/map/ConfigurationModal';

const Home: React.FC = () => {
  const [planes, setPlanes] = useState([
    {
      origin: { lat: 25.2048, lng: 55.2708 }, // Dubai
      destination: { lat: 1.3521, lng: 103.8198 }, // Singapore
      name: 'Flight 123',
      duration: 7000, // 5 seconds
    },
    {
      origin: { lat: -12.0464, lng: -77.0428 },
      destination: { lat: 50.8503, lng: 4.3517 },
      name: 'Flight 456',
      duration: 10000, // 3 seconds
    },

    // plane.origin = flightPlans[index].origin.coords;
    // plane.destination = flightPlans[index].destiny.coords;
    // plane.name = `Flight ${index}`;
    /*
       inicio =  flightPlans[index].departureTime.toNumber() // se tiene convertir a gmt 0 de acuerdo a lo que diga flightPlans[index].origin.GMT
       fin = flightPlans[index].arrivalTime.toNumber() // se tiene convertir a gmt 0 de acuerdo a lo que diga flightPlans[index].destiny.GMT

       plane.duration = fin - duration (segundos o milisegundos -> each real-time second should be equivalent to 4.2 simulated minutes.)
       

    */ 
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