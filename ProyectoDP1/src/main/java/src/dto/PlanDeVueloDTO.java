package src.dto;

import java.time.LocalDateTime;
import java.time.OffsetTime;

public class PlanDeVueloDTO {
    private String idPlanDeVuelo;
    private String idAeropuertoOrigen;
    private String idAeropuertoDestino;
    private OffsetTime horaSalidadLocal; 
    private OffsetTime horaLlegadaLocal;
    private int capacidad;
    private boolean transcontinental;   
}
