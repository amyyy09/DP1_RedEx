package src.Clases;

import java.util.List;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Envio {
    private String idEnvio;
    private int cantPaquetes;
    List<Paquete> paquetes;
    private OffsetDateTime fechaHoraOrigen;
    private OffsetDateTime fechaHoraLimite;
    private Aeropuerto aeropuertoOrigen;
    private Aeropuerto aeropuertoDestino;

}
