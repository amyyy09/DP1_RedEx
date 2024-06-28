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
public class ResultadoFinal {
    private List<Vuelo> vuelos;
    private List<Aeropuerto> aeropuertos;
}
