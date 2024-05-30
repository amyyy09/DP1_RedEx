'use client';
import React, { useState } from 'react';
import '../../styles/ConfigurationModal.css';

class Vuelo {
  cantPaquetes: number;
  capacidad: number;
  status: number;
  indexPlan: number;
  horaSalida: Date;
  horaLlegada: Date;
  aeropuertoOrigen: string;
  aeropuertoDestino: string;
  idVuelo: string;

  constructor(data: any) {
    this.cantPaquetes = data.cantPaquetes;
    this.capacidad = data.capacidad;
    this.status = data.status;
    this.indexPlan = data.indexPlan;
    this.horaSalida = new Date(data.horaSalida[0], data.horaSalida[1] - 1, data.horaSalida[2], data.horaSalida[3], data.horaSalida[4]);
    this.horaLlegada = new Date(data.horaLlegada[0], data.horaLlegada[1] - 1, data.horaLlegada[2], data.horaLlegada[3], data.horaLlegada[4]);
    this.aeropuertoOrigen = data.aeropuertoOrigen;
    this.aeropuertoDestino = data.aeropuertoDestino;
    this.idVuelo = data.idVuelo;
  }
}

interface ConfigurationModalProps {
  onApply: () => void;
}

const ConfigurationModal: React.FC<ConfigurationModalProps> = ({ onApply }) => {
  const [simulationMode, setSimulationMode] = useState('');
  const [startDate, setStartDate] = useState('');
  const [startTime, setStartTime] = useState('');
  const [loading, setLoading] = useState(false);
  const [vuelos, setVuelos] = useState<Vuelo[]>([]);

  const handleModeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSimulationMode(e.target.value);
  };

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setStartDate(e.target.value);
  };

  
  const handleTimeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setStartTime(e.target.value);
  };


  const handleSubmit = () => {
    console.log(`Simulation Mode: ${simulationMode}, Start Date: ${startDate}`);

    onApply();
  };

  const formatDateTime = (date: Date, time: string) => {
    const [hours, minutes] = time.split(':');
    date.setHours(parseInt(hours), parseInt(minutes));
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const formattedHours = String(date.getHours()).padStart(2, '0');
    const formattedMinutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = '00';
    return `${year}-${month}-${day}T${formattedHours}:${formattedMinutes}:${seconds}`;
  };

  const handleApplyClick = async () => {
    setLoading(true);
    const selectedDate = new Date(startDate);
    const formattedDate = formatDateTime(selectedDate, startTime);

    // Definir los datos JSON para la solicitud
    const data = {
      fechahora: formattedDate,
      aeropuertos: [],
      vuelos: []
    };

    try {
      const response = await fetch('http://localhost:8080/api/pso', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });

      console.log('Response:', response);

      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      const responseData = await response.json();
      const vuelosData: Vuelo[] = [];

      // Procesar los vuelos desde el responseData
      for (const key in responseData) {
        if (responseData.hasOwnProperty(key)) {
          const paquete = responseData[key];
          paquete.vuelos.forEach((vueloData: Vuelo) => {
            const vuelo = new Vuelo({
              ...vueloData,
              aeropuertoOrigen: paquete.aeropuertoOrigen,
              aeropuertoDestino: paquete.aeropuertoDestino
            });
            vuelosData.push(vuelo);
          });
        }
      }

      setVuelos(vuelosData);
      console.log('Vuelos:', vuelosData);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false); // Desactivar estado de cargando
    }
  };

  if (loading) {
    return (
      <div className="loading-overlay">
        <div className="loading-spinner">Cargando...</div>
      </div>
    );
  }

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>Configuración de Simulación</h2>
          <button className="close-button">&times;</button>
        </div>
        <div className="modal-body">
          <label htmlFor="simulation-mode">Modo de Simulación</label>
          <select 
            id="simulation-mode" 
            value={simulationMode} 
            onChange={handleModeChange}
          >
            <option value="">-</option>
            <option value="semanal">Semanal</option>
            <option value="sin-detenimiento">Sin Detenimiento</option>
          </select>

          <label htmlFor="start-date">Fecha de Inicio</label>
          <input 
            type="date" 
            id="start-date" 
            value={startDate} 
            onChange={handleDateChange} 
          />

          <label htmlFor="start-time">Hora de Inicio</label>
          <input 
            type="time" 
            id="start-time" 
            value={startTime} 
            onChange={handleTimeChange} 
          />

          <button className="apply-button" onClick={handleApplyClick}>APLICAR</button>
        </div>
      </div>
    </div>
  );
};

export default ConfigurationModal;