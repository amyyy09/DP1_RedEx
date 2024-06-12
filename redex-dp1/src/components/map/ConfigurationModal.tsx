"use client";
import React, { Dispatch, SetStateAction, useState } from "react";
import "../../styles/ConfigurationModal.css";
import { Vuelo } from "../../types/Planes";

interface ConfigurationModalProps {
  onApply: () => void;
  startDate: string;
  setStartDate: Dispatch<SetStateAction<string>>;
  startTime: string;
  setStartTime: Dispatch<SetStateAction<string>>;
  simulationMode: string;
  setSimulationMode: Dispatch<SetStateAction<string>>;
  vuelos: React.RefObject<Vuelo[]>;
  loading: boolean;
  setLoading: Dispatch<SetStateAction<boolean>>;
}

const ConfigurationModal: React.FC<ConfigurationModalProps> = ({
  onApply,
  startDate,
  setStartDate,
  startTime,
  setStartTime,
  simulationMode,
  setSimulationMode,
  vuelos,
  loading,
  setLoading,
}) => {
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
    const [hours, minutes] = time.split(":");
    date.setHours(parseInt(hours), parseInt(minutes));
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const formattedHours = String(date.getHours()).padStart(2, "0");
    const formattedMinutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = "00";
    return `${year}-${month}-${day}T${formattedHours}:${formattedMinutes}:${seconds}`;
  };

  const handleApplyClick = async () => {
    const numberOfCalls = 84; // Número de llamadas a la API
    const intervalHours = 2; // Intervalo de horas entre cada llamada

    // Formatear la fecha inicial
    const [year, month, day] = startDate.split('-').map(Number);
    const selectedDate = new Date(year, month - 1, day);
    let [startHours, startMinutes] = startTime.split(':').map(Number);

    // Lista para almacenar todas las respuestas
    const allResponses = [];
    // Lista para almacenar los vuelos actualizados
    let updatedVuelos: Vuelo[] = []; 

    try{
      const response = await fetch(`${process.env.BACKEND_URL}limpiar`, {
        method: 'GET', // Explicitly specifying the method
        headers: {
            // If needed, specify headers here, e.g., for authentication
        },
      });
    }
    catch (error) {
      console.error('Error:', error);
    }

    for (let i = 0; i < numberOfCalls; i++) {
        if (i === 0) {
            setLoading(true); // Activar estado de cargando en la primera iteración
        }
        // Formatear la fecha y hora actualizadas
        let formattedTime = `${startHours.toString().padStart(2, '0')}:${startMinutes.toString().padStart(2, '0')}`;
        let formattedDate = formatDateTime(selectedDate, formattedTime);
        // Definir los datos JSON para la solicitud
        const data = {
            fechahora: formattedDate
        };
        console.log('Request:', data);

        try {
            const response = await fetch(`${process.env.BACKEND_URL}pso`, {
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
            allResponses.push(responseData); // Guardar la respuesta en la lista
            console.log('allResponses:', allResponses);

            // Procesar los vuelos desde el responseData
            //console.log("Response data:", responseData);
            // Create a new Set to store the idVuelo of each Vuelo in vuelos.current
            const vuelosIds = new Set(vuelos.current?.map((vuelo: Vuelo) => vuelo.idVuelo));

            responseData.forEach((data: Vuelo) => {
              // Check if the idVuelo of data is already in vuelosIds
              if (!vuelosIds.has(data.idVuelo)) {
                // If it's not in vuelosIds, add it to vuelos.current and vuelosIds
                vuelos.current?.push(data);
                vuelosIds.add(data.idVuelo);
              }
              else{
                // If it's in vuelosIds, update the Vuelo in vuelos.current
                const index = vuelos.current?.findIndex((vuelo: Vuelo) => vuelo.idVuelo === data.idVuelo);
                if (index && index !== -1) {
                  vuelos.current?.splice(index, 1, data);
                }
              }
            });

            console.log("Vuelos:", vuelos.current);

            //const vuelosRef = { current: updatedVuelos };
            // for (const key in responseData) {
            //     if (responseData.hasOwnProperty(key)) {
            //         const paquete = responseData[key];
            //         if (paquete && paquete.vuelos) {
            //             vuelosWithCapacity(paquete, vuelos);
            //         }
            //     }
            // }

            //updatedVuelos = vuelosRef.current || []; // Actualizar la lista de vuelos
            //console.log("Vuelos:", vuelos.current);
        } catch (error) {
            console.error('Error:', error);
        } finally {
            if (i === 0) {
                setLoading(false); // Desactivar estado de cargando después de la primera iteración
                onApply(); // Realizar cualquier acción adicional después de la primera solicitud
            }
        }

        // Incrementar la hora para la siguiente solicitud
        startHours += intervalHours;

        while (startHours >= 24) {
          startHours -= 24;
          selectedDate.setDate(selectedDate.getDate() + 1);
        }

        if (i > 0) {
            setLoading(true); // Mantener el estado de cargando en las iteraciones siguientes
        }
    }

    setLoading(false); // Desactivar estado de cargando al finalizar todas las iteraciones
    onApply(); // Realizar cualquier acción adicional después de la última solicitud
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

          <button className="apply-button" onClick={handleApplyClick}>
            APLICAR
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfigurationModal;