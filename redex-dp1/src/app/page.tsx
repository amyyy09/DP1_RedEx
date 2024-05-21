import React, { useMemo } from 'react';
import dynamic from 'next/dynamic';
import Topbar from './components/layout/Topbar';
import Sidebar from './components/layout/Sidebar';

const Home: React.FC = () => {
  const Map = useMemo(() => dynamic(
    () => import('./components/Map'),
    { 
      loading: () => <p>A map is loading</p>,
      ssr: false
    }
  ), []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column' }}>
      <Topbar />
      <div style={{ display: 'flex', flex: 1 }}>
        <Sidebar />
      <Map />
      </div>
    </div>
  );
};

export default Home;