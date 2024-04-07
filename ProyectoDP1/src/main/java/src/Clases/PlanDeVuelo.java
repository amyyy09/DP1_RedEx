package src.Clases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PlanDeVuelo {
   private String IdPlan;
    private int capacidadMaxima;
    private LocalTime horaSalida;
    private LocalTime horaLlegada;

    private Aeropuerto aeropuertoOrigen;
    private Aeropuerto aeropuertoDestino;
}
