import React, {
  useState,
  useEffect,
  useRef,
  useMemo,
  useCallback,
} from "react";
import { Marker, Popup, Polyline } from "react-leaflet";
import L, { LatLngExpression } from "leaflet";
import { PlaneProps } from "../../types/Planes";
import { citiesByCode } from "@/app/data/cities";
import { arrayToTime } from "@/app/utils/timeHelper";
import "../../styles/popupPlane.css";
import { routesAngles } from "@/app/data/routesAngles";

const createRotatedIcon = (angle: number, color: string) => {
  return L.divIcon({
    html: `<svg width="20" height="20" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(${angle}deg);">
      <path d="M26.602,24.568l15.401,6.072l-0.389-4.902c-10.271-7.182-9.066-6.481-14.984-10.615V2.681 c0-1.809-1.604-2.701-3.191-2.681c-1.587-0.021-3.19,0.872-3.19,2.681v12.44c-5.918,4.134-4.714,3.434-14.985,10.615l-0.39,4.903 l15.401-6.072c0,0-0.042,15.343-0.006,15.581l-5.511,3.771v2.957l7.044-2.427h3.271l7.046,2.427V43.92l-5.513-3.771 C26.644,39.909,26.602,24.568,26.602,24.568z" fill="black" />
    </svg>`,
    iconSize: [20, 20],
    className: "",
  });
};

const getColorByLoadPercentage = (percentage: number) => {
  if (percentage < 20) return "green";
  if (percentage < 80) return "yellow";
  return "red";
};

