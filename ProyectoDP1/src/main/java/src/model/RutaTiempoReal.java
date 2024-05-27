package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RutaTiempoReal {
    private int idRuta;
    private Aeropuerto origen;
    private Aeropuerto destino;
    // private Almacen xAlmacen;s
    private LocalDateTime horaInicio;
    private LocalDateTime horaLlegada;
    private List<Vuelo> vuelos;
    private RutaPredefinida rutaPredefinida;
    private int status;
}
