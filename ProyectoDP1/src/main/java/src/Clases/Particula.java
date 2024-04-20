package src.Clases;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Particula {
    private Map<Paquete, RutaPredefinida> posicion;
    private Map<Paquete, Double> velocidad;
    private Map<Paquete, RutaPredefinida> pbest;
}
