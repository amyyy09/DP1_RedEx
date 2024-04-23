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
    private Map<RutaPredefinida, Paquete> gen;

    public Cromosoma(Map<RutaPredefinida, Paquete> gen) {
        this.gen = gen;
    }

    public Map<RutaPredefinida, Paquete> getGenes() {
        return gen;
    }

    public int getTamano() {
        return gen != null ? gen.size() : 0;
    }

    public static List<Cromosoma> createPopulation(List<Envio> envios, List<RutaPredefinida> rutasPred,
            int numCromosomas, List<Aeropuerto> aeropuertos) {
        List<Cromosoma> poblacion = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numCromosomas; i++) {
            Map<RutaPredefinida, Paquete> gen = new HashMap<>();

            for (Envio envio : envios) {
                List<Paquete> paquetes = envio.getPaquetes();
                for (Paquete paquete : paquetes) {
                    RutaPredefinida rutaPredefinida = rutasPred.get(random.nextInt(rutasPred.size()));
                    gen.put(rutaPredefinida, paquete);
                }
            }
            Cromosoma cromosoma = new Cromosoma(gen);
            poblacion.add(cromosoma);
        }

        return poblacion;
    }

    public static void imprimirGenDelCromosoma(Cromosoma cromosoma) {
        Map<RutaPredefinida, Paquete> gen = cromosoma.getGen(); // Asumiendo que existe un método getGen
        for (Map.Entry<RutaPredefinida, Paquete> entrada : gen.entrySet()) {
            RutaPredefinida ruta = entrada.getKey();
            Paquete paquete = entrada.getValue();
            System.out.println("Ruta de " + ruta.getCodigoIATAOrigen() + " a " + ruta.getCodigoIATADestino() +
                    " con paquete ID: " + paquete.getIdEnvio() + ", Salida: " + ruta.getHoraSalida() +
                    ", Llegada: " + ruta.getHoraLlegada());
        }
    }
}
