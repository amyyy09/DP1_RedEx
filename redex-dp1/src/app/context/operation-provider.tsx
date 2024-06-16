'use client';

import { createContext, useRef, useState, useCallback } from "react";
import { Vuelo } from "../types/Planes";

export const OperationContext = createContext({
    flights: null as any,
    updateFlights: () => {}, // Add a function to update flights
    startInterval: () => {},
    clearInterval: (intervalId: number) => {},
});

export default function OperationProvider({ children }: { children: React.ReactNode; }) {
    const flights = useRef<Vuelo[]>([]);
    const [, setFlightsUpdated] = useState(false);

    const updateFlights = useCallback(() => {
        setFlightsUpdated(prev => !prev); // Toggle the state to force re-render
    }, []);

    const startInterval = () => {
        const intervalId = setInterval(() => {
            // Call your API here
        }, 20 * 60 * 1000); // 20 minutes

        return intervalId;
    };

    const clearInterval = (intervalId: number) => {
        clearInterval(intervalId);
    };

    return (
        <OperationContext.Provider value={{ flights, updateFlights, startInterval, clearInterval }}>
            {children}
        </OperationContext.Provider>
    );
}
