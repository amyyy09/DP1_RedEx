// components/modal/EndModal.tsx

import React, { useState } from "react";
import "../../styles/endModal.css";
import { Airport } from "@/app/types/Planes";
import { citiesByCode } from "@/app/data/cities";
import { transformCode } from "@/app/utils/rutaHelper";
import { arrayToTime } from "@/app/utils/timeHelper";

interface EndModalProps {
  onClose: () => void;
  simulatedStartDate: string;
  simulatedStartHour: string;
  simulatedEndDate: string;
  summary: {
    numeroVuelos: number;
    totalPaquetes: number;
    aeropuertoMasFrecuente: string;
    horaConMasVuelos: number;
    promedioPaquetesPorVuelo: number;
    tiempoPromedioVuelo: number;
  };
  lastPlan: React.RefObject<Airport[]>;
}

const EndModal: React.FC<EndModalProps> = ({
  onClose,
  simulatedStartDate,
  simulatedStartHour,
  simulatedEndDate,
  summary,
  lastPlan,
}) => {
  const date = new Date(simulatedEndDate);
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const timeString = `${hours}:${minutes}`;

  const endDate = date.toLocaleDateString("en", {
    month: "2-digit",
    day: "2-digit",
    year: "numeric",
  });

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 2;

  const handleNextPage = () => {
    setCurrentPage((prevPage) => prevPage + 1);
  };

  const handlePreviousPage = () => {
    setCurrentPage((prevPage) => Math.max(prevPage - 1, 1));
  };

  function getAllPaquetesFromAirports(airports: Airport[]) {
    let allPaquetes: any[] = [];

    airports.forEach((airport) => {
      allPaquetes.push(...airport.almacen.paquetes);
    });

    return allPaquetes;
  }

  const tableData = getAllPaquetesFromAirports(lastPlan.current || []);

  const startIndex = (currentPage - 1) * itemsPerPage;
  const selectedItems = tableData.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className="modal-overlay-endModal">
      <div className="modal-content-endModal">
        <div className="modal-header-endModal">
          <h2>Fin de la simulación</h2>
          <button className="close-button-endModal" onClick={onClose}>
            &times;
          </button>
        </div>
        <div className="modal-body-endModal">
          <p>
            La simulación inició el{" "}
            <strong>
              {new Date(
                new Date(simulatedStartDate).setDate(
                  new Date(simulatedStartDate).getDate() + 1
                )
              ).toLocaleDateString(undefined, {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
              })}
            </strong>{" "}
            a las <strong>{simulatedStartHour}</strong>.
          </p>
          <br />
          <h3 style={{ fontSize: "1.5em", fontWeight: "bold" }}>
            Resumen de la última planificación:
          </h3>
          <ul>
            <li>Número de vuelos: {summary.numeroVuelos}</li>
            <li>Total de paquetes: {summary.totalPaquetes}</li>
            <li>Aeropuerto más frecuente: {summary.aeropuertoMasFrecuente}</li>
            <li>Hora con más vuelos: {summary.horaConMasVuelos}</li>
            <li>
              Promedio de paquetes por vuelo:{" "}
              {summary.promedioPaquetesPorVuelo.toFixed(2)}
            </li>
            <li>
              Tiempo promedio de vuelo: {summary.tiempoPromedioVuelo.toFixed(2)}{" "}
              minutos
            </li>
          </ul>
          <h3
            style={{ fontSize: "1.5em", fontWeight: "bold", marginTop: "20px" }}
          >
            Detalles de Envíos:
          </h3>
          <table className="table-endModal">
            <thead>
              <tr>
                <th>Código Envío</th>
                <th>N° Paquete</th>
                <th>Origen</th>
                <th>Destino</th>
                <th>Ruta Seleccionada</th>
                <th>Hora de Registro</th>
              </tr>
            </thead>
            <tbody>
              {selectedItems.map((row, index) => (
                <tr key={index}>
                  <td>{row.id.split("-")[0]}</td>
                  <td>{row.id.split("-")[1]}</td>
                  <td>{citiesByCode[row.aeropuertoOrigen].name}</td>
                  <td>{citiesByCode[row.aeropuertoDestino].name}</td>
                  <td>{transformCode(row.ruta)}</td>
                  <td>
                    {arrayToTime(row.horaInicio).toLocaleString(undefined, {
                      day: "2-digit",
                      month: "2-digit",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                      second: "2-digit",
                      hour12: false,
                    })}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="pagination">
            <button onClick={handlePreviousPage} disabled={currentPage === 1}>
              Anterior
            </button>
            <span>
              Página {currentPage} de{" "}
              {Math.ceil(tableData.length / itemsPerPage)}
            </span>
            <button
              onClick={handleNextPage}
              disabled={
                currentPage === Math.ceil(tableData.length / itemsPerPage)
              }
            >
              Siguiente
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EndModal;
