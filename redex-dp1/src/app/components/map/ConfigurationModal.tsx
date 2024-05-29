'use client';

import React, { useState } from 'react';
import '../../styles/ConfigurationModal.css';

interface ConfigurationModalProps {
  onApply: () => void;
}

const ConfigurationModal: React.FC<ConfigurationModalProps> = ({ onApply }) => {
  const [simulationMode, setSimulationMode] = useState('');
  const [startDate, setStartDate] = useState('');
  const [startTime, setStartTime] = useState('');

  const handleModeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSimulationMode(e.target.value);
  };

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setStartDate(e.target.value);
  };

  
  const handleTimeChange = (event) => {
    setStartTime(event.target.value);
  };


  const handleSubmit = () => {
    console.log(`Simulation Mode: ${simulationMode}, Start Date: ${startDate}`);

    onApply();
  };

  const formatDateTime = (date, time) => {
    const [hours, minutes] = time.split(':');
    date.setHours(hours, minutes);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const formattedHours = String(date.getHours()).padStart(2, '0');
    const formattedMinutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = '00';
    return `${year}-${month}-${day}T${formattedHours}:${formattedMinutes}:${seconds}`;
  };

  const handleApplyClick = async () => {
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

      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      const responseData = await response.json();
      console.log('Response:', responseData);
      // Maneja la respuesta según sea necesario
    } catch (error) {
      console.error('Error:', error);
    }
  };

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