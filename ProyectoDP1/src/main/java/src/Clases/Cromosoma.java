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

    public Map<RutaTiempoReal, Paquete> getGenes() {
        return gen; // Devuelve la referencia directa, las modificaciones afectan al mapa original
    }

    public int getTamano() {
        return gen != null ? gen.size() : 0;
    }
}
