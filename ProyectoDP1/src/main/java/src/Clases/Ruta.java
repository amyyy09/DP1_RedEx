package src.Clases;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
//import java.time.LocalTime;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ruta {

    private int IdRuta;
    private Ciudad origen;
    private Ciudad destino;
    private LocalDateTime horaInicio; //LocalDateTime
    private LocalDateTime horaLlegada; //LocalDateTime
    private List<Vuelo> vuelos;
}
