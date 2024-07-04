import React, { useState } from "react";
import { arrayToTime } from "@/app/utils/timeHelper";
import "../../styles/packageDetails.css";
import { citiesByCode } from "@/app/data/cities";

interface PackageDetailsProps {
  paquetes: any[];
  selectedPackageId: string | null;
  onClose?: () => void;
}

const PackageDetails: React.FC<PackageDetailsProps> = ({
  paquetes,
  selectedPackageId,
  onClose,
}) => {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = paquetes.slice(indexOfFirstItem, indexOfLastItem);

  const totalPages = Math.ceil(paquetes.length / itemsPerPage);

  const handleClickNext = () => {
    if (currentPage < totalPages) {
      setCurrentPage(currentPage + 1);
    }
  };

  const handleClickPrev = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1);
    }
  };

  return (
    <div className="package-details-fixed">
      <div className="package-details">
        <div className="package-details-header">
          <h2>
            <strong>Detalles de Envios</strong>
          </h2>
          {onClose && (
            <button className="close-button-detalles" onClick={onClose}>
              &times;
            </button>
          )}
        </div>
        <div className="divider"></div>
        <div className="table-container">
          <table className="package-table">
            <thead>
              <tr>
                <th style={{ width: "50px" }}>Código Envío</th>
                <th style={{ width: "25px" }}>N° Paquete</th>
                <th style={{ width: "50px" }}>Origen</th>
                <th style={{ width: "50px" }}>Destino</th>
                <th style={{ width: "50px" }}>Hora de Registro</th>
              </tr>
            </thead>
            <tbody>
              {currentItems.map((paquete, index) => {
                const isSelected = paquete.id === selectedPackageId;
                return (
                  <tr key={index} className={isSelected ? "selected" : ""}>
                    <td style={{ color: isSelected ? "red" : "black" }}>
                      {paquete.id.split("-")[0]}
                    </td>
                    <td style={{ color: isSelected ? "red" : "black" }}>
                      {paquete.id.split("-")[1]}
                    </td>
                    <td style={{ color: isSelected ? "red" : "black" }}>
                      {citiesByCode[paquete.aeropuertoOrigen].name}
                    </td>
                    <td style={{ color: isSelected ? "red" : "black" }}>
                      {citiesByCode[paquete.aeropuertoDestino].name}
                    </td>
                    <td style={{ color: isSelected ? "red" : "black" }}>
                      {arrayToTime(paquete.horaInicio).toLocaleString(
                        undefined,
                        {
                          day: "2-digit",
                          month: "2-digit",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                          second: "2-digit",
                          hour12: false,
                        }
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
        <div className="pagination-controls">
          <button onClick={handleClickPrev} disabled={currentPage === 1}>
            Anterior
          </button>
          <span>
            {currentPage} de {totalPages}
          </span>
          <button
            onClick={handleClickNext}
            disabled={currentPage >= totalPages}
          >
            Siguiente
          </button>
        </div>
      </div>
    </div>
  );
};

export default PackageDetails;
