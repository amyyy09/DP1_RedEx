// components/PlaneMap.tsx
"use client";
import React, { useState, useEffect, useRef, useCallback } from "react";
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
import AirportDetails from "./AirportDetails";
import { cities } from "@/app/data/cities";
import MapCenter from "./MapCenter";
import "@/app/styles/MoreInfoComponent.css";

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
  showMoreInfo: boolean;
  setShowMoreInfo: (value: boolean) => void;
  vuelosInAir: React.MutableRefObject<number>;
  selectedPlaneId: string | null;
  setSelectedPlaneId: (value: string | null) => void;
  paquetes: React.MutableRefObject<any[]>;
}

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
  showMoreInfo,
  setShowMoreInfo,
  vuelosInAir,
  selectedPlaneId,
  setSelectedPlaneId,
  paquetes,
}) => {
  const simulatedDate = useRef<Date>();
  const prevUpdate = useRef<number>(0);
  const markerRefs = useRef<Record<string, L.Marker<any>>>({});
  const [shouldOpenPopup, setShouldOpenPopup] = useState(false);
  const [highlightedAirportCode, setHighlightedAirportCode] = useState<
    string | null
  >(null);

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
        // console.log("Updating airports");
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

        // if (prevUpdate.current % 24 === 0) {
        //   airports.current.forEach((aeropuerto: Airport) => {
        //     aeropuerto.almacen.paquetes
        //       .slice(0, aeropuerto.almacen.paquetes.length / 4)
        //       .forEach((paquete: any) => {
        //         if (paquete.status === 0) {
        //           // delete the package
        //           const index = aeropuerto.almacen.paquetes.findIndex(
        //             (p: any) => p.id === paquete.id
        //           );
        //           aeropuerto.almacen.paquetes.splice(index, 1);
        //           aeropuerto.almacen.cantPaquetes -= 1;
        //         }
        //       });
        //   });
        // }
        // console.log("airports", airports.current);
        // console.log("history", airportsHistory.current);
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
  const [selectedCity, setSelectedCity] = useState<(typeof cities)[0] | null>(
    null
  );

  const handleShowPackages = useCallback((vuelo: Vuelo) => {
    setSelectedVuelo(vuelo);
    setShowPackages(true);
  }, []);

  const handlePopupClose = useCallback(() => {
    setSelectedVuelo(null);
    setSelectedPlaneId(null);
  }, [setSelectedPlaneId]);

  const handleShowAirportPackages = useCallback(
    (airport: Airport, city: (typeof cities)[0]) => {
      console.log("Selected Airport Data:", airport); // Log the airport data
      setSelectedAirport(airport);
      setSelectedCity(city);
      setShowPackages(true);
      setHighlightedAirportCode(airport.codigoIATA); // Cambiar el color del aeropuerto seleccionado a negro
    },
    []
  );

  const handleCloseAirportPackages = useCallback(() => {
    setSelectedAirport(null);
    setSelectedCity(null);
    setHighlightedAirportCode(null); // Resetear el color del aeropuerto
  }, []);

  const handleClosePackageDetails = useCallback(() => {
    setShowPackages(false);
  }, []);

  useEffect(() => {
    if (selectedPackageId) {
      // Cierra el paquete actual primero
      setShowPackages(false);
      setSelectedVuelo(null);
      setShouldOpenPopup(true); // Forzar apertura del popup

      // Espera un momento antes de abrir el nuevo paquete
      setTimeout(() => {
        const foundVuelo = planes.current?.find((vuelo) =>
          vuelo.paquetes.some((paquete) => paquete.id === selectedPackageId)
        );
        if (foundVuelo) {
          setSelectedVuelo(foundVuelo);
          setSelectedPlaneId(foundVuelo.idVuelo);
          setShowPackages(true);
        } else {
          const foundAirport = airports.current.find((airport) =>
            airport.almacen.paquetes.some(
              (paquete) => paquete.id === selectedPackageId
            )
          );
          if (foundAirport) {
            const city = cities.find(
              (city) => city.code === foundAirport.codigoIATA
            );
            if (city) {
              setSelectedAirport(foundAirport);
              setSelectedCity(city);
              setShowPackages(true);
              const marker = markerRefs.current[foundAirport.codigoIATA];
              if (marker) {
                marker.openPopup();
                setShouldOpenPopup(false); // Resetear el estado
              }
              setHighlightedAirportCode(foundAirport.codigoIATA); // Cambiar el color del aeropuerto encontrado a negro
            }
          }
        }
      }, 300); // Adjust delay if necessary
    }
  }, [selectedPackageId, planes, airports]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as HTMLElement;
      if (
        !target.closest(".airport-details-fixed") &&
        !target.closest(".package-details-fixed")
      ) {
        handleCloseAirportPackages();
      }
    };

    if (showPackages && selectedAirport) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showPackages, selectedAirport, handleCloseAirportPackages]);

  return (
    <>
      <MapContainer
        center={[17.5, -7.5]}
        zoom={3}
        style={{ height: "calc(100vh - 50px)", width: "calc(100vw - 50px)" }}
        scrollWheelZoom={false}
        // dragging={false}
        // touchZoom={false}
        doubleClickZoom={false}
        zoomControl={false}
      >
        <TileLayer
          url="https://tile.jawg.io/jawg-light/{z}/{x}/{y}.png?lang=es&access-token=bs1zsL2E6RmY3M31PldL4RlDqNN0AWy3PJAMBU0DRv2G1PGLdj0tDtxlZ1ju4WT4"
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />
        <ZoomControl position="topright" />

        {/* {mapCenter && <MapCenter center={mapCenter} />} */}
        {cities.map((city, idx) => {
          const cityData = airports.current ? airports.current[idx] : null;
          const iconColor =
            highlightedAirportCode === city.code
              ? "black"
              : cityData && cityData.almacen.cantPaquetes > 0
              ? (city.capacidad + 1000) / cityData.almacen.cantPaquetes > 1 / 3
                ? "green"
                : (city.capacidad + 1000) / cityData.almacen.cantPaquetes >
                  2 / 3
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
                    handleShowAirportPackages(cityData, city);
                  }
                },
              }}
            />
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
                  isOpen={
                    highlightedPlaneId === plane.idVuelo && forceOpenPopup
                  }
                  setForceOpenPopup={setForceOpenPopup}
                  selectedPackageId={selectedPackageId}
                  handleShowPackages={handleShowPackages}
                  showPackages={showPackages}
                  setShowPackages={setShowPackages}
                  vuelosInAir={vuelosInAir}
                  selectedPlaneId={selectedPlaneId}
                  setSelectedPlaneId={setSelectedPlaneId}
                  paquetes={paquetes}
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
      {selectedAirport && selectedCity && (
        <AirportDetails
          city={selectedCity}
          cityData={selectedAirport}
          onClose={handleCloseAirportPackages}
          onShowPackages={() => setShowPackages((prev) => !prev)}
          showPackages={showPackages}
        />
      )}
      {showPackages && selectedAirport && (
        <PackageDetails
          paquetes={selectedAirport.almacen.paquetes.toReversed() || []}
          selectedPackageId={selectedPackageId}
          onClose={handleClosePackageDetails}
        />
      )}
      {(dayToDay || startSimulation) && !showMoreInfo && (
        <button
          className="more-info-button"
          onClick={() => setShowMoreInfo(true)}
        >
          Más información
        </button>
      )}
    </>
  );
};

export default Map;
