package src.Clases;

import java.util.Map;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Particula {
    private Map<Paquete, RutaPredefinida> posicion;
    private List<Double> velocidad;
    private Map<Paquete, RutaPredefinida> pbest;
    private double fbest;
}
