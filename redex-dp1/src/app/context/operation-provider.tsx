"use client";
import React, {
  createContext,
  useRef,
  useState,
  useCallback,
  useEffect,
} from "react";
import { Airport, Vuelo } from "../types/Planes";
import { Envio } from "../types/envios";
import { cities, citiesByCode } from "../data/cities";

export const OperationContext = createContext({
  flights: null as any,
  updateFlights: () => {},
  clearInterval: () => {},
  saveShipmentData: async (data: Envio) => {},
  saveShipmentBatch: async (data: Envio[]) => {}, // Nueva función para guardar lotes de envíos
  shipments: [] as Envio[],
  flightsOnAir: null as any,
  packages: null as any,
  airports: null as any,
  startTime: null as any,
  start: false,
  setStart: (start: boolean) => {},
  referenceTime: null as any,
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
  const [start, setStart] = useState(false);
  const [referenceTime, setReferenceTime] = useState<Date | null>(null);

  useEffect(() => {
    if (start) {
      startInterval();
    }
  }, [start]);

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

  const startInterval = async () => {
    if (intervalId.current === null) {
      createAirports();
      startTime.current = Date.now();
      const ref = await getDateTime();
      setReferenceTime(ref);
      console.log("Reference time called again:", ref);
      // console.log("Reference time:", referenceTime.current);
      // console.log("Start time:", startTime.current);
      intervalId.current = setInterval(async () => {
        updateAirports();
        // await psoDiario(); // Existing async operation
      }, 60 * 1000);
      //ahora será cada minuto
    }

    return intervalId;
  };

  const getDateTime = async () => {
    const response = await fetch(`${process.env.BACKEND_URL}iniciar`);
    if (response.ok) {
      const responseData = await response.text();
      console.log("Response:", responseData);

      // Extract the date and time part from the response
      const dateTimePart = responseData.split("Hora simulada actual: ")[1];

      // Assuming the date and time part is in "YYYY-MM-DD HH:mm:ss" format
      // Convert the space between date and time to 'T' to make it ISO 8601 format
      const isoDateTime = dateTimePart.replace(" ", "T");

      // Create a Date object from the ISO 8601 formatted string
      const dateTime = new Date(isoDateTime);

      // console.log("Extracted Date and Time:", dateTime);
      return dateTime;
    } else {
      const errorResponse = await response.text();
      throw new Error(errorResponse);
    }
  };

  const updateAirports = () => {
    if (shipments.current.length === 0 || startTime.current === null) {
      console.log("No shipments to update");
      return;
    }
    const toRemove: any = [];
    shipments.current.forEach((envio, index) => {
      // convert the envio fechaHora to gmt -5 considering its origen timezone
      const gmtOffset = citiesByCode[envio.codigoIATAOrigen].GMT;
      const fechaHora = new Date(envio.fechaHoraOrigen);
      console.log("envio.fechaHoraOrigen:", envio.fechaHoraOrigen);
      fechaHora.setHours(fechaHora.getHours() - gmtOffset - 5);
      console.log("FechaHora:", fechaHora);

      let customDate = referenceTime ? new Date(referenceTime) : null;
      if (customDate === null) {
        return; // Don't do anything if the reference time is not set
      }
      const current = new Date();
      const start = new Date(startTime.current || 0);

      // add to customDate the difference between the current time and the start time
      customDate.setMinutes(
        customDate.getMinutes() + current.getMinutes() - start.getMinutes()
      );

      customDate.setSeconds(
        customDate.getSeconds() + current.getSeconds() - start.getSeconds()
      );

      console.log("customDate:", customDate);

      if (fechaHora.getTime() <= customDate.getTime()) {
        console.log("subir paquetes al avion");
        // find the airport with the same IATA code as the envio.origen
        const airport = airports.current.find(
          (airport) => airport.codigoIATA === envio.codigoIATAOrigen
        );
        if (airport) {
          airport.almacen.cantPaquetes += envio.cantPaquetes;
          envio.paquetes.forEach((paquete) => {
            airport.almacen.paquetes.push(paquete);
          });
          toRemove.push(index);
        }
      }
    });
    updateFlights();

    // Remove the shipments that have been processed
    toRemove.forEach((index: any) => {
      shipments.current.splice(index, 1);
    });
  };

  const psoDiario = async () => {
    try {
      const response = await fetch(`${process.env.BACKEND_URL}psoDiario`);

      if (response.ok) {
        let responseData;
        // Attempt to parse the response as JSON
        try {
          responseData = await response.json();
        } catch (error) {
          // If parsing fails, read the response as text
          const responseText = await response.text();
          // Check if the response is the specific string
          if (responseText === "Aún no termina la ejecucion") {
            console.log("Aún no termina la ejecucion");
            return; // Exit the function or handle this case as needed
          } else {
            throw new Error("Unexpected response format");
          }
        }
        console.log("Response:", responseData);

        // Procesar los vuelos desde el responseData
        //console.log("Response data:", responseData);
        // Create a new Set to store the idVuelo of each Vuelo in vuelos.current
        const vuelosIds = new Set(
          flights.current?.map((vuelo: Vuelo) => vuelo.idVuelo)
        );

        responseData.forEach((data: Vuelo) => {
          // Check if the idVuelo of data is already in vuelosIds
          if (!vuelosIds.has(data.idVuelo)) {
            // If it's not in vuelosIds, add it to vuelos.current and vuelosIds
            flights.current?.push(data);
            vuelosIds.add(data.idVuelo);
          } else {
            // If it's in vuelosIds, update the Vuelo in vuelos.current
            const index = flights.current?.findIndex(
              (vuelo: Vuelo) => vuelo.idVuelo === data.idVuelo
            );
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
      console.error("Failed to get flights:", error);
    }
  };

  const clearInterval = (current?: NodeJS.Timeout) => {
    if (intervalId.current) {
      global.clearInterval(intervalId.current);
      console.log("Interval cleared at ", new Date());
      intervalId.current = null;
    }
  };

  const saveShipmentData = async (data: Envio) => {
    //Llamada a la API
    shipments.current.push(data);
    console.log("Shipment :", shipments.current);

    await sendShipmentData([data]);
  };

  const sendShipmentData = async (data: Envio[]) => {
    const peticion = { envios: data };
    console.log("Peticion:", peticion);
    try {
      const response = await fetch(`${process.env.BACKEND_URL}registro`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(peticion),
      });

      if (response.ok) {
        const responseData = await response.text();
        console.log("Response:", responseData);
        //console.log("Flights updated at ", new Date());
      } else {
        const errorResponse = await response.text();
        throw new Error(errorResponse);
      }
    } catch (error) {
      console.error("Failed to send shipments:", error);
      // throw error;
    }
  };

  const saveShipmentBatch = async (data: Envio[]) => {
    //Llamada a la API
    shipments.current = shipments.current.concat(data);

    console.log("Shipments:", shipments.current);
    await sendShipmentData(data);
  };

  return (
    <OperationContext.Provider
      value={{
        flights: flights,
        updateFlights,
        clearInterval,
        saveShipmentData,
        saveShipmentBatch,
        shipments: shipments.current,
        flightsOnAir: flightsOnAir,
        packages: packages,
        airports: airports,
        startTime: startTime,
        start,
        setStart,
        referenceTime: referenceTime,
      }}
    >
      {children}
    </OperationContext.Provider>
  );
}
