import React, { useState, useEffect, useRef } from "react";
import { Marker, Popup, Polyline } from "react-leaflet";
import L, { LatLngExpression } from "leaflet";
import { PlaneProps } from "../../types/Planes";
import { citiesByCode } from "@/app/data/cities";
import { arrayToTime } from "@/app/utils/timeHelper";
import "../../styles/popupPlane.css";
import { routesAngles } from "@/app/data/routesAngles";

interface Route {
  origin: string;
  destination: string;
  angle: number;
}

const createRotatedIcon = (angle: number, color: string) => {
  return L.divIcon({
    html: `<img style="transform: rotate(${angle}deg); width: 20px; height: 20px; filter: hue-rotate(${color});" src="./icons/plane.svg">`,
    iconSize: [20, 20],
    className: "",
  });
};

const getColorByLoadPercentage = (percentage: number) => {
  if (percentage < 50) return "green";
  if (percentage < 80) return "yellow";
  return "red";
};

const Plane: React.FC<
  PlaneProps & {
    isOpen: boolean;
    setForceOpenPopup: (value: boolean) => void;
    selectedPackageId: string | null;
  }
> = ({
  vuelo,
  index,
  listVuelos,
  startTime,
  startDate,
  startHour,
  speedFactor,
  startSimulation,
  dayToDay,
  isOpen,
  setForceOpenPopup,
  selectedPackageId,
}) => {
  const [position, setPosition] = useState<LatLngExpression>([0, 0]);
  const [isVisible, setIsVisible] = useState(false);
  const [showPackages, setShowPackages] = useState(false);
  const markerRef = useRef<L.Marker>(null);
  const simulatedDate = React.useRef<Date>();
  const selectedPackageRef = useRef<HTMLLIElement>(null);
  const packagesListRef = useRef<HTMLDivElement>(null);

  if (dayToDay) {
    const updateTime = () => {
      if (!dayToDay) return;
      const currentTime = new Date();
      const origin = citiesByCode[vuelo.aeropuertoOrigen];
      const destiny = citiesByCode[vuelo.aeropuertoDestino];

      const originGMTOffset = origin.GMT;
      const destinyGMTOffset = destiny.GMT;

      const horaSalida = arrayToTime(vuelo.horaSalida);

      horaSalida.setUTCHours(horaSalida.getUTCHours() - originGMTOffset);

      const horaLlegada = arrayToTime(vuelo.horaLlegada);
      horaLlegada.setUTCHours(horaLlegada.getUTCHours() - destinyGMTOffset);

      if (
        currentTime &&
        (currentTime > horaLlegada || currentTime < horaSalida)
      ) {
        setIsVisible(false);

        if (currentTime > horaLlegada) {
          console.log("Plane has arrived");
          console.log("horaLlegada aquí", horaLlegada);
          vuelo.status = 2;
          clearInterval(intervalId);
          listVuelos.splice(index, 1);
          console.log("listVuelos", listVuelos.length);
        }
        return;
      }

      if (
        currentTime &&
        currentTime >= horaSalida &&
        currentTime <= horaLlegada
      ) {
        setIsVisible(true);
      }

      const progress =
        ((currentTime?.getTime() ?? 0) - horaSalida.getTime()) /
        (horaLlegada.getTime() - horaSalida.getTime());

      const newLat =
        origin.coords.lat + (destiny.coords.lat - origin.coords.lat) * progress;

      const newLng =
        origin.coords.lng + (destiny.coords.lng - origin.coords.lng) * progress;

      setPosition([newLat, newLng] as LatLngExpression);
    };
    const intervalId = setInterval(updateTime, 1000);
  }

  const getAngle = () => {
    const route = routesAngles.find(
      (route) =>
        route.origin === vuelo.aeropuertoOrigen &&
        route.destination === vuelo.aeropuertoDestino
    );
    return route ? route.angle : 0;
  };

  useEffect(() => {
    if (!startSimulation || dayToDay) return;
    const updateSimulatedTime = () => {
      if (!startSimulation || !startTime.current) return;
      const currentTime = Date.now();
      const elapsedTime = (currentTime - startTime.current) / 1000; // in seconds
      const simulatedTime = elapsedTime * speedFactor;
      const startDateSim = new Date(startDate + "T" + startHour + ":00");
      simulatedDate.current = new Date(
        startDateSim.getTime() + simulatedTime * 1000
      );
      const systemTimezoneOffset = new Date().getTimezoneOffset();
      const origin = citiesByCode[vuelo.aeropuertoOrigen];
      const destiny = citiesByCode[vuelo.aeropuertoDestino];

      const originGMTOffset = origin.GMT;
      const destinyGMTOffset = destiny.GMT;

      const horaSalida = arrayToTime(vuelo.horaSalida);
      horaSalida.setUTCHours(horaSalida.getUTCHours() - originGMTOffset);
      const horaLlegada = arrayToTime(vuelo.horaLlegada);
      horaLlegada.setUTCHours(horaLlegada.getUTCHours() - destinyGMTOffset);

      if (
        simulatedDate.current &&
        (simulatedDate.current > horaLlegada ||
          simulatedDate.current < horaSalida)
      ) {
        setIsVisible(false);

        if (simulatedDate.current > horaLlegada) {
          console.log("Plane has arrived");
          console.log("horaLlegada aquí", horaLlegada);
          console.log("simulatedDate.current", simulatedDate.current);
          vuelo.status = 2;
          clearInterval(intervalId);
          listVuelos.splice(index, 1);
          console.log("listVuelos", listVuelos.length);
        }
        return;
      }

      if (
        simulatedDate.current &&
        simulatedDate.current >= horaSalida &&
        simulatedDate.current <= horaLlegada
      ) {
        setIsVisible(true);
      }

      const progress =
        ((simulatedDate.current?.getTime() ?? 0) - horaSalida.getTime()) /
        (horaLlegada.getTime() - horaSalida.getTime());

      const newLat =
        origin.coords.lat + (destiny.coords.lat - origin.coords.lat) * progress;

      const newLng =
        origin.coords.lng + (destiny.coords.lng - origin.coords.lng) * progress;

      setPosition([newLat, newLng] as LatLngExpression);
    };

    const intervalId = setInterval(updateSimulatedTime, 100 / speedFactor);

    return () => {
      clearInterval(intervalId);
    };
  }, [
    dayToDay,
    index,
    listVuelos,
    speedFactor,
    startDate,
    startHour,
    startSimulation,
    startTime,
    vuelo,
  ]);

  useEffect(() => {
    if (vuelo === undefined) return;
    if (startTime === undefined) return;
    if (startDate === undefined) return;
    if (!startSimulation) return;
  }, [vuelo, startTime, startDate, startSimulation]);

  useEffect(() => {
    if (markerRef.current && isOpen) {
      markerRef.current.openPopup();
      setShowPackages(true); // Automatically show packages
      setForceOpenPopup(false); // Reset the forceOpenPopup state after opening
    }
  }, [isOpen, setForceOpenPopup]);

  useEffect(() => {
    if (showPackages && selectedPackageRef.current && packagesListRef.current) {
      packagesListRef.current.scrollTo({
        top:
          selectedPackageRef.current.offsetTop -
          packagesListRef.current.offsetTop,
        behavior: "smooth",
      });
    }
  }, [showPackages]);

  const loadPercentage = (vuelo.cantPaquetes / vuelo.capacidad) * 100;
  const color = getColorByLoadPercentage(loadPercentage);

  useEffect(() => {
    const angle = getAngle();
    const icon = createRotatedIcon(angle, color);
    if (isVisible && position) {
      markerRef.current?.setIcon(icon);
    }
  }, [vuelo, isVisible, position, color, getAngle]);

  const handlePopupClose = () => {
    setShowPackages(false);
  };

  if (!isVisible) {
    return null;
  }

  return (
    <>
      {isVisible && (
        <Polyline
          positions={[
            [
              citiesByCode[vuelo.aeropuertoOrigen].coords.lat,
              citiesByCode[vuelo.aeropuertoOrigen].coords.lng,
            ],
            [
              citiesByCode[vuelo.aeropuertoDestino].coords.lat,
              citiesByCode[vuelo.aeropuertoDestino].coords.lng,
            ],
          ]}
          pathOptions={{ color: "grey", weight: 0.5, dashArray: "5,10" }}
        />
      )}
      {isVisible && (
        <Marker
          position={position}
          icon={createRotatedIcon(getAngle(), color)} // Set the rotated icon here
          ref={markerRef}
        >
          <Popup
            eventHandlers={{
              remove: handlePopupClose,
            }}
          >
            <div>
              <h2 style={{ fontSize: "1.5em", fontWeight: "bold" }}>
                Detalles de vuelo
              </h2>
              <p>
                <strong>Origen:</strong>{" "}
                {citiesByCode[vuelo.aeropuertoOrigen].name}
              </p>
              <p>
                <strong>Destino:</strong>{" "}
                {citiesByCode[vuelo.aeropuertoDestino].name}
              </p>
              <p>
                <strong>Hora de salida:</strong>{" "}
                {arrayToTime(vuelo.horaSalida).toLocaleString(undefined, {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                  second: "2-digit",
                  hour12: false,
                })}
              </p>
              <p>
                <strong>GMT origen:</strong>
                {citiesByCode[vuelo.aeropuertoOrigen].GMT}
              </p>
              <p>
                <strong>Hora de llegada:</strong>{" "}
                {arrayToTime(vuelo.horaLlegada).toLocaleString(undefined, {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                  second: "2-digit",
                  hour12: false,
                })}
              </p>
              <p>
                <strong>GMT destino:</strong>
                {citiesByCode[vuelo.aeropuertoDestino].GMT}
              </p>
              <p>
                <strong>Capacidad:</strong> {vuelo.capacidad}
              </p>
              <p>
                <strong>Cantidad de paquetes:</strong> {vuelo.cantPaquetes}
              </p>
              <button
                onClick={() => setShowPackages(!showPackages)}
                className="button"
                style={{ fontSize: "0.8em", padding: "5px 10px" }}
              >
                {showPackages ? "Ocultar Paquetes" : "Mostrar Paquetes"}
              </button>
              {showPackages && vuelo.paquetes && (
                <div
                  ref={packagesListRef}
                  style={{ maxHeight: "100px", overflowY: "auto" }}
                >
                  <ul>
                    {vuelo.paquetes.map((paquete, index) => (
                      <li
                        key={index}
                        ref={
                          paquete.id === selectedPackageId
                            ? selectedPackageRef
                            : null
                        }
                        style={{
                          fontWeight:
                            paquete.id === selectedPackageId
                              ? "bold"
                              : "normal",
                          fontSize:
                            paquete.id === selectedPackageId ? "1.2em" : "1em", // Change font size for selected package
                          color:
                            paquete.id === selectedPackageId ? "red" : "black", // Change color for selected package
                        }}
                      >
                        <strong>ID:</strong> {paquete.id},{" "}
                        <strong>Status:</strong> {paquete.status}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          </Popup>
        </Marker>
      )}
    </>
  );
};

export default Plane;
