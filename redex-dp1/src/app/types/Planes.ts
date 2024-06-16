export interface Airport {
  name: string;
  coords: { lat: number; lng: number };
  code: string;
  GMT: number;
  capacidad: number;
}

export interface PlaneProps {
  vuelo: Vuelo;
  index: number;
  listVuelos: Vuelo[];
  startTime: React.RefObject<number>;
  startDate: string;
  startHour: string;
  // simulatedDate: React.RefObject<Date>;
  speedFactor: number;
  startSimulation: boolean;
  dayToDay: boolean;
}

export class Vuelo {
  cantPaquetes: number;
  capacidad: number;
  status: number;
  indexPlan: number;
  horaSalida: Array<number>;
  horaLlegada: Array<number>;
  aeropuertoOrigen: string;
  aeropuertoDestino: string;
  idVuelo: string;

  constructor(data: any) {
    this.cantPaquetes = data.cantPaquetes;
    this.capacidad = data.capacidad;
    this.status = data.status;
    this.indexPlan = data.indexPlan;
    this.horaSalida = data.horaSalida;
    this.horaLlegada = data.horaLlegada;
    // this.horaSalida = new Date(data.horaSalida[0], data.horaSalida[1] - 1, data.horaSalida[2], data.horaSalida[3], data.horaSalida[4]);
    // this.horaLlegada = new Date(data.horaLlegada[0], data.horaLlegada[1] - 1, data.horaLlegada[2], data.horaLlegada[3], data.horaLlegada[4]);
    this.aeropuertoOrigen = data.aeropuertoOrigen;
    this.aeropuertoDestino = data.aeropuertoDestino;
    this.idVuelo = data.idVuelo;
  }
}