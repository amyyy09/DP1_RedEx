export interface Paquete {
    iD: string;
    status: number;
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
