import React, { useMemo } from 'react';
import dynamic from 'next/dynamic';

const Home: React.FC = () => {
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
      <Map />
    </div>
  );
};

export default Home;