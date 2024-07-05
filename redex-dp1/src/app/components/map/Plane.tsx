import React, {
  useState,
  useEffect,
  useRef,
  useMemo,
  useCallback,
} from "react";
import { Marker, Popup } from "react-leaflet";
import L, { LatLngExpression, LatLng } from "leaflet";
import { PlaneProps, Vuelo } from "../../types/Planes";
import { citiesByCode } from "@/app/data/cities";
import { arrayToTime } from "@/app/utils/timeHelper";
import { routesAngles } from "@/app/data/routesAngles";
import GeodesicLine from "./GeodesicLine";
import FlightDetails from "./FlightDetails";
import "../../styles/popupPlane.css";

const createRotatedIcon = (angle: number, color: string) => {
  return L.divIcon({
    html: `<svg width="20" height="20" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(${angle}deg);">
      <path d="M26.602,24.568l15.401,6.072l-0.389-4.902c-10.271-7.182-9.066-6.481-14.984-10.615V2.681 c0-1.809-1.604-2.701-3.191-2.681c-1.587-0.021-3.19,0.872-3.19,2.681v12.44c-5.918,4.134-4.714,3.434-14.985,10.615l-0.39,4.903 l15.401-6.072c0,0-0.042,15.343-0.006,15.581l-5.511,3.771v2.957l7.044-2.427h3.271l7.046,2.427V43.92l-5.513-3.771 C26.644,39.909,26.602,24.568,26.602,24.568z" fill=${color} />
    </svg>`,
    iconSize: [20, 20],
    className: "",
  });
};

const getColorByLoadPercentage = (percentage: number) => {
  if (percentage < 1 / 3) return "green";
  if (percentage < 2 / 3) return "yellow";
  return "red";
};

const Plane: React.FC<
  PlaneProps & {
    isOpen: boolean;
    setForceOpenPopup: (value: boolean) => void;
    selectedPackageId: string | null;
    handleShowPackages: (vuelo: any) => void;
    showPackages: boolean;
    setShowPackages: (value: boolean) => void;
    selectedPlaneId: string | null;
    setSelectedPlaneId: (value: string | null) => void;
  }
