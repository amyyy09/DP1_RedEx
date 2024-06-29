import React from "react";
import { Vuelo } from "@/app/types/Planes";
import { arrayToTime } from "@/app/utils/timeHelper";
import '../../styles/packageDetails.css';

interface PackageDetailsProps {
  vuelo: Vuelo;
  selectedPackageId: string | null;
  onClose: () => void;
}

const PackageDetails: React.FC<PackageDetailsProps> = ({ vuelo, selectedPackageId, onClose }) => {
  return (
    <div className="package-details">
      <div className="package-details-header">
        <h2>Detalles de Paquetes</h2>
        <button className="close-button-detalles" onClick={onClose}>&times;</button>
      </div>
      <div className="divider"></div> {/* Línea debajo del título */}
      <ul className="package-list">
        {vuelo.paquetes.map((paquete, index) => (
          <li
            key={index}
            className={`package-item ${paquete.id === selectedPackageId ? "selected" : ""}`}
          >
            <p
              style={{
                fontWeight: paquete.id === selectedPackageId ? "bold" : "normal",
                color: paquete.id === selectedPackageId ? "red" : "black",
              }}
            >
              <strong>ID:</strong> {paquete.id}, 
              <strong> Origen:</strong> {paquete.aeropuertoOrigen}, 
              <strong> Destino:</strong> {paquete.aeropuertoDestino}, 
              <strong> Hora de Registro:</strong> {arrayToTime(paquete.horaInicio).toLocaleString(undefined, {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
                hour12: false,
              })}
            </p>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default PackageDetails;
