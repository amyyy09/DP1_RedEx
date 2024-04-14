package src.Clases;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Cromosoma {
    private Map<RutaTiempoReal, Paquete> gen;

    public Cromosoma(Map<RutaTiempoReal, Paquete> gen) {
        this.gen = gen;
    }

    public int getTamano() {
        return gen != null ? gen.size() : 0;
    }
}
