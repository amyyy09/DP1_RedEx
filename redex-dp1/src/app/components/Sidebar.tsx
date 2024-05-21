
// components/Sidebar.tsx
import React from 'react';
import '../styles/Sidebar.css'; // Import the CSS for styling
const Sidebar: React.FC = () => {
  return (
    <div className="sidebar">
      <div className="sidebar-item">
        <img src="./hogar.png" alt="Home" />
      </div>
      <div className="sidebar-item active">
        <img src="./modo-vuelo.png" alt="Plane" />
      </div>
      <div className="sidebar-item">
        <img src="./radar.png" alt="Power" />
      </div>
      <div className="sidebar-item">
        <img src="./paquete.png" alt="Package" />
      </div>
      <div className="sidebar-item">
        <img src="./actualizacion.png" alt="Loading" />
      </div>
      <div className="sidebar-item">
        <img src="./salir.png" alt="Logout" />
      </div>
    </div>
  );
};

export default Sidebar;
