"use client";
import React, {
  createContext,
  useRef,
  useState,
  useCallback,
} from "react";
import { Vuelo } from "../types/Planes";
import { Envio } from "../types/envios";

export const OperationContext = createContext({
  flights: null as any,
  updateFlights: () => {},
  startInterval: () => {},
  clearInterval: () => {},
  saveShipmentData: (data: Envio) => {},
  saveShipmentBatch: (data: Envio[]) => {}, // Nueva función para guardar lotes de envíos
  shipments: [] as Envio[],
});

export default function OperationProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const flights = useRef<Vuelo[]>([]);
  const [, setFlightsUpdated] = useState(false);
  const intervalId = useRef<NodeJS.Timeout | null>(null);
  const shipments = useRef<Envio[]>([]); // Using useRef for shipments

  const updateFlights = useCallback(() => {
    setFlightsUpdated((prev) => !prev);
  }, []);

  const startInterval = () => {
    if (intervalId.current === null) {
      intervalId.current = setInterval(async () => {
        console.log("Sending shipments at ", new Date());
        const peticion = { envios: shipments.current };
        console.log("Peticion:", peticion);
        try {
          const response = await fetch(
            `${process.env.BACKEND_URL}diario`,
            {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
              },
              body: JSON.stringify(peticion),
            }
          );

          if (response.ok) {
            const responseData = await response.json();
            console.log("Response:", responseData);
            shipments.current = [];
          

            // Procesar los vuelos desde el responseData
            //console.log("Response data:", responseData);
            // Create a new Set to store the idVuelo of each Vuelo in vuelos.current
            const vuelosIds = new Set(flights.current?.map((vuelo: Vuelo) => vuelo.idVuelo));

            responseData.forEach((data: Vuelo) => {
              // Check if the idVuelo of data is already in vuelosIds
              if (!vuelosIds.has(data.idVuelo)) {
                // If it's not in vuelosIds, add it to vuelos.current and vuelosIds
                flights.current?.push(data);
                vuelosIds.add(data.idVuelo);
              }
              else{
                // If it's in vuelosIds, update the Vuelo in vuelos.current
                const index = flights.current?.findIndex((vuelo: Vuelo) => vuelo.idVuelo === data.idVuelo);
                if (index && index !== -1) {
                  flights.current?.splice(index, 1, data);
                }
              }
            });
            updateFlights();
            //console.log("Flights updated at ", new Date());
          } else {
            const errorResponse = await response.text();
            throw new Error(errorResponse);
          }
        } catch (error) {
          console.error("Failed to send shipments:", error);
        }
      }, 5 * 60 * 1000);
    }

    return intervalId;
  };

  const clearInterval = (current?: NodeJS.Timeout) => {
    if (intervalId.current) {
      global.clearInterval(intervalId.current);
      console.log("Interval cleared at ", new Date());
      intervalId.current = null;
    }
  };

  const saveShipmentData = (data: Envio) => {
    shipments.current.push(data);
    console.log("Shipment :", shipments.current);
  };

  const saveShipmentBatch = (data: Envio[]) => {
    shipments.current = shipments.current.concat(data);
    updateFlights();
  };

  return (
    <OperationContext.Provider
      value={{
        flights: flights,
        updateFlights,
        startInterval,
        clearInterval,
        saveShipmentData,
        saveShipmentBatch,
        shipments: shipments.current,
      }}
    >
      {children}
    </OperationContext.Provider>
  );
}
