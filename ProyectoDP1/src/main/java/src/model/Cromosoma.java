package src.model;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Cromosoma {
    private Map<RutaPredefinida, Paquete> gen;

    public Cromosoma(Map<RutaPredefinida, Paquete> gen) {
        this.gen = gen;
    }

    public Map<RutaPredefinida, Paquete> getGenes() {
        return gen;
    }

    public int getSize() {
        return gen != null ? gen.size() : 0;
    }
}
