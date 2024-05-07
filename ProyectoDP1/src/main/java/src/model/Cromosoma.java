package src.model;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Cromosoma {
    private Map<Paquete,RutaTiempoReal> gen;

    public Cromosoma(Map<Paquete,RutaTiempoReal> gen) {
        this.gen = gen;
    }

    public Map<Paquete,RutaTiempoReal> getGenes() {
        return gen;
    }

    public int getSize() {
        return gen != null ? gen.size() : 0;
    }
}
