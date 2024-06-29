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
      <path d="M26.602,24.568l15.401,6.072l-0.389-4.902c-10.271-7.182-9.066-6.481-14.984-10.615V2.681 c0-1.809-1.604-2.701-3.191-2.681c-1.587-0.021-3.19,0.872-3.19,2.681v12.44c-5.918,4.134-4.714,3.434-14.985,10.615l-0.39,4.903 l15.401-6.072c0,0-0.042,15.343-0.006,15.581l-5.511,3.771v2.957l7.044-2.427h3.271l7.046,2.427V43.92l-5.513-3.771 C26.644,39.909,26.602,24.568,26.602,24.568z" fill="${color}" />
    </svg>`,
    iconSize: [20, 20],
    className: "",
  });
};

const getColorByCantidadPaquetes = (cantidadPaquetes: number) => {
  if (cantidadPaquetes == 1) return "green";
  if (cantidadPaquetes > 1 && cantidadPaquetes < 3) return "yellow";
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
  const simulatedDate = useRef<Date>();
  const selectedPackageRef = useRef<HTMLLIElement>(null);
  const packagesListRef = useRef<HTMLDivElement>(null);

  const origin = useMemo(
    () => citiesByCode[vuelo.aeropuertoOrigen],
    [vuelo.aeropuertoOrigen]
  );
  const destiny = useMemo(
    () => citiesByCode[vuelo.aeropuertoDestino],
    [vuelo.aeropuertoDestino]
  );

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
          vuelo.status = 2;
          listVuelos.splice(index, 1);
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
      setShowPackages(true);
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

  const color = getColorByCantidadPaquetes(vuelo.cantPaquetes);

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
        <Marker
          position={position}
          icon={createRotatedIcon(getAngle(), color)}
          ref={markerRef}
        >
          <Popup eventHandlers={{ remove: handlePopupClose }}>
            <div>
              <h2 style={{ fontSize: "1.5em", fontWeight: "bold" }}>
                Detalles de vuelo
              </h2>
              <p>
                <strong>Origen:</strong> {origin.name}
              </p>
              <p>
                <strong>Destino:</strong> {destiny.name}
              </p>
              <p>
                <strong>Hora de salida:</strong>{" "}
                {horaSalida.toLocaleString(undefined, {
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
                <strong>GMT origen:</strong> {origin.GMT}
              </p>
              <p>
                <strong>Hora de llegada:</strong>{" "}
                {horaLlegada.toLocaleString(undefined, {
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
                <strong>GMT destino:</strong> {destiny.GMT}
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
                            paquete.id === selectedPackageId ? "1.2em" : "1em",
                          color:
                            paquete.id === selectedPackageId ? "red" : "black",
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
