export interface Paquete {
  status: number;
  horaInicio: Array<number>;
  aeropuertoOrigen: string;
  aeropuertoDestino: string;
  id: string;
  ruta: string;
  ubicacion: string;
}

export interface Envio {
  idEnvio: string;
  fechaHoraOrigen: string;
  zonaHorariaGMT: number;
  codigoIATAOrigen: string;
  codigoIATADestino: string;
  cantPaquetes: number;
  paquetes: Paquete[];
}

export interface PeticionPSOD {
  envios: Envio[];
}
