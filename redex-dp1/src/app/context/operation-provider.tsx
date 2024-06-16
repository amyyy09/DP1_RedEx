'use client';

import { createContext, useRef, useState, useCallback } from "react";
import { Vuelo } from "../types/Planes";

export const OperationContext = createContext({
    flights: null as any,
    updateFlights: () => {}, // Add a function to update flights
});

export default function OperationProvider({ children }: { children: React.ReactNode; }) {
    const flights = useRef<Vuelo[]>([]);
    const [, setFlightsUpdated] = useState(false);

    const updateFlights = useCallback(() => {
        setFlightsUpdated(prev => !prev); // Toggle the state to force re-render
    }, []);

    return (
        <OperationContext.Provider value={{ flights, updateFlights }}>
            {children}
        </OperationContext.Provider>
    );
}
