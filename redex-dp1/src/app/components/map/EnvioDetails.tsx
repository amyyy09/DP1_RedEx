import React, { useState } from "react";
import { arrayToTime } from "@/app/utils/timeHelper";
import "../../styles/packageDetails.css";
import { citiesByCode } from "@/app/data/cities";
import { transformCode } from "@/app/utils/rutaHelper";

interface EnvioDetailsProps {
  paquetes: any[];
  onClose?: () => void;
}

const EnvioDetails: React.FC<EnvioDetailsProps> = ({ paquetes, onClose }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 2;

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
            <strong>Envío: </strong>
            {paquetes[0].id.split("-")[0]}
          </h2>
          {onClose && (
            <button className="close-button-detalles" onClick={onClose}>
              &times;
            </button>
          )}
        </div>
        <div className="divider"></div>
        <div className="package-details-header">
          <h2>
            <strong>Origen: </strong>
            {citiesByCode[paquetes[0].aeropuertoOrigen].name}
          </h2>
        </div>
        <div className="package-details-header">
          <h2>
            <strong>Destino: </strong>
            {citiesByCode[paquetes[0].aeropuertoDestino].name}
          </h2>
        </div>
        <div className="package-details-header">
          <h2>
            <strong>Hora de registro: </strong>
            {arrayToTime(paquetes[0].horaInicio).toLocaleString(undefined, {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
              hour: "2-digit",
              minute: "2-digit",
              second: "2-digit",
              hour12: false,
            })}
          </h2>
        </div>
        <div className="divider"></div>
        <div className="package-details-header">
          <h2>
            <strong>Paquetes: </strong>
          </h2>
        </div>
        <div className="table-container">
          <table className="package-table">
            <thead>
              <tr>
                <th style={{ width: "25px" }}>N° Paquete</th>
                <th style={{ width: "100px" }}>Ruta asignada</th>
                <th style={{ width: "50px" }}>Ubicación actual</th>
              </tr>
            </thead>
            <tbody>
              {currentItems.map((paquete, index) => {
                return (
                  <tr key={index}>
                    <td>{paquete.id.split("-")[1]}</td>
                    <td>{transformCode(paquete.ruta)}</td>
                    <td>
                      {/* {/^\d+$/.test(paquete.ubicacion)
                        ? `Vuelo ${paquete.ubicacion}`
                        : citiesByCode[paquete.ubicacion]
                        ? citiesByCode[paquete.ubicacion].name
                        : paquete.ubicacion} */}
                        {paquete.ubicacion}
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

export default EnvioDetails;
