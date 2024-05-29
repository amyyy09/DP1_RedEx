package src.model;

import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import src.services.FitnessEvaluatorService;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Particula {
    private Map<Paquete, RutaTiempoReal> posicion;
    private List<Double> velocidad;
    private Map<Paquete, RutaTiempoReal> pbest;
    private double fbest;

    public static Map<Paquete, RutaTiempoReal> inicializarPosicion(List<Envio> envios,
            Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPred, 
            List<Aeropuerto> aeropuertos, List<Vuelo> vuelosActivos, LocalDateTime fechaHora) {
        Map<Paquete, RutaTiempoReal> position = new HashMap<>();
        for (Envio envio : envios) {
            String codigoIATAOrigen = envio.getCodigoIATAOrigen();
            String codigoIATADestino = envio.getCodigoIATADestino();
            int horaLlegada = envio.getFechaHoraOrigen().getHour()*100 + envio.getFechaHoraOrigen().getMinute();

            Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>> origenMap = rutasPred.getOrDefault(codigoIATAOrigen, new HashMap<>());
            TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>> destinoMap = origenMap.getOrDefault(codigoIATADestino, new TreeMap<>());

            SortedMap<Integer, TreeMap<Integer, List<RutaPredefinida>>> llegadaSubMap = destinoMap.headMap(horaLlegada);
            List<RutaPredefinida> filteredRutasPred = new ArrayList<>();

            for (TreeMap<Integer, List<RutaPredefinida>> llegadaMap : llegadaSubMap.values()) {
                SortedMap<Integer, List<RutaPredefinida>> salidaSubMap = llegadaMap.tailMap(horaLlegada);
                for (List<RutaPredefinida> rutas : salidaSubMap.values()) {
                    filteredRutasPred.addAll(rutas);
                }
                SortedMap<Integer, List<RutaPredefinida>> salidaSubMapExtra = llegadaMap.headMap(horaLlegada);
                for (List<RutaPredefinida> rutas : salidaSubMapExtra.values()) {
                    for (RutaPredefinida ruta : rutas) {
                        if ((ruta.isSameContinent() && ruta.getDuracion() == 0) || 
                            (!ruta.isSameContinent() && ruta.getDuracion() < 2)) {
                            filteredRutasPred.add(ruta);
                        }
                    }
                }
            }

            SortedMap<Integer, TreeMap<Integer, List<RutaPredefinida>>> llegadaSubMap2 = destinoMap.tailMap(horaLlegada);
            for (TreeMap<Integer, List<RutaPredefinida>> llegadaMap : llegadaSubMap2.values()) {
                SortedMap<Integer, List<RutaPredefinida>> salidaSubMap = llegadaMap.tailMap(horaLlegada);
                for (List<RutaPredefinida> rutas : salidaSubMap.values()) {
                    for (RutaPredefinida ruta : rutas) {
                        if ((ruta.isSameContinent() && ruta.getDuracion() == 0) || 
                            (!ruta.isSameContinent() && ruta.getDuracion() < 2)) {
                            filteredRutasPred.add(ruta);
                        }
                    }
                }
                SortedMap<Integer, List<RutaPredefinida>> salidaSubMapExtra = llegadaMap.headMap(horaLlegada);
                for (List<RutaPredefinida> rutas : salidaSubMapExtra.values()) {
                    for (RutaPredefinida ruta : rutas) {
                        if ((!ruta.isSameContinent() && ruta.getDuracion() < 1)) {
                            filteredRutasPred.add(ruta);
                        }
                    }
                }
            }

            for (Paquete pkg : envio.getPaquetes()) {
                if(filteredRutasPred.isEmpty()){
                    position.put(pkg, null);
                }else{
                int index = new Random().nextInt(filteredRutasPred.size());
                RutaPredefinida randomRoute = filteredRutasPred.get(index);
                // cambia un poco la lógica de la siguiente línea, se le añade la fecha hora del envío
                RutaTiempoReal randTiempoReal = randomRoute.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActivos, fechaHora);
                position.put(pkg, randTiempoReal);
                }
            }
            
        }
        return position;
    }

    public static List<Double> inicializarVelocidad(int numPaquetes) {
        List<Double> velocity = new ArrayList<>();
        for (int i = 0; i < numPaquetes; i++) {
            double randomChange = new Random().nextDouble();
            velocity.add(randomChange);
        }
        return velocity;
    }

    public static int verifyLimits(double velocity, List<RutaPredefinida> rutasPred) {
        int val = (int) Math.floor(velocity);
    
        if (val < 0) {
            val = (val % rutasPred.size() + rutasPred.size()) % rutasPred.size();
        }
        if (val >= rutasPred.size()) {
            val = val % rutasPred.size();
        }
    
        return val;
    }

    public static Map<Paquete, RutaTiempoReal> determineGbest(List<Particula> population, Map<String, Almacen> almacenes,
            List<Vuelo> vuelos) {
        Map<Paquete, RutaTiempoReal> gbest = new HashMap<Paquete, RutaTiempoReal>(population.get(0).getPosicion());
        FitnessEvaluatorService fitnessEvaluator = new FitnessEvaluatorService();
        double bestFitness = fitnessEvaluator.fitness(gbest, almacenes, vuelos, false);
        for (Particula particle : population) {
            if (particle.getFbest() < bestFitness) {
                gbest = new HashMap<Paquete, RutaTiempoReal>(particle.getPbest());
                bestFitness = particle.getFbest();
            }
        }
        return gbest;
    }
}
