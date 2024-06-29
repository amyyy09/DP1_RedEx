// Definir el tipo FlightPlan
export interface FlightPlan {
    indexPlan: number;
    fechaSalida: number[];
    fechaLLegada: number[];
    aeropuertoSalida: string;
    aeropuertoDestino: string;
  }