const Plane: React.FC<PlaneProps & {
  isOpen: boolean;
  setForceOpenPopup: (value: boolean) => void;
  selectedPackageId: string | null;
  handleShowPackages: (vuelo: any) => void;
  showPackages: boolean;
  setShowPackages: (value: boolean) => void;
}> = ({
  vuelo,
  index,
  airports,
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
  handleShowPackages,
  showPackages,
  setShowPackages,
}) => {
  const [position, setPosition] = useState<LatLngExpression>([0, 0]);
  const [isVisible, setIsVisible] = useState(false);
  const markerRef = useRef<L.Marker>(null);
  const simulatedDate = useRef<Date>();
  const selectedPackageRef = useRef<HTMLLIElement>(null);
  const packagesListRef = useRef<HTMLDivElement>(null);
  const prevIsVisibleRef = useRef<boolean>(false);

  const origin = useMemo(
    () => citiesByCode[vuelo.aeropuertoOrigen],
    [vuelo.aeropuertoOrigen]
  );
  const destiny = useMemo(
    () => citiesByCode[vuelo.aeropuertoDestino],
    [vuelo.aeropuertoDestino]
  );

  if (dayToDay) {
    const updateTime = () => {
      if (!dayToDay) return;
      const currentTime = new Date();
      const origin = citiesByCode[vuelo.aeropuertoOrigen];
      const destiny = citiesByCode[vuelo.aeropuertoDestino];

      // Get the origin and destiny city's GMT offsets in minutes
      const originGMTOffset = origin.GMT;
      const destinyGMTOffset = destiny.GMT;

      // Convert the departure and arrival times to the system's timezone
      // Subtract 1 from the month to make it 0-indexed
      const horaSalida = arrayToTime(vuelo.horaSalida);
      // console.log("horaSalida vuelo", vuelo.horaSalida);
      // console.log("horaSalida inicial", horaSalida);
      // console.log("systemTimezoneOffset", systemTimezoneOffset);
      // console.log("horaSalida hour", horaSalida.getUTCHours()+ originGMTOffset - systemTimezoneOffset);

      horaSalida.setUTCHours(horaSalida.getUTCHours() - originGMTOffset);
      //console.log("offset", originGMTOffset);
      // console.log("horaSalida after", horaSalida);

      const horaLlegada = arrayToTime(vuelo.horaLlegada);
      //console.log("horaLlegada inicial", horaLlegada);
      horaLlegada.setUTCHours(horaLlegada.getUTCHours() - destinyGMTOffset);

      if (
        currentTime &&
        (currentTime > horaLlegada || currentTime < horaSalida)
      ) {
        setIsVisible(false);

        if (currentTime > horaLlegada) {
          // console.log("Plane has arrived día");
          // console.log("horaLlegada aquí", horaLlegada);
          vuelo.status = 2;
          clearInterval(intervalId);
          listVuelos.splice(index, 1);
          // console.log("listVuelos", listVuelos.length);
        }
        // console.log("Plane is not visible");
        // console.log("simulatedDate.current", simulatedDate.current);
        // console.log("horaSalida aquí", horaSalida);
        // console.log("horaLlegada aquí", horaLlegada);

        return;
      }

      if (
        currentTime &&
        currentTime >= horaSalida &&
        currentTime <= horaLlegada
      ) {
        // console.log("Plane is visible");
        // console.log("simulatedDate.current", simulatedDate.current);
        // console.log("horaSalida", horaSalida);
        setIsVisible(true);
      }

      const progress =
        ((currentTime?.getTime() ?? 0) - horaSalida.getTime()) /
        (horaLlegada.getTime() - horaSalida.getTime());

      // console.log("progress", progress);
      // console.log("simulatedDate.current", simulatedDate.current);
      // console.log("horaSalida", horaSalida);
      // console.log("horaLlegada", horaLlegada);

      const newLat =
        origin.coords.lat + (destiny.coords.lat - origin.coords.lat) * progress;

      const newLng =
        origin.coords.lng + (destiny.coords.lng - origin.coords.lng) * progress;

      setPosition([newLat, newLng] as LatLngExpression);
    };
    // console.log("dayToDay", dayToDay);
    const intervalId = setInterval(updateTime, 1000);
  }

  const horaSalida = useMemo(() => {
    const time = arrayToTime(vuelo.horaSalida);
    time.setUTCHours(time.getUTCHours() - origin.GMT);
    return time;
  }, [vuelo.horaSalida, origin.GMT]);

  const horaLlegada = useMemo(() => {
    const time = arrayToTime(vuelo.horaLlegada);
    time.setUTCHours(time.getUTCHours() - destiny.GMT);
    return time;
  }, [vuelo.horaLlegada, destiny.GMT]);

  const getAngle = useCallback(() => {
    const route = routesAngles.find(
      (route) =>
        route.origin === vuelo.aeropuertoOrigen &&
        route.destination === vuelo.aeropuertoDestino
    );
    return route ? route.angle : 0;
  }, [vuelo.aeropuertoOrigen, vuelo.aeropuertoDestino]);

  useEffect(() => {
    if (!startSimulation || dayToDay) return;

    const updateSimulatedTime = () => {
      if (!startSimulation || !startTime.current) return;
      const currentTime = Date.now();
      const elapsedTime = (currentTime - startTime.current) / 1000; // in seconds
      const simulatedTime = elapsedTime * speedFactor;
      const startDateSim = new Date(`${startDate}T${startHour}:00`);
      simulatedDate.current = new Date(
        startDateSim.getTime() + simulatedTime * 1000
      );

      if (
        simulatedDate.current > horaLlegada ||
        simulatedDate.current < horaSalida
      ) {
        setIsVisible(false);
        if (simulatedDate.current > horaLlegada) {
          if (vuelo.aeropuertoDestino === "WIII" || vuelo.aeropuertoOrigen === "WIII") {
            console.log("Plane has arrived correct");
            console.log("horaLlegada vuelo", vuelo.horaLlegada);
            console.log("ciudad destino", citiesByCode[vuelo.aeropuertoDestino].name);
            console.log("gmt destino", citiesByCode[vuelo.aeropuertoDestino].GMT);
            console.log("horaLlegada aquí", horaLlegada);
            console.log("simulatedDate.current", simulatedDate.current);
          }
          vuelo.status = 2;
          listVuelos.splice(index, 1);
          const foundAirport = airports.find(
            (airport) => airport.codigoIATA === vuelo.aeropuertoDestino
          );
          if (foundAirport) {
            // console.log("Aeropuerto destino", foundAirport.almacen);
            foundAirport.almacen.cantPaquetes =
              foundAirport.almacen.cantPaquetes + vuelo.cantPaquetes;
            foundAirport.almacen.paquetes =
              foundAirport.almacen.paquetes.concat(vuelo.paquetes);
            // console.log("Paquetes en el aeropuerto", foundAirport.almacen);
          } else {
            console.log("No se encontró el aeropuerto");
          }
          // console.log("listVuelos", listVuelos.length);
        }
        return;
      }

      setIsVisible(true);
      const progress =
        (simulatedDate.current.getTime() - horaSalida.getTime()) /
        (horaLlegada.getTime() - horaSalida.getTime());

      const newLat =
        origin.coords.lat + (destiny.coords.lat - origin.coords.lat) * progress;
      const newLng =
        origin.coords.lng + (destiny.coords.lng - origin.coords.lng) * progress;
      setPosition([newLat, newLng] as LatLngExpression);
    };

    const intervalId = setInterval(updateSimulatedTime, 1000 / speedFactor);

    return () => clearInterval(intervalId);
  }, [
    startSimulation,
    dayToDay,
    startTime,
    startDate,
    startHour,
    speedFactor,
    vuelo,
    index,
    listVuelos,
    horaLlegada,
    horaSalida,
    origin,
    destiny,
  ]);

  useEffect(() => {
    if (markerRef.current && isOpen) {
      markerRef.current.openPopup();
      setForceOpenPopup(false);
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
  }, [isVisible, position, getAngle, color]);

  const handlePopupClose = () => {
    setShowPackages(false);
  };

  useEffect(() => {
    // if (!isVisible && prevIsVisibleRef.current) {
    //   // console.log("Plane has arrived correct");
    //   console.log("horaLlegada aquí", vuelo.horaLlegada);
    //   // console.log("ciudad destino", citiesByCode[vuelo.aeropuertoDestino].name);
    //   console.log("gmt destino", citiesByCode[vuelo.aeropuertoDestino].GMT);
    //   console.log("simulatedDate.current", simulatedDate.current);
    //   console.log("listVuelos", listVuelos.length);
    //   const foundAirport = airports.find(
    //     (airport) => airport.codigoIATA === vuelo.aeropuertoDestino
    //   );
    //   if (foundAirport) {
    //     console.log("Aeropuerto destino", foundAirport.almacen);
    //     foundAirport.almacen.cantPaquetes = foundAirport.almacen.cantPaquetes + vuelo.cantPaquetes;
    //     foundAirport.almacen.paquetes = foundAirport.almacen.paquetes.concat(vuelo.paquetes);
    //     console.log("Paquetes en el aeropuerto", foundAirport.almacen);
    //   } else {
    //     console.log("No se encontró el aeropuerto");
    //   }
    // }
    if (isVisible && !prevIsVisibleRef.current) {
      const foundAirport = airports.find(
        (airport) => airport.codigoIATA === vuelo.aeropuertoOrigen
      );
      if (foundAirport) {
        // console.log("Aeropuerto origen", foundAirport.almacen);
        foundAirport.almacen.cantPaquetes =
          foundAirport.almacen.cantPaquetes - vuelo.cantPaquetes;
        // filter all the packages that are in the vuelo
        foundAirport.almacen.paquetes = foundAirport.almacen.paquetes.filter(
          (paquete) =>
            !vuelo.paquetes.some(
              (vueloPaquete) => vueloPaquete.id === paquete.id
            )
        );
        // console.log("Paquetes en el aeropuerto", foundAirport.almacen);
      } else {
        console.log("No se encontró el aeropuerto");
      }
    }

    prevIsVisibleRef.current = isVisible;
    // console.log("isVisible", isVisible);
  }, [isVisible]);
  const togglePackages = () => {
    if (showPackages) {
      handlePopupClose();
    } else {
      handleShowPackages(vuelo);
    }
    setShowPackages(!showPackages);
  };

  if (!isVisible) {
    return null;
  }

  return (
    <>
      {isVisible && (
        <Polyline
          positions={[
            [origin.coords.lat, origin.coords.lng],
            [destiny.coords.lat, destiny.coords.lng],
          ]}
          pathOptions={{ color: "grey", weight: 0.5, dashArray: "5,10" }}
        />
      )}
      {isVisible && (
        <>
          <Marker
          position={position}
          icon={createRotatedIcon(getAngle(), color)}
          ref={markerRef}
        >
            <Popup
              eventHandlers={{
                remove: handlePopupClose,
              }}
            >
              <div className="flight-plan-popup">
                <div className="flight-plan-popup-content">
                  <h2 style={{ fontSize: "1.5em", fontWeight: "bold" }}>Detalles de vuelo</h2>
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
                    onClick={togglePackages}
                    className="button"
                    style={{ fontSize: "0.8em", padding: "5px 10px" }}
                  >
                    {showPackages ? "Ocultar Paquetes" : "Mostrar Paquetes"}
                  </button>
                </div>
              </div>
            </Popup>
          </Marker>
        </>
      )}
    </>
  );
};

export default Plane;
