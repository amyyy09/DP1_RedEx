export interface Airport {
  name: string;
  coords: { lat: number; lng: number };
  code: string;
  GMT: number;
  capacidad: number;
}

export interface PlaneProps {
  origin: Airport;
  destiny: Airport;
  departureTime: string;
  arrivalTime: string;
  capacidad: number;
  controlClock?: number;
}

export interface FlightProps {
  id: string;
  date: Airport;
  paquetes: string;
}