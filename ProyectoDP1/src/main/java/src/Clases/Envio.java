package src.Clases;
import java.util.List;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Envio {
    private String idEnvio;
    List <Paquete> paquetes;
   
    private LocalDateTime fechaHoraOrigen;
    private LocalDateTime fechaHoraLimite;
    private int cantPaquetes;

    private Aeropuerto aeropuertoOrigen;
    private Aeropuerto aeropuertoDestino;

}
