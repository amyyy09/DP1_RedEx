
// components/Sidebar.tsx
import React from 'react';
import '../../styles/Sidebar.css';
const Sidebar: React.FC = () => {
  return (
    <div className="sidebar">
      <div className="sidebar-item">
        <img src="./icons/hogar.png" alt="Home" />
      </div>
      <div className="sidebar-item active">
        <img src="./icons/modo-vuelo.png" alt="Plane" />
      </div>
      <div className="sidebar-item">
        <img src="./icons/radar.png" alt="Power" />
      </div>
      <div className="sidebar-item">
        <img src="./icons/paquete.png" alt="Package" />
      </div>
      <div className="sidebar-item">
        <img src="./icons/actualizacion.png" alt="Loading" />
      </div>
      <div className="sidebar-item">
        <img src="./icons/salir.png" alt="Logout" />
      </div>
    </div>
  );
};

export default Sidebar;
