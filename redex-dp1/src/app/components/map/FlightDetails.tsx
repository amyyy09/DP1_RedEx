import React, { forwardRef } from 'react';
import { citiesByCode } from '@/app/data/cities';
import { Vuelo } from '@/app/types/Planes';
import { arrayToTime } from '@/app/utils/timeHelper';
import '../../styles/flightDetails.css';

interface FlightDetailsProps {
  vuelo: Vuelo;
  onClose: () => void;
  showPackages: boolean;
  togglePackages: () => void;
}

const FlightDetails = forwardRef<HTMLDivElement, FlightDetailsProps>(({
  vuelo, onClose, showPackages, togglePackages
}, ref) => {
  return (
    <div className="flight-details" ref={ref}>
      <button onClick={onClose} className="close-button">X</button>
      <h2 style={{ fontSize: "1.5em", fontWeight: "bold" }}>Detalles de vuelo</h2>
      <p><strong>Plan de vuelo:</strong> {citiesByCode[vuelo.aeropuertoOrigen].name} - {citiesByCode[vuelo.aeropuertoDestino].name}</p>
      <p><strong>Hora de salida:</strong> {arrayToTime(vuelo.horaSalida).toLocaleString(undefined, {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: false,
      })} (
        {citiesByCode[vuelo.aeropuertoOrigen].GMT > 0
          ? `+${citiesByCode[vuelo.aeropuertoOrigen].GMT}`
          : citiesByCode[vuelo.aeropuertoOrigen].GMT}
        )</p>
      <p><strong>Hora de llegada:</strong> {arrayToTime(vuelo.horaLlegada).toLocaleString(undefined, {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: false,
      })} (
        {citiesByCode[vuelo.aeropuertoDestino].GMT > 0
          ? `+${citiesByCode[vuelo.aeropuertoDestino].GMT}`
          : citiesByCode[vuelo.aeropuertoDestino].GMT}
        )</p>
      <p><strong>Capacidad:</strong> {vuelo.capacidad}</p>
      <p><strong>Cantidad de paquetes:</strong> {vuelo.cantPaquetes}</p>
      <button onClick={togglePackages} className="button_plane">
        {showPackages ? "Ocultar Envíos" : "Mostrar Envíos"}
      </button>
    </div>
  );
});

export default FlightDetails;