> = ({
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
  vuelosInAir,
  selectedPlaneId,
  setSelectedPlaneId,
}) => {
  const [position, setPosition] = useState<LatLngExpression>([0, 0]);
  const [isVisible, setIsVisible] = useState(false);
  const markerRef = useRef<L.Marker>(null);
  const simulatedDate = useRef<Date>();
  const prevIsVisibleRef = useRef<boolean>(false);
  const flightDetailsRef = useRef<HTMLDivElement>(null);

  if (dayToDay) {
    const updateTimeDia = () => {
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
    const intervalId = setInterval(updateTimeDia, 1000);
  }

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

      // console.log("simulatedDate.current", simulatedDate.current);

      const systemTimezoneOffset = new Date().getTimezoneOffset();

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
        simulatedDate.current &&
        (simulatedDate.current > horaLlegada ||
          simulatedDate.current < horaSalida)
      ) {
        setIsVisible(false);

        if (simulatedDate.current > horaLlegada) {
          // if (vuelo.aeropuertoDestino === "WIII" || vuelo.aeropuertoOrigen === "WIII") {
          //check if the plane is in the destination airport position

          // if (index === 367) {
          // console.log("Plane has arrived correct");
          // console.log("horaLlegada vuelo", vuelo.horaLlegada);
          // console.log(
          //   "ciudad destino",
          //   citiesByCode[vuelo.aeropuertoDestino].name
          // );
          // console.log(
          //   "gmt destino",
          //   citiesByCode[vuelo.aeropuertoDestino].GMT
          // );
          // console.log("horaLlegada aquí", horaLlegada);
          // console.log("simulatedDate.current", simulatedDate.current);
          // }

          clearInterval(intervalId);

          const foundAirport = airports.find(
            (airport) => airport.codigoIATA === vuelo.aeropuertoDestino
          );
          if (foundAirport) {
            // console.log("Aeropuerto destino", foundAirport.almacen);
            // foundAirport.almacen.cantPaquetes =
            //   foundAirport.almacen.cantPaquetes + vuelo.cantPaquetes;
            vuelo.paquetes.forEach((paquete) => {
              if (paquete.aeropuertoDestino === foundAirport.codigoIATA) {
                paquete.status = 2;
              } else {
                paquete.status = 1;
                foundAirport.almacen.paquetes.push(paquete);
              }
            });

            foundAirport.almacen.cantPaquetes =
              foundAirport.almacen.paquetes.length;
            // console.log("Paquetes en el aeropuerto", foundAirport.almacen);
          } else {
            console.log("No se encontró el aeropuerto");
          }
          // console.log("listVuelos", listVuelos.length);
          vuelo.status = 2;
          vuelosInAir.current--;
          vuelo.enAire = false;
          // listVuelos.splice(index, 1);
        }
        // console.log("Plane is not visible");
        // console.log("simulatedDate.current", simulatedDate.current);
        // console.log("horaSalida aquí", horaSalida);
        // console.log("horaLlegada aquí", horaLlegada);

        return;
      }

      if (
        simulatedDate.current &&
        simulatedDate.current >= horaSalida &&
        simulatedDate.current <= horaLlegada
      ) {
        // console.log("Plane is visible");
        // console.log("simulatedDate.current", simulatedDate.current);
        // console.log("horaSalida", horaSalida);
        setIsVisible(true);
      }

      const progress =
        ((simulatedDate.current?.getTime() ?? 0) - horaSalida.getTime()) /
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

    // Call updateSimulatedTime every second
    const intervalId = setInterval(updateSimulatedTime, 100 / speedFactor);

    return () => {
      clearInterval(intervalId);
    };
  }, [startSimulation]);

  const getAngle = useCallback(() => {
    const route = routesAngles.find(
      (route) =>
        route.origin === vuelo.aeropuertoOrigen &&
        route.destination === vuelo.aeropuertoDestino
    );
    return route ? route.angle : 0;
  }, [vuelo.aeropuertoOrigen, vuelo.aeropuertoDestino]);

  useEffect(() => {
    if (markerRef.current && isOpen) {
      markerRef.current.openPopup();
      setForceOpenPopup(false);
    }
  }, [isOpen, setForceOpenPopup]);

  useEffect(() => {
    if (markerRef.current && selectedPlaneId === vuelo.idVuelo) {
      markerRef.current.openPopup();
    }
  }, [selectedPlaneId, vuelo.idVuelo]);

  const loadPercentage = vuelo.cantPaquetes / (vuelo.capacidad - 220);
  const color = getColorByLoadPercentage(loadPercentage);

  // useEffect(() => {
  //   const angle = getAngle();
  //   const icon = createRotatedIcon(angle, color);
  //   if (isVisible && position) {
  //     markerRef.current?.setIcon(icon);
  //   }
  // }, [isVisible, position, getAngle, color]);

  const handlePopupClose = () => {
    setSelectedPlaneId(null);
  };

  const togglePackages = () => {
    handleShowPackages(vuelo);
    setShowPackages(!showPackages);
  };

  useEffect(() => {
    if (isVisible && !prevIsVisibleRef.current) {
      vuelo.enAire = true;
      vuelo.paquetes.forEach((paquete) => {
        paquete.status = vuelo.indexPlan;
      });

      const foundAirport = airports.find(
        (airport) => airport.codigoIATA === vuelo.aeropuertoOrigen
      );
      if (foundAirport) {
        foundAirport.almacen.paquetes = foundAirport.almacen.paquetes.filter(
          (paquete) => !vuelo.paquetes.some((p) => p.id === paquete.id)
        );
        foundAirport.almacen.cantPaquetes =
          foundAirport.almacen.paquetes.length;

        // foundAirport.almacen.cantPaquetes =
        //   foundAirport.almacen.cantPaquetes - vuelo.cantPaquetes;
      } else {
        console.log("No se encontró el aeropuerto");
      }
      vuelosInAir.current++;
    }

    prevIsVisibleRef.current = isVisible;
    // console.log("isVisible", isVisible);
  }, [isVisible]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        flightDetailsRef.current &&
        !flightDetailsRef.current.contains(event.target as Node)
      ) {
        handlePopupClose();
      }
    };

    if (selectedPlaneId === vuelo.idVuelo) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [selectedPlaneId, vuelo.idVuelo]);

  if (!isVisible) {
    return null;
  }

  return (
    <>
      <GeodesicLine
        isVisible={isVisible}
        citiesByCode={citiesByCode}
        vuelo={vuelo}
      />
      {isVisible && (
        <>
          <Marker
            position={position}
            icon={createRotatedIcon(
              getAngle(),
              selectedPlaneId === vuelo.idVuelo ? "black" : color
            )}
            ref={markerRef}
            eventHandlers={{
              click: () => {
                console.log("Vuelo seleccionado:", vuelo);
                setSelectedPlaneId(vuelo.idVuelo);
              },
            }}
          />
        </>
      )}
      {selectedPlaneId === vuelo.idVuelo && (
        <FlightDetails
          vuelo={vuelo}
          onClose={handlePopupClose}
          showPackages={showPackages}
          togglePackages={togglePackages}
          ref={flightDetailsRef}
        />
      )}
    </>
  );
};

export default Plane;
