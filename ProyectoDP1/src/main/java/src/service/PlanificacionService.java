package src.service;

import src.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanificacionService {
    private static FitnessEvaluatorService evaluator = new FitnessEvaluatorService();
    private static Random rand = new Random();

    @Autowired
    public PlanificacionService() {
        rand = new Random();
        PlanificacionService.evaluator = new FitnessEvaluatorService();
    }
    // PSO

    public Map<Paquete, RutaTiempoReal> PSO(List<Envio> envios, List<Paquete> paquetes, List<RutaPredefinida> rutasPred,
            Map<String, Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActuales) {
        List<Particula> population = new ArrayList<>();
        int numParticles = 50;
        int numIterationsMax = 100;
        double w = 0.5, c1 = 1, c2 = 2;

        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredMap = createMap(
                rutasPred);

        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(Particula.inicializarPosicion(envios, rutasPredMap, aeropuertos, vuelosActuales));
            particle.setVelocidad(Particula.inicializarVelocidad(paquetes.size()));
            particle.setPbest(particle.getPosicion());
            particle.setFbest(evaluator.fitness(particle.getPbest(), almacenes, vuelosActuales, false));
            population.add(particle);
        }
        Map<Paquete, RutaTiempoReal> gbest = Particula.determineGbest(population, almacenes, vuelosActuales);
        int noImprovementCounter = 0;
        // for (int j = 0; j < numIterationsMax; j++) {
        int j = 0;
        while (noImprovementCounter < numIterationsMax && j < 750) {
            // if(evaluator.fitness(gbest, aeropuertos, vuelosActuales) == 0){
            // return gbest;
            // }
            for (Particula particle : population) {
                for (int k = 0; k < envios.size(); k++) {
                    List<RutaPredefinida> filteredRutasPred = filterRutasForEnvio(rutasPredMap, envios.get(k));
                    for (int l = 0; l < envios.get(k).getPaquetes().size(); l++) {
                        // Update velocity and position for each package (paquete) in the envio
                        double r1 = rand.nextDouble(), r2 = rand.nextDouble();
                        Paquete paquete = envios.get(k).getPaquetes().get(l);

                        int indexPos = filteredRutasPred
                                .indexOf(particle.getPosicion().get(paquete).getRutaPredefinida());
                        double velocity = w * particle.getVelocidad().get(k) +
                                c1 * r1 * (indexPos
                                        - filteredRutasPred.indexOf(
                                                population.get(k).getPbest().get(paquete).getRutaPredefinida()))
                                +
                                c2 * r2 * (indexPos
                                        - filteredRutasPred.indexOf(gbest.get(paquete).getRutaPredefinida()));

                        particle.getVelocidad().set(k, velocity);

                        double newPosIndex = indexPos + velocity;

                        int posIndex = Particula.verifyLimits(newPosIndex, filteredRutasPred);

                        RutaPredefinida newPosition = rutasPred.get(posIndex);

                        RutaTiempoReal newRTR = newPosition.convertirAPredefinidaEnTiempoReal(aeropuertos,
                                vuelosActuales);

                        particle.getPosicion().put(paquete, newRTR);
                    }
                }

                double fit = evaluator.fitness(particle.getPosicion(), almacenes, vuelosActuales, false);

                if (fit < particle.getFbest()) {
                    particle.setPbest(particle.getPosicion());
                    particle.setFbest(fit);
                }
            }
            Map<Paquete, RutaTiempoReal> currentGbest = Particula.determineGbest(population, almacenes,
                    vuelosActuales);
            if (evaluator.fitness(currentGbest, almacenes, vuelosActuales, false) > evaluator.fitness(gbest, almacenes,
                    vuelosActuales, false)) {
                gbest = currentGbest;
                noImprovementCounter = 0; // reset the counter when there's an improvement
            } else {
                // Double fit = evaluator.fitness(gbest, almacenes, vuelosActuales, false);
                noImprovementCounter++; // increment the counter when there's no improvement
            }
            j++;
        }
        double fit = evaluator.fitness(gbest, almacenes, vuelosActuales, true);
        // if (fit < 0) {
        System.out.println("Fitness: " + fit);
        // }
        return gbest;
        // return null;
    }

    public static List<RutaPredefinida> filterRutasForEnvio(
            Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPred,
            Envio envio) {

        String codigoIATAOrigen = envio.getCodigoIATAOrigen();
        String codigoIATADestino = envio.getCodigoIATADestino();
        int horaLlegada = envio.getFechaHoraOrigen().getHour() * 100 + envio.getFechaHoraOrigen().getMinute();

        Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>> origenMap = rutasPred
                .getOrDefault(codigoIATAOrigen, new HashMap<>());
        TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>> destinoMap = origenMap.getOrDefault(codigoIATADestino,
                new TreeMap<>());

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

        // Map<RutaPredefinida, Integer> filteredRutasPredMap = new HashMap<>();
        // for (int i = 0; i < filteredRutasPred.size(); i++) {
        // filteredRutasPredMap.put(filteredRutasPred.get(i), i);
        // }

        return filteredRutasPred;
    }

    public static Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> createMap(
            List<RutaPredefinida> rutasPred) {
        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredMap = new HashMap<>();
        for (RutaPredefinida ruta : rutasPred) {
            String origen = ruta.getCodigoIATAOrigen();
            String destino = ruta.getCodigoIATADestino();
            int horaSalida = ruta.getHoraSalida().getHour() * 100 + ruta.getHoraSalida().getMinute();
            int horaLlegada = ruta.getHoraLlegada().getHour() * 100 + ruta.getHoraLlegada().getMinute();

            Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>> origenMap = rutasPredMap
                    .getOrDefault(origen, new HashMap<>());
            TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>> destinoMap = origenMap.getOrDefault(destino,
                    new TreeMap<>());
            TreeMap<Integer, List<RutaPredefinida>> llegadaMap = destinoMap.getOrDefault(horaLlegada, new TreeMap<>());
            List<RutaPredefinida> rutas = llegadaMap.getOrDefault(horaSalida, new ArrayList<>());
            rutas.add(ruta);
            llegadaMap.put(horaSalida, rutas);
            destinoMap.put(horaLlegada, llegadaMap);
            origenMap.put(destino, destinoMap);
            rutasPredMap.put(origen, origenMap);
        }
        return rutasPredMap;
    }

}
