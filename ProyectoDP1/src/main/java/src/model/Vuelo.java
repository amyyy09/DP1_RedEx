package src.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vuelo {
    private String IdVuelo;
    private int cantPaquetes;
    private int capacidad;
    private int status;
    private int indexPlan;
    private LocalDateTime horaSalida;
    private LocalDateTime horaLlegada;
    private PlanDeVuelo planDeVuelo;
    private String aeropuertoOrigen;
    private String aeropuertoDestino;
    private List<Paquete> paquetes;

    public static Vuelo fromVueloNuevo(VueloNuevo vn) {
        Vuelo vuelo = new Vuelo();
        vuelo.setIdVuelo(vn.getIdVuelo());
        vuelo.setCantPaquetes(vn.getCantPaquetes());
        vuelo.setCapacidad(vn.getCapacidad());
        vuelo.setStatus(vn.getStatus());
        vuelo.setIndexPlan(vn.getIndexPlan());
        vuelo.setHoraSalida(vn.getHoraSalida());
        vuelo.setHoraLlegada(vn.getHoraLlegada());
        return vuelo;
    }
}
