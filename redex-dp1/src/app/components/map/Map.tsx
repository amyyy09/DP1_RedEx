// components/PlaneMap.tsx
"use client";


import React, { useState, useMemo, useCallback, useEffect,useRef } from "react";
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  ZoomControl,
} from "react-leaflet";
import L, { LatLngTuple } from "leaflet";
import "leaflet/dist/leaflet.css";
import Plane from "./Plane";
import { Airport, Vuelo } from "@/app/types/Planes";
import PackageDetails from "./PackageDetails";
import { cities } from "@/app/data/cities";
import MapCenter from "./MapCenter";

interface MapProps {
  planes: React.RefObject<Vuelo[]>;
  airports: React.MutableRefObject<Airport[]>;
  startTime: React.RefObject<number>;
  startDate: string;
  startHour: string;
  speedFactor: number;
  startSimulation: boolean;
  dayToDay: boolean;
  mapCenter: LatLngTuple | null;
  highlightedPlaneId: string | null;
  selectedPackageId: string | null;
  forceOpenPopup: boolean;
  setForceOpenPopup: (value: boolean) => void;
  airportsHistory?: React.MutableRefObject<Airport[][]>;
}

const customIcon = new L.Icon({
  iconUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png",
  iconRetinaUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png",
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
  shadowSize: [41, 41],
});

