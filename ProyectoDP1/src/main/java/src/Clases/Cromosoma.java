package src.Clases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
 
    public static List<Cromosoma> createPopulation(List<Envio> envios, List<RutaPredefinida> rutasPred, int numCromosomas) {
        List<Cromosoma> poblacion = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numCromosomas; i++) {
            Map<RutaTiempoReal, Paquete> gen = new HashMap<>();

            for (Envio envio : envios) {
                List<Paquete> paquetes = envio.getPaquetes();
                for (Paquete paquete : paquetes) {
                    // Asignar al paquete una ruta real basada en una ruta predefinida aleatoria
                    RutaPredefinida rutaPredefinida = rutasPred.get(random.nextInt(rutasPred.size()));
                    RutaTiempoReal rutaTiempoReal = convertirAPredefinidaEnTiempoReal(rutaPredefinida);
                    gen.put(rutaTiempoReal, paquete);
                }
            }
            Cromosoma cromosoma = new Cromosoma(gen);
            poblacion.add(cromosoma);
        }

        return poblacion;
    }

    private static RutaTiempoReal convertirAPredefinidaEnTiempoReal(RutaPredefinida rutaPredefinida) {
        // Transforma RutaPredefinida a RutaTiempoReal
        // Esta es una implementación ficticia, debes implementarla según tus requerimientos
        return new RutaTiempoReal(); // Retorna un nuevo objeto RutaTiempoReal basado en RutaPredefinida
    }
    
}
