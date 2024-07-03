"use client";

import React, { useMemo, useContext, useEffect, useState } from "react";
import dynamic from "next/dynamic";
import Topbar from "./components/layout/Topbar";
import Sidebar from "./components/layout/Sidebar";
import CurrentTimeDisplay from "./components/map/CurrentTimeDisplay";
import Notification from "./components/notificacion/Notification";
import { Vuelo } from "./types/Planes";
import "./styles/SimulatedTime.css";
import { OperationContext } from "./context/operation-provider";
import { citiesByCode } from "@/app/data/cities";
import { FlightPlan } from "@/app/types/FlightPlan ";
import "./styles/popupPlanDeVuelo.css";

const DayToDay: React.FC = () => {
  // const vuelos = useContext(OperationContext); // Obtiene los vuelos del contexto

  const [startSimulation, setStartSimulation] = useState(false); // Inicia la simulación
  const { flights, updateFlights, startInterval } = useContext(OperationContext);
  const speedFactor = 1; // Factor de velocidad de la simulación
  const dayToDay = true; // Indica que se trata de una simulación de día a día
  const [mapCenter, setMapCenter] = useState<[number, number] | null>(null);
  const [highlightedPlaneId, setHighlightedPlaneId] = useState<string | null>(null);
  const [selectedPackageId, setSelectedPackageId] = useState<string | null>(null);
  const [forceOpenPopup, setForceOpenPopup] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [flightPlan, setFlightPlan] = useState<FlightPlan[]>([]);
  const [showFlightPlanPopup, setShowFlightPlanPopup] = useState(false);
 
  //quiero tener los vuelos hardcodeados de arriba
  //flights.current = hardcodedVuelos;
  const handleSearch = async (id: string) => {
    // Buscar el paquete por ID
    console.log("vuelos búsqueda",flights.current);

    const foundVuelo = flights.current.find((vuelo: { paquetes: any[]; }) =>
      vuelo.paquetes.some((paquete) => paquete.id === id)
    );
    if (foundVuelo) {
      const { aeropuertoOrigen } = foundVuelo;
      const city = citiesByCode[aeropuertoOrigen];
      if (city) {
        setMapCenter([city.coords.lat, city.coords.lng]);
        setHighlightedPlaneId(foundVuelo.idVuelo);
        setSelectedPackageId(id);
        setForceOpenPopup(true);
        setErrorMessage("");

        // Fetch the flight plan
        try {
          const responseplanvuelo = await fetch(`http://localhost:8080/api/paquete/${id}`,
            {
              method: "GET",
              headers: {
                "Content-Type": "application/json",
              },
            }
          );
          const dataplanvuelo = await responseplanvuelo.json();
          console.log("data plan de vuelo ",dataplanvuelo);
          setFlightPlan(dataplanvuelo);
          setShowFlightPlanPopup(true);
        } catch (error) {
          console.error("Error fetching flight plan:", error);
          setErrorMessage("Error fetching flight plan");
        }
      }
    } else {
      setErrorMessage("ID de paquete no encontrado");
    }
  };

  useEffect(() => {
    // setVuelos(hardcodedVuelos); // Establece los vuelos hardcodeados al montar el componente
    //flights.current = hardcodedVuelos;
    console.log("flights inicio",flights);
    startInterval(); // Inicia el intervalo de actualización
    setStartSimulation(true); // Inicia la simulación al montar el componente
  }, []);

  const Map = useMemo(
    () =>
      dynamic(() => import("./components/map/Map"), {
        loading: () => <p>A map is loading...</p>,
        ssr: false,
      }),
    []
  );

  return (
    <div style={{ display: "flex", flexDirection: "column" }}>
      <Topbar onSearch={handleSearch} errorMessage={errorMessage} />
      <div style={{ display: "flex", flex: 1 }}>
        <Sidebar />
        <Map
          planes={flights} // Pasa los vuelos hardcodeados directamente
          airports={{ current: [] }}
          startTime={{ current: Date.now() }} // Asigna un tiempo de inicio ficticio
          startDate={""} // Asigna la fecha actual
          startHour={""} // Asigna la hora actual
          speedFactor={speedFactor} // Supone 1 como un marcador de posición, ajustar según sea necesario
          startSimulation={startSimulation} // Siempre inicia la simulación
          dayToDay={dayToDay} // Indica que se trata de una simulación de día a día
          mapCenter={mapCenter} // Pasa el centro del mapa actualizado
          highlightedPlaneId={highlightedPlaneId} // Pasa el ID del avión resaltado
          selectedPackageId={selectedPackageId} // Pasa el ID del paquete seleccionado
          forceOpenPopup={forceOpenPopup}
          setForceOpenPopup={setForceOpenPopup}
        />
        <CurrentTimeDisplay />{" "}
        {/* Añade el componente de visualización de la hora */}
        {/* <button onClick={addFlights}>Add Flights</button> */}
      </div>
      {errorMessage && (
        <Notification message={errorMessage} onClose={() => setErrorMessage("")} />
      )}
      {showFlightPlanPopup && (
        <div className="flight-plan-popup">
          <div className="flight-plan-popup-header">
            <h2>Plan de Vuelo</h2>
            <button onClick={() => setShowFlightPlanPopup(false)} className="close-button">&times;</button>
          </div>
          <div className="flight-plan-popup-content">
            {flightPlan.length > 0 ? (
              <ul>
                {flightPlan.map((plan, index) => (
                  <li key={index} className="flight-plan-item">
                    <p><strong>Plan ID:</strong> {plan.indexPlan}</p>
                    <p>Origen: {plan.aeropuertoSalida} con hora de Salida: {plan.fechaSalida.join("-")}</p>
                    <p>El destino es: {plan.aeropuertoDestino} con hora de llega: {plan.fechaLLegada.join("-")}</p>
                  </li>
                ))}
              </ul>
            ) : (
              <p>No hay datos de plan de vuelo disponibles.</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default DayToDay;
