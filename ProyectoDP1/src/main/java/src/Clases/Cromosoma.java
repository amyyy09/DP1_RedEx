package src.Clases;

import java.time.LocalDateTime;
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

    // eficiencia de nodos entre dos puntos, vuelos
    public int getTamano() {
        return gen != null ? gen.size() : 0;
    }

    public static List<Cromosoma> createPopulation(List<Envio> envios, List<RutaPredefinida> rutasPred,
            int numCromosomas, List<Aeropuerto> aeropuertos) {
        List<Cromosoma> poblacion = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numCromosomas; i++) {
            Map<RutaTiempoReal, Paquete> gen = new HashMap<>();

            for (Envio envio : envios) {
                List<Paquete> paquetes = envio.getPaquetes();
                for (Paquete paquete : paquetes) {
                    RutaPredefinida rutaPredefinida = rutasPred.get(random.nextInt(rutasPred.size()));
                    RutaTiempoReal rutaTiempoReal = convertirAPredefinidaEnTiempoReal(rutaPredefinida, aeropuertos);
                    gen.put(rutaTiempoReal, paquete);
                }
            }
            Cromosoma cromosoma = new Cromosoma(gen);
            poblacion.add(cromosoma);
        }

        return poblacion;
    }

    private static RutaTiempoReal convertirAPredefinidaEnTiempoReal(RutaPredefinida rutaPredefinida,
            List<Aeropuerto> aeropuertos) {
        RutaTiempoReal rutaTiempoReal = new RutaTiempoReal();

        // Suponemos que tienes una forma de obtener los objetos Aeropuerto basados en
        // el código IATA
        Aeropuerto origen = aeropuertos.stream()
                .filter(a -> a.getCodigoIATA().equals(rutaPredefinida.getCodigoIATAOrigen()))
                .findFirst()
                .orElse(null);
        Aeropuerto destino = aeropuertos.stream()
                .filter(a -> a.getCodigoIATA().equals(rutaPredefinida.getCodigoIATADestino()))
                .findFirst()
                .orElse(null);

        // Asumimos que cada aeropuerto tiene un almacén asociado y podemos obtenerlo
        // directamente
        Almacen almacenOrigen = (origen != null) ? origen.getAlmacen() : null;
        LocalDateTime horaInicio = null;
        LocalDateTime horaFin = null;

        List<Vuelo> vuelos = new ArrayList<>(); // Esto debería ser poblado según lógica específica

        rutaTiempoReal.setIdRuta(1); // Generar un ID aleatorio o de alguna otra forma
        rutaTiempoReal.setOrigen(origen);
        rutaTiempoReal.setDestino(destino);
        rutaTiempoReal.setXAlmacen(almacenOrigen);
        rutaTiempoReal.setHoraInicio(horaInicio);
        rutaTiempoReal.setHoraLlegada(horaFin);
        rutaTiempoReal.setVuelos(vuelos);
        rutaTiempoReal.setStatus(0); // Status inicial, suponemos '0' para no activa

        return rutaTiempoReal;
    }

}
