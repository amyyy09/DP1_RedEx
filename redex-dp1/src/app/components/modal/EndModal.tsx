// components/modal/EndModal.tsx

import React from "react";

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

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>Fin de la simulación</h2>
          <button className="close-button" onClick={onClose}>
            &times;
          </button>
        </div>
        <div className="modal-body">
          <p>
            La simulación inició el{" "}
            <strong>
              {new Date(simulatedStartDate).toLocaleDateString(undefined, {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
              })}
            </strong>{" "}
            a las <strong>{simulatedStartHour}</strong>.
          </p>
          <p>
            La simulación finalizó el <strong>{endDate}</strong> a las{" "}
            <strong>{timeString}</strong>.
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
        </div>
      </div>
    </div>
  );
};

export default EndModal;