const Map: React.FC<MapProps> = ({
  planes,
  airports,
  startTime,
  startDate,
  startHour,
  speedFactor,
  startSimulation,
  dayToDay,
  mapCenter,
  highlightedPlaneId,
  selectedPackageId,
  forceOpenPopup,
  setForceOpenPopup,
  airportsHistory,
}) => {
  const simulatedDate = useRef<Date>();
  const prevUpdate = useRef<number>(0);
  // console.log("planes",planes.current);
  useEffect(() => {
    // console.log("Plane vuelo", vuelo);
    // console.log("startSimulation", startSimulation);
    // console.log("startTime", startTime);
    // console.log("startDate", startDate);
    // console.log("startHour", startHour);
    // console.log("speedFactor", speedFactor);
    if (!startSimulation || dayToDay) return;

    // console.log("plane started");

    // Update the simulated time
    const updateSimulatedTime = () => {
      if (!startSimulation || !startTime.current) return;

      // console.log("startTime", startTime.current);

      const currentTime = Date.now();
      // console.log("currentTime", currentTime);
      const elapsedTime = (currentTime - startTime.current) / 1000; // in seconds
      // console.log("elapsedTime", elapsedTime);
      const simulatedTime = elapsedTime * speedFactor;
      // console.log("simulatedTime", simulatedTime);
      // Create a new Date object for the start of the simulation
      const startDateSim = new Date(startDate + "T" + startHour + ":00");
      // console.log("startDateSim", startDateSim);

      // Add the simulated time to the start date
      simulatedDate.current = new Date(
        startDateSim.getTime() + simulatedTime * 1000
      );

      const hoursElapsed =
        (simulatedDate.current.getTime() - startDateSim.getTime()) /
        (1000 * 60 * 60); // Convert milliseconds to hours
      const hoursSinceLastUpdate = hoursElapsed - prevUpdate.current;
      if (hoursSinceLastUpdate >= 2) {
        console.log("Updating airports");
        // Perform the desired action here
        // Procesar los aeropuertos desde el responseAeropuertos
        const responseAeropuertos = airportsHistory?.current[0];
        if (!responseAeropuertos) return;
        responseAeropuertos.forEach((data: Airport) => {
          const index = airports.current.findIndex(
            (aeropuerto: Airport) => aeropuerto.codigoIATA === data.codigoIATA
          );
          if (index !== -1) {
            // console.log('Aeropuerto encontrado:', data.codigoIATA);
            // console.log('cantidad de paquetes:', data.almacen.cantPaquetes);
            // console.log('cantidad de paquetes almacen:', airports.current[index].almacen.cantPaquetes);
            airports.current[index].almacen.cantPaquetes +=
              data.almacen.cantPaquetes;
            // console.log('cantidad de paquetes suma:', airports.current);

            data.almacen.paquetes.forEach((paquete: any) => {
              airports.current[index].almacen.paquetes.push(paquete);
            });
          } else {
            console.log("Aeropuerto no encontrado:", data.codigoIATA);
          }
        });
        // drop the first element of the history
        airportsHistory.current.shift();

        // Update prevUpdate to the current hoursElapsed rounded down to the nearest even number
        prevUpdate.current = Math.floor(hoursElapsed / 2) * 2;
        console.log("airports", airports.current);
        console.log("history", airportsHistory.current);
      }

      if (prevUpdate.current === 168) {
        clearInterval(intervalId);
      }
    };

    // Call updateSimulatedTime every second
    const intervalId = setInterval(updateSimulatedTime, 100 / speedFactor);

    return () => {
      clearInterval(intervalId);
    };
  }, [startSimulation]);

  const [showPackages, setShowPackages] = useState(false);
  const [selectedVuelo, setSelectedVuelo] = useState<Vuelo | null>(null);
  const [selectedAirport, setSelectedAirport] = useState<Airport | null>(null);

  const handleShowPackages = useCallback((vuelo: Vuelo) => {
    setSelectedVuelo(vuelo);
    setShowPackages(true);
  }, []);


  const handlePopupClose = useCallback(() => {
    setShowPackages(false);
    setSelectedVuelo(null);
  }, []);

  const handleShowAirportPackages = useCallback((airport: Airport) => {
    console.log("Selected Airport Data:", airport); // Log the airport data
    setSelectedAirport(airport);
  }, []);

  const handleCloseAirportPackages = useCallback(() => {
    setSelectedAirport(null);
  }, []);

  useEffect(() => {
    if (selectedPackageId) {
      // Cierra el paquete actual primero
      setShowPackages(false);
      setSelectedVuelo(null);

      // Espera un momento antes de abrir el nuevo paquete
      setTimeout(() => {
        const foundVuelo = planes.current?.find((vuelo) =>
          vuelo.paquetes.some((paquete) => paquete.id === selectedPackageId)
        );
        if (foundVuelo) {
          setSelectedVuelo(foundVuelo);
          setShowPackages(true);
        }
      }, 300); // Ajusta el retraso si es necesario
    }
  }, [selectedPackageId, planes]);

  useEffect(() => {
    if (selectedAirport) {
      const updatedAirport = airports.current.find(
        (airport) => airport.codigoIATA === selectedAirport.codigoIATA
      );
      if (updatedAirport) {
        setSelectedAirport(updatedAirport);
      }
    }
  }, [airports]);

  return (
    <>
      <MapContainer
        center={[20, 20]}
        zoom={3}
        style={{ height: "calc(100vh - 50px)", width: "calc(100vw - 50px)" }}
        zoomControl={false}
      >
        <TileLayer
          url="https://tile.jawg.io/jawg-light/{z}/{x}/{y}.png?lang=es&access-token=bs1zsL2E6RmY3M31PldL4RlDqNN0AWy3PJAMBU0DRv2G1PGLdj0tDtxlZ1ju4WT4"
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />
        <ZoomControl position="topright" />

        {mapCenter && <MapCenter center={mapCenter} />}
        {cities.map((city, idx) => {
          const cityData = airports.current ? airports.current[idx] : null;
          const iconColor =
            cityData && cityData.almacen.cantPaquetes > 0
              ? city.capacidad / cityData.almacen.cantPaquetes > 2
                ? "green"
                : city.capacidad / cityData.almacen.cantPaquetes > 4 / 3
                ? "yellow"
                : "red"
              : "green";

          const dynamicIcon = new L.Icon({
            iconUrl: `https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-${iconColor}.png`,
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowUrl:
              "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
            shadowSize: [41, 41],
          });

          return (
            <Marker
              key={idx}
              position={[city.coords.lat, city.coords.lng] as LatLngTuple}
              icon={dynamicIcon}
              eventHandlers={{
                click: () => {
                  if (cityData) {
                    handleShowAirportPackages(cityData);
                  }
                },
              }}
            >
              <Popup>
                <h2 style={{ fontSize: "1.5em", fontWeight: "bold" }}>
                  {city.name}
                </h2>
                <br />
                <strong>Capacidad de Almacenamiento: </strong>
                {city.capacidad}
                {cityData && (
                  <>
                    <br />
                    <strong>Cantidad de paquetes: </strong>
                    {cityData.almacen.cantPaquetes}
                  </>
                )}
              </Popup>
            </Marker>
          );
        })}
        {planes.current &&
          planes.current.length > 0 &&
          planes.current.map(
            (plane, index) =>
              plane.status !== 2 && (
                <Plane
                  key={plane.idVuelo}
                  listVuelos={planes.current as Vuelo[]}
                  airports={airports.current as Airport[]}
                  index={index}
                  vuelo={plane}
                  startTime={startTime}
                  startDate={startDate}
                  startHour={startHour}
                  speedFactor={speedFactor}
                  startSimulation={startSimulation}
                  dayToDay={dayToDay}
                  isOpen={highlightedPlaneId === plane.idVuelo && forceOpenPopup}
                  setForceOpenPopup={setForceOpenPopup}
                  selectedPackageId={selectedPackageId}
                  handleShowPackages={handleShowPackages}
                  showPackages={showPackages}
                  setShowPackages={setShowPackages}
                />
              )
          )}
      </MapContainer>
      {showPackages && selectedVuelo && (
        <PackageDetails
          paquetes={selectedVuelo.paquetes || []}
          selectedPackageId={selectedPackageId}
          onClose={handlePopupClose}
        />
      )}
      {selectedAirport && (
        <div className="package-details-left">
          <PackageDetails
            paquetes={selectedAirport.almacen.paquetes}
            selectedPackageId={selectedPackageId}
            onClose={handleCloseAirportPackages}
          />
        </div>
      )}
    </>
  );
};

export default Map;
