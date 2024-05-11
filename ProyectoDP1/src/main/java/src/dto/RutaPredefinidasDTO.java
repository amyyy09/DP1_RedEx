package src.dto;

import java.time.OffsetDateTime;

public class RutaPredefinidasDTO {
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    OffsetDateTime horaSalida;
    OffsetDateTime horaLlegada;
    int duracion;
    boolean isSameContinente;   
}
