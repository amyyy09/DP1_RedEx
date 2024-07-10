"use client";
import React, { createContext, useRef, useState, useCallback } from "react";
import { Airport, Vuelo } from "../types/Planes";
import { Envio } from "../types/envios";
import { cities } from "../data/cities";

export const OperationContext = createContext({
  flights: null as any,
  updateFlights: () => {},
  startInterval: () => {},
  clearInterval: () => {},
  saveShipmentData: (data: Envio) => {},
  saveShipmentBatch: (data: Envio[]) => {}, // Nueva función para guardar lotes de envíos
  shipments: [] as Envio[],
  flightsOnAir: null as any,
  packages: null as any,
  airports: null as any,
  startTime: null as any,
});

export default function OperationProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const flights = useRef<Vuelo[]>([]);
  const airports = useRef<Airport[]>([]);
  const [, setFlightsUpdated] = useState(false);
  const intervalId = useRef<NodeJS.Timeout | null>(null);
  const shipments = useRef<Envio[]>([]); // Using useRef for shipments
  const flightsOnAir = useRef<number>(0);
  const packages = useRef<any[]>([]);
  const startTime = useRef<number | null>(null);

  const createAirports = () => {
    cities.forEach((city) => {
      const airport = {
        codigoIATA: city.code,
        ciudad: city.name,
        pais: "Pais",
        continente: "Continente",
        alias: "Alias",
        zonaHorariaGMT: city.GMT,
        almacen: {
          capacidad: city.capacidad,
          cantPaquetes: 0,
          paquetes: [],
        },
        latitud: city.coords.lat,
        longitud: city.coords.lng,
      };
      airports.current.push(new Airport(airport));
    });
  };

  const updateFlights = useCallback(() => {
    setFlightsUpdated((prev) => !prev);
  }, []);

  const startInterval = () => {
    if (intervalId.current === null) {
      createAirports();
      startTime.current = Date.now();
      console.log ("Start time:", startTime.current);
      intervalId.current = setInterval(async () => {
        console.log("Sending shipments at ", new Date());
        const peticion = { envios: shipments.current };
        console.log("Peticion:", peticion);
        // try {
        //   const response = await fetch(
        //     `${process.env.BACKEND_URL}diario`,
        //     {
        //       method: "POST",
        //       headers: {
        //         "Content-Type": "application/json",
        //       },
        //       body: JSON.stringify(peticion),
        //     }
        //   );

        //   if (response.ok) {
        //     const responseData = await response.json();
        //     console.log("Response:", responseData);
        //     shipments.current = [];

        //     // Procesar los vuelos desde el responseData
        //     //console.log("Response data:", responseData);
        //     // Create a new Set to store the idVuelo of each Vuelo in vuelos.current
        //     const vuelosIds = new Set(flights.current?.map((vuelo: Vuelo) => vuelo.idVuelo));

        //     responseData.forEach((data: Vuelo) => {
        //       // Check if the idVuelo of data is already in vuelosIds
        //       if (!vuelosIds.has(data.idVuelo)) {
        //         // If it's not in vuelosIds, add it to vuelos.current and vuelosIds
        //         flights.current?.push(data);
        //         vuelosIds.add(data.idVuelo);
        //       }
        //       else{
        //         // If it's in vuelosIds, update the Vuelo in vuelos.current
        //         const index = flights.current?.findIndex((vuelo: Vuelo) => vuelo.idVuelo === data.idVuelo);
        //         if (index && index !== -1) {
        //           flights.current?.splice(index, 1, data);
        //         }
        //       }
        //     });
        //     updateFlights();
        //     //console.log("Flights updated at ", new Date());
        //   } else {
        //     const errorResponse = await response.text();
        //     throw new Error(errorResponse);
        //   }
        // } catch (error) {
        //   console.error("Failed to send shipments:", error);
        // }
      }, 5 * 60 * 1000);
      //ahora será cada minuto
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
    //Llamada a la API
    shipments.current.push(data);
    console.log("Shipment :", shipments.current);
  };

  const saveShipmentBatch = (data: Envio[]) => {
    //Llamada a la API
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
        flightsOnAir: flightsOnAir,
        packages: packages,
        airports: airports,
        startTime: startTime,
      }}
    >
      {children}
    </OperationContext.Provider>
  );
}
