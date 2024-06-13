import React from "react";
import "@/styles/ConfigPage.css"; // Asegúrate de que la ruta sea correcta

const ConfigPage: React.FC = () => {
  return (
    <div className="config-container">
      <h1 className="config-title">Configuración y Carga de Datos</h1>
      <div className="config-buttons">
        <div className="config-button">
          <img src="/icons/flight-plans.png" alt="Carga de Planes de Vuelo" />
          <p>Carga de Planes de Vuelo</p>
        </div>
        <div className="config-button">
          <img src="/icons/warehouses.png" alt="Carga de Almacenes" />
          <p>Carga de Almacenes</p>
        </div>
        <div className="config-button">
          <img src="/icons/historical-data.png" alt="Carga de Data Historica" />
          <p>Carga de Data Historica</p>
        </div>
        <div className="config-button">
          <img src="/icons/parameters.png" alt="Configuración de Parámetros" />
          <p>Configuración de Parámetros</p>
        </div>
      </div>
    </div>
  );
};

export default ConfigPage;
