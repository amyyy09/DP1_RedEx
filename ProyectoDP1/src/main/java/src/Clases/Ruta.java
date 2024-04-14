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
public class Ruta {
    private int IdRuta;
    private Aeropuerto origen;
    private Aeropuerto destino;
    private LocalDateTime horaInicio;
    private LocalDateTime horaLlegada;
    private List<Vuelo> vuelos;
}
