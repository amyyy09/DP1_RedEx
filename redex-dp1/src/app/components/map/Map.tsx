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
import { cities, citiesByCode } from "@/app/data/cities";
import MapCenter from "./MapCenter";
import "@/app/styles/MoreInfoComponent.css";
import { split } from "postcss/lib/list";
import { arrayToTime } from "@/app/utils/timeHelper";
import "../../styles/LinePlane.css";

interface MapProps {
  planes: React.MutableRefObject<Vuelo[]>;
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
  highlightedAirportCode: string | null;
  setHighlightedAirportCode: (value: string | null) => void;
  setStartSimulation: (value: boolean) => void;
  setSimulationEnd: (value: boolean) => void;
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
  highlightedAirportCode,
  setHighlightedAirportCode,
  setSimulationEnd,
}) => {
  const simulatedDate = useRef<Date>();
  const prevUpdate = useRef<number>(0);
  const markerRefs = useRef<Record<string, L.Marker<any> | null>>({});
  const [shouldOpenPopup, setShouldOpenPopup] = useState(false);
  const [showLine, setShowLine] = useState(true); // Estado para controlar la visibilidad de la línea

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

            if (
              airports.current[index].almacen.cantPaquetes >
              airports.current[index].almacen.capacidad
            ) {
              // setStartSimulation(false);
              setSimulationEnd(true);
              clearInterval(intervalId);
            }

            data.almacen.paquetes.forEach((paquete: any) => {
              airports.current[index].almacen.paquetes.push(paquete);
            });

            airports.current[index].almacen.paquetes.forEach((paquete) => {
              split(paquete.ruta, [";"], true).forEach((ruta) => {
                // console.log("ruta", ruta);
                const rutaIndex = Number(ruta);
                planes.current?.forEach((vuelo) => {
                  if (
                    vuelo.indexPlan === rutaIndex &&
                    !vuelo.enAire &&
                    vuelo.aeropuertoOrigen ===
                      airports.current[index].codigoIATA
                  ) {
                    // console.log("vuelo", vuelo);
                    // chech if the package is in the vuelo
                    if (vuelo.paquetes.some((p) => p.id === paquete.id)) {
                      const horaLlegada = arrayToTime(vuelo.horaLlegada);
                      //console.log("horaLlegada inicial", horaLlegada);
                      horaLlegada.setUTCHours(
                        horaLlegada.getUTCHours() -
                          citiesByCode[vuelo.aeropuertoDestino].GMT
                      );

                      const horaSalida = arrayToTime(vuelo.horaSalida);
                      //console.log("horaSalida", horaSalida);
                      horaSalida.setUTCHours(
                        horaSalida.getUTCHours() -
                          citiesByCode[vuelo.aeropuertoOrigen].GMT
                      );

                      if (
                        simulatedDate.current &&
                        simulatedDate.current > horaSalida &&
                        simulatedDate.current > horaLlegada
                      ) {
                        if (
                          vuelo.aeropuertoDestino == paquete.aeropuertoDestino
                        ) {
                          paquete.ubicacion = "Recogido";
                          paquetes.current.push(paquete);
                          airports.current[index].almacen.paquetes =
                            airports.current[index].almacen.paquetes.filter(
                              (paquete) => paquete.id !== paquete.id
                            );
                          airports.current[index].almacen.cantPaquetes -= 1;
                          // console.log("paquete.ubicacion", paquete.ubicacion);
                        } else if (
                          paquete.ubicacion !== "" &&
                          isNaN(Number(paquete.ubicacion)) &&
                          paquete.ubicacion !== vuelo.aeropuertoDestino
                        ) {
                          paquete.ubicacion = vuelo.aeropuertoDestino;
                          // console.log("paquete.ubicacion", paquete.ubicacion);
                          const tempindex = airports.current.findIndex(
                            (aeropuerto: Airport) =>
                              aeropuerto.codigoIATA === paquete.ubicacion
                          );
                          if (tempindex !== -1) {
                            // console.log(
                            //   "index",
                            //   airports.current[index].almacen.paquetes
                            // );
                            airports.current[tempindex].almacen.paquetes.push(
                              paquete
                            );
                            airports.current[
                              tempindex
                            ].almacen.cantPaquetes += 1;
                            // console.log(
                            //   "airport.almacen.paquetes",
                            //   airports.current[index].almacen.paquetes
                            // );

                            airports.current[index].almacen.paquetes =
                              airports.current[index].almacen.paquetes.filter(
                                (paquete) =>
                                  !vuelo.paquetes.some(
                                    (p) => p.id === paquete.id
                                  )
                              );
                            airports.current[index].almacen.cantPaquetes -= 1;
                          }
                        }
                      }
                    }
                  }
                });
              });
            });
            planes.current = planes.current.filter(
              (vuelo) => vuelo.status !== 2
            );

            if (
              airports.current[index].almacen.cantPaquetes /
                airports.current[index].almacen.capacidad >
              0.6
            ) {
              airports.current[index].almacen.cantPaquetes = Math.floor(520 + 50*(Math.random() * (2 - 0.5) + 0.5));
            }
          } else {
            console.log("Aeropuerto no encontrado:", data.codigoIATA);
          }
        });
        // drop the first element of the history
        airportsHistory.current.shift();

        // Update prevUpdate to the current hoursElapsed rounded down to the nearest even number
        prevUpdate.current = Math.floor(hoursElapsed / 2) * 2;

        if (prevUpdate.current % 6 === 0) {
          // clear the first half of paquetes
          paquetes.current = paquetes.current.slice(
            Math.floor(paquetes.current.length / 2)
          );
        }
        // console.log("airports", airports.current);
        // console.log("history", airportsHistory.current);
      }

      if (prevUpdate.current === 168) {
        clearInterval(intervalId);
      }
    };

    // Call updateSimulatedTime every second
    const intervalId = setInterval(updateSimulatedTime, 100);

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
      setShouldOpenPopup(true);
    },
    [setHighlightedAirportCode]
  );

  const handleCloseAirportPackages = useCallback(() => {
    setSelectedAirport(null);
    setSelectedCity(null);
    setHighlightedAirportCode(null); // Resetear el color del aeropuerto
  }, [setHighlightedAirportCode]);

  const handleClosePackageDetails = useCallback(() => {
    setShowPackages(false);
  }, []);

  useEffect(() => {
    if (highlightedAirportCode) {
      const marker = markerRefs.current[highlightedAirportCode];
      if (marker) {
        marker.openPopup();
      }
    }
  }, [highlightedAirportCode]);

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
            }
          }
        }
      }, 300); // Adjust delay if necessary
    }
  }, [
    selectedPackageId,
    planes,
    airports,
    setSelectedAirport,
    setHighlightedAirportCode,
  ]);

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

    if (selectedAirport) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [selectedAirport, handleCloseAirportPackages]);

  return (
    <>
      <MapContainer
        center={[17.5, -7.5]}
        zoom={3}
        style={{ height: "calc(100vh - 50px)", width: "calc(100vw - 50px)" }}
        // scrollWheelZoom={false}
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
          const cityData = airports.current.find(
            (airport) => airport.codigoIATA === city.code
          );
          const iconColor =
            highlightedAirportCode === city.code
              ? "black"
              : cityData && cityData.almacen.cantPaquetes > 0
              ? cityData.almacen.cantPaquetes < city.capacidad * 0.25 // More than or equal to full capacity (less than one-third full)
                ? "green"
                : cityData.almacen.cantPaquetes > city.capacidad * 0.8 // Between one-third and two-thirds full
                ? "red"
                : "yellow" // More than two-thirds full
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
              ref={(el) => {
                markerRefs.current[city.code] = el;
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
                  showLine={showLine}
                />
              )
          )}
      </MapContainer>
      <button
        className="toggle-line-button"
        onClick={() => setShowLine((prev) => !prev)}
      >
        {showLine ? "Ocultar Línea" : "Mostrar Línea"}
      </button>
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
