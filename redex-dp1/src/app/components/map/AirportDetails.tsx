// components/AirportDetails.tsx
import React from 'react';
import { Airport } from "@/app/types/Planes";
import { citiesByCode } from "@/app/data/cities";
import '../../styles/airportDetails.css';

interface AirportDetailsProps {
  city: typeof citiesByCode[number];
  cityData: Airport | null;
  onClose: () => void;
  onShowPackages: () => void;
  showPackages: boolean;
}

const AirportDetails: React.FC<AirportDetailsProps> = ({
  city, cityData, onClose, onShowPackages, showPackages
}) => {
  return (
    <div className="airport-details-fixed">
      <div className="airport-details">
        <button onClick={onClose} className="close-button">x</button>
        <h2 style={{ fontSize: "1.5em", fontWeight: "bold" }}>{city.name}</h2>
        <p><strong>Capacidad de Almacenamiento: </strong>{city.capacidad + 1000}</p>
        {cityData && (
          <>
            <p><strong>Cantidad de paquetes: </strong>{cityData.almacen.cantPaquetes}</p>
            <button onClick={onShowPackages} className="button_plane">
              {showPackages ? "Ocultar Envíos" : "Mostrar Envíos"}
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default AirportDetails;