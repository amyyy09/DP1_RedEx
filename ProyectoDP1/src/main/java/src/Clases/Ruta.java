package src.Clases;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ruta {

    private int IdRuta;
    private Date fechaInicio;
    private List<Vuelo> vuelos;
    private LocalTime horaLlegada;
;
}
