"use client";
import React, {
  createContext,
  useRef,
  useState,
  useCallback,
  useContext,
} from "react";
import { Vuelo } from "../types/Planes";
import { Envio } from "../types/envios";

export const OperationContext = createContext({
  flights: [] as Vuelo[],
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
        try {
          const response = await fetch(
            "http://localhost:8080/back/api/diario",
            {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
              },
              body: JSON.stringify(shipments),
            }
          );

          if (response.ok) {
            const responseData = await response.json();
            console.log("Response:", responseData);
            shipments.current = [];
          } else {
            const errorResponse = await response.text();
            throw new Error(errorResponse);
          }
        } catch (error) {
          console.error("Failed to send shipments:", error);
        }
      }, 2 * 60 * 1000);
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
  };

  const saveShipmentBatch = (data: Envio[]) => {
    shipments.current = shipments.current.concat(data);
  };

  return (
    <OperationContext.Provider
      value={{
        flights: flights.current,
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
