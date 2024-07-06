// components/modal/EndModal.tsx

import React, { useState } from "react";
import "../../styles/endModal.css";

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
}

const EndModal: React.FC<EndModalProps> = ({
  onClose,
  simulatedStartDate,
  simulatedStartHour,
  simulatedEndDate,
  summary,
}) => {
  const date = new Date(simulatedEndDate);
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const timeString = `${hours}:${minutes}`;

  const endDate = date.toLocaleDateString('en', {
    month: "2-digit",
    day: "2-digit",
    year: "numeric",
  });

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const handleNextPage = () => {
    setCurrentPage((prevPage) => prevPage + 1);
  };

  const handlePreviousPage = () => {
    setCurrentPage((prevPage) => Math.max(prevPage - 1, 1));
  };

  const tableData = [
    { codigoEnvio: "OERK000008589", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:50:00" },
    { codigoEnvio: "OERK000008588", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:36:00" },
    { codigoEnvio: "OERK000008589", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:50:00" },
    { codigoEnvio: "OERK000008588", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:36:00" },
    { codigoEnvio: "OERK000008589", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:50:00" },
    { codigoEnvio: "OERK000008588", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:36:00" },
    { codigoEnvio: "OERK000008589", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:50:00" },
    { codigoEnvio: "OERK000008588", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:36:00" },
    { codigoEnvio: "OERK000008589", numeroPaquete: 1, origen: "Riad", destino: "Zagreb", rutaSeleccionada: "Riad-Delhi-Zagreb", horaRegistro: "06/07/2024, 20:50:00" },
    { codigoEnvio: "OERK000008587", numeroPaquete: 1, origen: "Riad", destino: "Delhi", rutaSeleccionada: "Riad-Zagreb-Delhi", horaRegistro: "06/07/2024, 20:20:00" }
  ];

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
            Resumen de la simulación:
          </h3>
          <ul>
            <li>Número de vuelos: {summary.numeroVuelos}</li>
            <li>Total de paquetes: {summary.totalPaquetes}</li>
            <li>Aeropuerto más frecuente: {summary.aeropuertoMasFrecuente}</li>
            <li>Hora con más vuelos: {summary.horaConMasVuelos}</li>
            <li>Promedio de paquetes por vuelo: {summary.promedioPaquetesPorVuelo.toFixed(2)}</li>
            <li>Tiempo promedio de vuelo: {summary.tiempoPromedioVuelo.toFixed(2)} minutos</li>
          </ul>
          <h3 style={{ fontSize: "1.5em", fontWeight: "bold", marginTop: "20px" }}>
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
                  <td>{row.codigoEnvio}</td>
                  <td>{row.numeroPaquete}</td>
                  <td>{row.origen}</td>
                  <td>{row.destino}</td>
                  <td>{row.rutaSeleccionada}</td>
                  <td>{row.horaRegistro}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="pagination">
            <button onClick={handlePreviousPage} disabled={currentPage === 1}>
              Anterior
            </button>
            <span>
              Página {currentPage} de {Math.ceil(tableData.length / itemsPerPage)}
            </span>
            <button
              onClick={handleNextPage}
              disabled={currentPage === Math.ceil(tableData.length / itemsPerPage)}
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
