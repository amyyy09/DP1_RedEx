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
  const [shipments, setShipments] = useState<Envio[]>([]);

  const updateFlights = useCallback(() => {
    setFlightsUpdated((prev) => !prev);
  }, []);

  const startInterval = () => {
    if (intervalId.current === null) {
      intervalId.current = setInterval(() => {
        console.log("Sending shipments at ", new Date());
        fetch("http://localhost:8080/back/api/diario", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(shipments),
        })
          .then((response) => response.json())
          .then((responseData) => {
            console.log("Response:", responseData);
            setShipments([]);
          })
          .catch((error) => {
            console.error("Failed to send shipme nts:", error);
          });
      }, 2 * 60 * 1000); // 20 minutes
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
    setShipments([...shipments, data]);
    console.log(shipments);
  };

  const saveShipmentBatch = (data: Envio[]) => {
    setShipments((prevShipments) => [...prevShipments, ...data]);
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
        shipments,
      }}
    >
      {children}
    </OperationContext.Provider>
  );
}
