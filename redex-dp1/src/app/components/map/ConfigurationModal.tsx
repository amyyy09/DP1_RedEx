'use client';

import React, { useState } from 'react';
import '../../styles/ConfigurationModal.css';

interface ConfigurationModalProps {
  onApply: () => void;
}

const ConfigurationModal: React.FC<ConfigurationModalProps> = ({ onApply }) => {
  const [simulationMode, setSimulationMode] = useState('');
  const [startDate, setStartDate] = useState('');

  const handleModeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSimulationMode(e.target.value);
  };

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setStartDate(e.target.value);
  };

  const handleSubmit = () => {
    console.log(`Simulation Mode: ${simulationMode}, Start Date: ${startDate}`);

    onApply();
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

          <button className="apply-button" onClick={handleSubmit}>APLICAR</button>
        </div>
      </div>
    </div>
  );
};

export default ConfigurationModal;