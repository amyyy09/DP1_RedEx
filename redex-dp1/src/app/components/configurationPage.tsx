import React from "react";
import "../styles/ConfigPage.css"; // Asegúrate de que la ruta sea correcta
import TitleWithIcon from "./registro/TitleWithIcon";
import { Toaster } from "react-hot-toast";

const ConfigPage: React.FC = () => {
  return (
    <div className="config-container">
      <TitleWithIcon
        name="Configuración y Carga de Datos"
        icon="/icons/caja.png"
      />
      <Toaster position="top-right" reverseOrder={false} />
      <div className="config-buttons">
        <div className="config-button">
          <img src="config.png" alt="Carga de Planes de Vuelo" />
          <p>Carga de Planes de Vuelo</p>
        </div>
        <div className="config-button">
          <img src="config.png" alt="Carga de Data Historica" />
          <p>Agregar Aeropuerto</p>
        </div>
        <div className="config-button">
          <img src="config.png" alt="Configuración de Parámetros" />
          <p>Configuración de Parámetros</p>
        </div>
      </div>
    </div>
  );
};

export default ConfigPage;
