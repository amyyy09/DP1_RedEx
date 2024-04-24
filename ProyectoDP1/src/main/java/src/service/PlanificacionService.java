package src.service;

import src.model.*;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanificacionService {
    private final double probabilidadSeleccion = 0.7;
    private double probabilidadMutacion = 0.1;
    private double probabilidadCruce = 0.85;
    private int numCromosomas = 100;
    private int tamanoTorneo = 5;
    private int numDescendientes = 50;
    private int numGeneraciones = 20;

    private static FitnessEvaluatorService evaluator = new FitnessEvaluatorService();
    private static Random rand = new Random();

    @Autowired
    public PlanificacionService() {
        rand = new Random();
        PlanificacionService.evaluator = new FitnessEvaluatorService();
    }

    public Cromosoma ejecutarAlgoritmoGenetico(List<Envio> envios, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActuales, List<PlanDeVuelo> planesDeVuelo) throws IOException {

        List<RutaPredefinida> rutasPred = generarRutas(aeropuertos, planesDeVuelo);
        List<Cromosoma> poblacion = createPopulation(envios, rutasPred, numCromosomas, aeropuertos);
        Random rand = new Random();

        for (int generacion = 0; generacion < numGeneraciones; generacion++) {
            List<Double> fitnessAgregado = evaluator.calcularFitnessAgregado(poblacion, aeropuertos, vuelosActuales);
            if (!fitnessAgregado.isEmpty() && fitnessAgregado.get(0) >= 0) {
                System.out.println("Se ha encontrado una solución satisfactoria en la generación " + generacion);
                return poblacion.get(0);
            }

            List<Cromosoma> matingPool = TournamentSelection(poblacion, probabilidadSeleccion, tamanoTorneo,
                    fitnessAgregado);
            List<Cromosoma> descendientes = new ArrayList<>();

            // Generar descendientes
            for (int j = 0; j < numDescendientes; j++) {
                int indexPadre1 = rand.nextInt(matingPool.size());
                int indexPadre2 = rand.nextInt(matingPool.size());
                if (Math.random() < probabilidadCruce) {
                    List<Cromosoma> hijos = crossover(matingPool.get(indexPadre1), matingPool.get(indexPadre2));

                    // Aplicar mutación con probabilidad probabilidadMutacion
                    hijos.forEach(hijo -> {
                        if (Math.random() < probabilidadMutacion) {
                            mutarHijo(hijo, rutasPred); // Asumiendo que mutarHijos puede ahora manejar un solo hijo
                        }
                    });

                    descendientes.addAll(hijos);
                }
            }

            // Reemplazar la población vieja con los descendientes para la siguiente
            // generación
            poblacion = new ArrayList<>(descendientes);
        }

        System.out
                .println("No se encontró una solución satisfactoria después de " + numGeneraciones + " generaciones.");
        return null; // Devolver el mejor cromosoma encontrado o null si no se encontró solución
    }

    public void mutarHijo(Cromosoma hijo, List<RutaPredefinida> rutasDisponibles) {
        Random rand = new Random();

        if (Math.random() < probabilidadMutacion) {
            // Selecciona un gen (ruta) al azar para mutar.
            List<RutaPredefinida> claves = new ArrayList<>(hijo.getGen().keySet());
            RutaPredefinida rutaAMutar = claves.get(rand.nextInt(claves.size()));

            // Selecciona una nueva ruta diferente a la actual.
            RutaPredefinida nuevaRuta;
            do {
                nuevaRuta = rutasDisponibles.get(rand.nextInt(rutasDisponibles.size()));
            } while (nuevaRuta.equals(rutaAMutar));

            // Encuentra el paquete asociado a la ruta que se va a mutar y actualiza la
            // asignación.
            Paquete paqueteAMutar = hijo.getGen().get(rutaAMutar);
            hijo.getGen().remove(rutaAMutar);
            hijo.getGen().put(nuevaRuta, paqueteAMutar);
        }
    }

    private static List<Cromosoma> TournamentSelection(List<Cromosoma> poblacion, double ps, int tamanoTorneo,
            List<Double> fitnessAgregado) {
        List<Cromosoma> matingPool = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < poblacion.size(); i++) {
            List<Cromosoma> torneo = new ArrayList<>();
            for (int j = 0; j < tamanoTorneo; j++) {
                torneo.add(poblacion.get(rand.nextInt(poblacion.size())));
            }
            torneo.sort(Comparator.comparing(c -> fitnessAgregado.get(poblacion.indexOf(c))));
            matingPool.add(torneo.get(torneo.size() - 1)); // Agregar el de mejor fitness
        }
        return matingPool;
    }

    public static List<Cromosoma> crossover(Cromosoma padre1, Cromosoma padre2) {

        Map<RutaPredefinida, Paquete> genPadre1 = new HashMap<>(padre1.getGen());
        Map<RutaPredefinida, Paquete> genPadre2 = new HashMap<>(padre2.getGen());

        List<Map.Entry<RutaPredefinida, Paquete>> listaGenPadre1 = new ArrayList<>(genPadre1.entrySet());
        List<Map.Entry<RutaPredefinida, Paquete>> listaGenPadre2 = new ArrayList<>(genPadre2.entrySet());

        Random random = new Random();
        int puntoCruce = random.nextInt(listaGenPadre1.size());

        for (int i = puntoCruce; i < listaGenPadre1.size(); i++) {
            Map.Entry<RutaPredefinida, Paquete> temp = listaGenPadre1.get(i);
            listaGenPadre1.set(i, listaGenPadre2.get(i));
            listaGenPadre2.set(i, temp);
        }

        Map<RutaPredefinida, Paquete> genHijo1 = new HashMap<>();
        Map<RutaPredefinida, Paquete> genHijo2 = new HashMap<>();
        for (int i = 0; i < listaGenPadre1.size(); i++) {
            genHijo1.put(listaGenPadre1.get(i).getKey(), listaGenPadre1.get(i).getValue());
            genHijo2.put(listaGenPadre2.get(i).getKey(), listaGenPadre2.get(i).getValue());
        }

        Cromosoma hijo1 = new Cromosoma(genHijo1);
        Cromosoma hijo2 = new Cromosoma(genHijo2);
        List<Cromosoma> hijos = new ArrayList<>();
        hijos.add(hijo1);
        hijos.add(hijo2);

        return hijos;
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
                    RutaPredefinida rutaPredefinida = rutasPred
                            .get(random.nextInt(rutasPred.size()));
                    gen.put(rutaPredefinida, paquete);
                }
            }
            Cromosoma cromosoma = new Cromosoma(gen);
            poblacion.add(cromosoma);
        }

        return poblacion;
    }

    public static List<RutaPredefinida> generarRutas(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes) {
        List<RutaPredefinida> rutas = new ArrayList<>();
        for (Aeropuerto origen : aeropuertos) {
            for (Aeropuerto destino : aeropuertos) {
                if (!origen.equals(destino) && origen.getContinente()
                        .equals(destino.getContinente())) {
                    List<Integer> daysm = new ArrayList<>();
                    List<List<PlanDeVuelo>> _planesRutas = generarEscalas(origen, destino, planes,
                            daysm);
                    for (List<PlanDeVuelo> _planRuta : _planesRutas) {
                        RutaPredefinida ruta = new RutaPredefinida();
                        ruta.setCodigoIATAOrigen(origen.getCodigoIATA());
                        ruta.setCodigoIATADestino(destino.getCodigoIATA());
                        ruta.setEscalas(_planRuta);
                        rutas.add(ruta);
                    }
                }
            }
        }
        return rutas;
    }

    public static List<List<PlanDeVuelo>> generarEscalas(Aeropuerto origen, Aeropuerto destino,
            List<PlanDeVuelo> planes, List<Integer> daysm) {
        List<List<PlanDeVuelo>> allRoutes = new ArrayList<>();
        List<PlanDeVuelo> currentRoute = new ArrayList<>();

        dfs(origen.getCodigoIATA(), destino.getCodigoIATA(), currentRoute, allRoutes, planes, daysm, 0);
        return allRoutes;
    }

    private static void dfs(String current, String destination, List<PlanDeVuelo> currentRoute,
            List<List<PlanDeVuelo>> allRoutes, List<PlanDeVuelo> planes, List<Integer> daysm, int days) {

        if (currentRoute.size() > 8) {
            return; // Si se exceden 8 escalas, detiene la recursión para esta ruta
        }

        if (current.equals(destination)) {
            List<PlanDeVuelo> routeToAdd = new ArrayList<>(currentRoute);
            if (!containsRoute(allRoutes, routeToAdd)) {
                allRoutes.add(routeToAdd);
                daysm.add(days);
                return;
            }
        }

        for (PlanDeVuelo plan : planes) {
            if (plan.getCodigoIATAOrigen().equals(current)) {
                OffsetTime arrivalTime = plan.getHoraLlegada().withOffsetSameInstant(ZoneOffset.UTC);
                OffsetTime departureTime = plan.getHoraSalida().withOffsetSameInstant(ZoneOffset.UTC);
                OffsetTime finalTime = null;

                if (currentRoute.size() > 0) {
                    PlanDeVuelo lastPlan = currentRoute.get(currentRoute.size() - 1);
                    finalTime = lastPlan.getHoraLlegada().withOffsetSameInstant(ZoneOffset.UTC);
                    if (!finalTime.plusMinutes(5).isBefore(departureTime)) {
                        continue;
                    }
                    if (currentRoute.stream()
                            .anyMatch(p -> p.getCodigoIATAOrigen()
                                    .equals(plan.getCodigoIATADestino()))) {
                        continue;
                    }
                }

                if (arrivalTime.isBefore(departureTime)
                        || (finalTime != null && departureTime.isBefore(finalTime))) {
                    days++;
                }

                if (plan.isSameContinent()) {
                    if (!currentRoute.contains(plan)) {
                        if (days <= 1) {
                            currentRoute.add(plan);
                            dfs(plan.getCodigoIATADestino(), destination, currentRoute,
                                    allRoutes, planes, daysm, days);
                            currentRoute.remove(currentRoute.size() - 1);
                        }
                        if (arrivalTime.isBefore(departureTime)
                                || (finalTime != null
                                        && departureTime.isBefore(finalTime))) {
                            days--;
                        }
                    }
                } else {
                    if (!currentRoute.contains(plan)) {
                        if (days <= 2) {
                            currentRoute.add(plan);
                            dfs(plan.getCodigoIATADestino(), destination, currentRoute,
                                    allRoutes, planes, daysm, days);
                            currentRoute.remove(currentRoute.size() - 1);
                        }
                        if (arrivalTime.isBefore(departureTime)
                                || (finalTime != null
                                        && departureTime.isBefore(finalTime))) {
                            days--;
                        }
                    }
                }
            }
        }
    }

    private static boolean containsRoute(List<List<PlanDeVuelo>> allRoutes, List<PlanDeVuelo> currentRoute) {
        return allRoutes.stream().anyMatch(route -> route.equals(currentRoute));
    }

    // PSO

    public static Map<Paquete, RutaTiempoReal> PSO(List<Paquete> paquetes, List<RutaPredefinida> rutasPred,
            List<Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActuales) {
        List<Particula> population = new ArrayList<>();
        int numParticles = 50;
        int numIterationsMax = 100;
        double w = 0.1, c1 = 0.1, c2 = 0.1;

        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(Particula.inicializarPosicion(paquetes, rutasPred, aeropuertos));
            particle.setVelocidad(Particula.inicializarVelocidad(paquetes.size()));
            particle.setPbest(particle.getPosicion());
            particle.setFbest(evaluator.fitness(particle.getPbest(), aeropuertos, vuelosActuales));
            population.add(particle);
        }
        Map<Paquete, RutaTiempoReal> gbest = Particula.determineGbest(population, aeropuertos, vuelosActuales);

        for (int j = 0; j < numIterationsMax; j++) {
            for (Particula particle : population) {
                for (int k = 0; k < paquetes.size(); k++) {
                    double r1 = rand.nextDouble(), r2 = rand.nextDouble();
                    double velocity = w * particle.getVelocidad().get(k) +

                            c1 * r1 * (rutasPred.indexOf(particle.getPbest().get(paquetes.get(k)).getRutaPredefinida())
                                    - rutasPred
                                            .indexOf(particle.getPosicion().get(paquetes.get(k)).getRutaPredefinida()))
                            +

                            c2 * r2 * (rutasPred.indexOf(gbest.get(paquetes.get(k)).getRutaPredefinida()) - rutasPred
                                    .indexOf(particle.getPosicion().get(paquetes.get(k)).getRutaPredefinida()));

                    int velint = Particula.verifyLimits(velocity, rutasPred);

                    particle.getVelocidad().set(k, (double) velint);

                    RutaPredefinida newPosition = rutasPred
                            .get(rutasPred.indexOf(particle.getPosicion().get(paquetes.get(k)).getRutaPredefinida())
                                    + velint);

                    RutaTiempoReal newRTR = newPosition.convertirAPredefinidaEnTiempoReal(aeropuertos);

                    particle.getPosicion().put(paquetes.get(k), newRTR);
                }

                double fit = evaluator.fitness(particle.getPosicion(), aeropuertos, vuelosActuales);

                if (fit < particle.getFbest()) {
                    particle.setPbest(particle.getPosicion());
                    particle.setFbest(fit);
                }
            }
            Map<Paquete, RutaTiempoReal> currentGbest = Particula.determineGbest(population, aeropuertos,
                    vuelosActuales);
            if (evaluator.fitness(currentGbest, aeropuertos, vuelosActuales) < evaluator.fitness(gbest,
                    aeropuertos, vuelosActuales)) {
                gbest = currentGbest;
            }
        }
        return gbest;
    }

}
