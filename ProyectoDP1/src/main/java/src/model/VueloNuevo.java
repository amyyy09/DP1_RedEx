package src.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VueloNuevo {
    private int cantPaquetes;
    private int capacidad;
    private int status;
    private int indexPlan;
    private LocalDateTime horaSalida;
    private LocalDateTime horaLlegada;
    private String aeropuertoOrigen;
    private String aeropuertoDestino;
    private String IdVuelo;
}
