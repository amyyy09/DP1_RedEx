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
public class Resultado {
    private int idRuta;
    private String aeropuertoOrigen;
    private String aeropuertoDestino;
    private LocalDateTime horaInicio;
    private LocalDateTime horaLlegada;
    private List<Vuelo> vuelos;
    private int status;
    private int capacidadAlmacen;
}
