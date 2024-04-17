package src.Clases;

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
    private Almacen xAlmacen;
    private LocalDateTime horaInicio;
    private LocalDateTime horaLlegada;
    private List<Vuelo> vuelos;
    private int status; // indica si la ruta esta actualmente activa, en el aire o en el aeropuerto

    // get capcidad del almcen actual (o del avion actul)
}
