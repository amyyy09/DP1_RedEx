"use client";

import { createContext, useRef, useState, useCallback } from "react";
import { Vuelo } from "../types/Planes";

export const OperationContext = createContext({
  flights: null as any,
  updateFlights: () => {}, // Add a function to update flights
  startInterval: () => {},
  clearInterval: () => {},
});

export default function OperationProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const flights = useRef<Vuelo[]>([]);
  const [, setFlightsUpdated] = useState(false);
  const intervalId = useRef<NodeJS.Timeout | null>(null);

  const updateFlights = useCallback(() => {
    setFlightsUpdated((prev) => !prev); // Toggle the state to force re-render
  }, []);

  const startInterval = () => {
    // Call your API here
    // console.log("API called at ", new Date());
    if (intervalId.current === null) {
      // aquí debería limpiarse también
      console.log("API called at ", new Date());
      intervalId.current = setInterval(() => {
        // Call your API here
        console.log("API called at ", new Date());
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

  return (
    <OperationContext.Provider
      value={{ flights, updateFlights, startInterval, clearInterval }}
    >
      {children}
    </OperationContext.Provider>
  );
}
