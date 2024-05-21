// components/Topbar.tsx
import React from "react";
import "../../styles/Topbar.css";

const Topbar: React.FC = () => {
  return (
    <div className="topbar">
      <div className="topbar-item-log">
        <img src="./redex.png" alt="Logo" />
      </div>
      <div className="topbar-item">
        <img src="./icons/buscar.png" alt="Buscar Paquete" />
        <span>Buscar Paquete</span>
      </div>
      <div className="topbar-item">
        <img src="./icons/caja.png" alt="Estado Envíos" />
        <span>Estado Envíos</span>
      </div>
      <div className="topbar-item">
        <img src="./icons/rastro.png" alt="Rastrear Vuelo" />
        <span>Rastrear Vuelo</span>
      </div>
      <div className="topbar-item">
        <img src="./icons/cajas.png" alt="Almacenes" />
        <span>Almacenes</span>
      </div>
    </div>
  );
};

export default Topbar;
