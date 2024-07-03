export class Airport {
  codigoIATA: string;
  ciudad: string;
  pais: string;
  continente: string;
  alias: string;
  zonaHorariaGMT: number;
  almacen: {
    capacidad: number;
    cantPaquetes: number;
    paquetes: Array<{
      status: number;
      horaInicio: Array<number>;
      aeropuertoOrigen: string;
      aeropuertoDestino: string;
      id: string;
    }>;
  };
  latitud: string;
  longitud: string;

  constructor(data: any) {
    this.codigoIATA = data.codigoIATA;
    this.ciudad = data.ciudad;
    this.pais = data.pais;
    this.continente = data.continente;
    this.alias = data.alias;
    this.zonaHorariaGMT = data.zonaHorariaGMT;
    this.almacen = {
      capacidad: data.almacen.capacidad,
      cantPaquetes: data.almacen.cantPaquetes,
      paquetes: data.almacen.paquetes.map(
        (paquete: any) => ({
          status: paquete.status,
          horaInicio: paquete.horaInicio,
          aeropuertoOrigen: paquete.aeropuertoOrigen,
          aeropuertoDestino: paquete.aeropuertoDestino,
          id: paquete.id,
        } as any)
      ),
    };
    this.latitud = data.latitud;
    this.longitud = data.longitud;
  }
}

export interface PlaneProps {
  vuelo: Vuelo;
  index: number;
  airports: Airport[];
  listVuelos: Vuelo[];
  startTime: React.RefObject<number>;
  startDate: string;
  startHour: string;
  // simulatedDate: React.RefObject<Date>;
  speedFactor: number;
  startSimulation: boolean;
  dayToDay: boolean;
  vuelosInAir: React.MutableRefObject<number>;
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
  paquetes: Array<{
    status: number;
    horaInicio: Array<number>;
    aeropuertoOrigen: string;
    aeropuertoDestino: string;
    id: string;
  }>;
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
    this.paquetes = data.paquetes || [];
    this.idVuelo = data.idVuelo;
  }
}
