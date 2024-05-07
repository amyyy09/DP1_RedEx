package src.service;

import src.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<RutaPredefinida> generarRutas(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes) {
        List<RutaPredefinida> rutas = new ArrayList<>();
        Aeropuerto origen = aeropuertos.stream()//en este caso solo tenemos de origen ZBAA
            .filter(a -> a.getCodigoIATA().equals("ZBAA"))
            .findFirst()
            .orElse(null);
            if (origen == null) return rutas;
        List<String> destinosEspecificos = Arrays.asList("SKBO","SEQM","SVMI","SBBR","SPIM","SLLP","SCEL","SABE","SGAS","SUAA","LATI", "EDDI","LOWW","EBCI","UMMS","LBSF","LKPR","LDZA","EKCH","EHAM","VIDP","RKSI", "VTBS","OMDB","ZBAA","RJTT","WMKK","WSSS","WIII","RPLL"); //para nuestro experimento tenemos solo un aeropuerto destino WMKK
        // List<Aeropuerto> destinos = aeropuertos.stream()
        //                                    .filter(a -> destinosEspecificos.contains(a.getCodigoIATA()))
        //                                    .collect(Collectors.toList());
        for (Aeropuerto destino  : aeropuertos) {
            if (!origen.equals(destino)) {
                List<Integer> daysm = new ArrayList<>();
                List<List<PlanDeVuelo>> planesRutas = generarEscalas(origen, destino, planes,daysm, origen.getContinente().equals(destino.getContinente()));                
                for (int i = 0; i < planesRutas.size(); i++) {
                    List<PlanDeVuelo> planRuta = planesRutas.get(i);
                    RutaPredefinida ruta = new RutaPredefinida(
                        origen.getCodigoIATA(),
                        destino.getCodigoIATA(),
                        planRuta.get(0).getHoraSalida(),
                        planRuta.get(planRuta.size() - 1).getHoraLlegada(),
                        planRuta,
                        daysm.get(i) // get the corresponding value from the daysm array
                    );
                    rutas.add(ruta);
                }
            }
        }
        return rutas;
    }

    public static List<List<PlanDeVuelo>> generarEscalas(Aeropuerto origen, Aeropuerto destino, List<PlanDeVuelo> planes, List<Integer> daysm, Boolean sameContinent) {
        List<List<PlanDeVuelo>> allRoutes = new ArrayList<>();
        List<PlanDeVuelo> currentRoute = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        dfs(origen.getCodigoIATA(), destino.getCodigoIATA(), currentRoute, allRoutes, planes, daysm, 0, sameContinent, visited);
        return allRoutes;
    }

    private static void dfs(String current, String destination, List<PlanDeVuelo> currentRoute,
                 List<List<PlanDeVuelo>> allRoutes, List<PlanDeVuelo> planes, List<Integer> daysm,
                 int totalDays, boolean sameContinent, Set<String> visited) {

        if (current.equals(destination)) {
            if (!currentRoute.isEmpty() && !allRoutes.contains(currentRoute)) {
                List<PlanDeVuelo> routeToAdd = new ArrayList<>(currentRoute);
                allRoutes.add(routeToAdd);
                daysm.add(totalDays);
                // print the route for debugging with the respective horas de salida y llegada
                // System.out.println("Ruta encontrada: en " + totalDays + " días");
                // for (PlanDeVuelo plan : routeToAdd) {
                //     System.out.println(plan.getCodigoIATAOrigen() + " " + plan.getHoraSalida() + " -> " + plan.getCodigoIATADestino() + " " +  " " + plan.getHoraLlegada());
                // }
                return;
            }
        }
        if (currentRoute.size() > 3 || visited.contains(current)) {
            return; // Limit recursion depth and prevent visiting the same airport in one route
        }
    
        visited.add(current);
        List<PlanDeVuelo> filteredPlanes = planes.stream()
            .filter(plan -> plan.getCodigoIATAOrigen().equals(current))
            .collect(Collectors.toList());

        for (PlanDeVuelo plan : filteredPlanes) {
            if (!visited.contains(plan.getCodigoIATADestino())) {
                
                if (currentRoute.isEmpty() || currentRoute.get(currentRoute.size() - 1).getHoraLlegada().plusMinutes(5).isBefore(plan.getHoraSalida())) { // Ensure at least 5 minutes between flights
                    currentRoute.add(plan);
                    int newTotalDays = totalDays;
                    if(plan.getHoraLlegada().isBefore(plan.getHoraSalida()) 
                        || (currentRoute.size() > 1 && 
                        plan.getHoraSalida().isBefore(currentRoute.get(currentRoute.size() - 2).getHoraLlegada()))){

                        newTotalDays++;
                    }
                    
                    if (sameContinent && (newTotalDays > 1 || (newTotalDays > 0 && (currentRoute.size() > 1 && 
                        plan.getHoraLlegada().toLocalTime().isAfter(currentRoute.get(0).getHoraSalida().toLocalTime()))))){
                        currentRoute.remove(currentRoute.size() - 1);
                        visited.remove(current);
                        return; // Abort the route if it takes more than 1 day in the same continent
                    }

                    if (!sameContinent && (newTotalDays > 2 || (newTotalDays > 1 && 
                        plan.getHoraLlegada().toLocalTime().isAfter(currentRoute.get(0).getHoraSalida().toLocalTime())))) {
                        currentRoute.remove(currentRoute.size() - 1);
                        visited.remove(current);
                        return; // Abort the route if it exceeds 2 days and not in the same continent
                    }
    
                    dfs(plan.getCodigoIATADestino(), destination, currentRoute, allRoutes, planes, daysm, newTotalDays, sameContinent, visited);
                    currentRoute.remove(currentRoute.size() - 1);
                }
            }
        }
        visited.remove(current);
    }

    // PSO

    public Map<Paquete, RutaTiempoReal> PSO(List<Envio> envios, List<Paquete> paquetes, List<RutaPredefinida> rutasPred,
            List<Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActuales) {
        List<Particula> population = new ArrayList<>();
        int numParticles = 25;
        int numIterationsMax = 20;
        double w = 0.5, c1 = 1, c2 = 2;
        
        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(Particula.inicializarPosicion(envios, rutasPred, aeropuertos,vuelosActuales));
            particle.setVelocidad(Particula.inicializarVelocidad(paquetes.size()));
            particle.setPbest(particle.getPosicion());
            particle.setFbest(evaluator.fitness(particle.getPbest(), aeropuertos, vuelosActuales));
            population.add(particle);
        }
        Map<Paquete, RutaTiempoReal> gbest = Particula.determineGbest(population, aeropuertos, vuelosActuales);
        for (int j = 0; j < numIterationsMax; j++) {
            // if(evaluator.fitness(gbest, aeropuertos, vuelosActuales) == 0){
            //     return gbest;
            // }
            for (Particula particle : population) {
                for (int k = 0; k < envios.size(); k++) {
                    List<RutaPredefinida> filteredRutasPred = filterRutasForEnvio(rutasPred, envios.get(k));
                    for (int l = 0; l< envios.get(k).getPaquetes().size(); l++) {
                        // Update velocity and position for each package (paquete) in the envio
                        double r1 = rand.nextDouble(), r2 = rand.nextDouble();
                        Paquete paquete = envios.get(k).getPaquetes().get(l);
                        
                        int indexPos = filteredRutasPred.indexOf(particle.getPosicion().get(paquete).getRutaPredefinida());
                        double velocity = w * particle.getVelocidad().get(k) +
                            c1 * r1 * (indexPos
                                - filteredRutasPred.indexOf(particle.getPosicion().get(paquete).getRutaPredefinida()))
                            +
                            c2 * r2 * (indexPos
                                - filteredRutasPred.indexOf(particle.getPosicion().get(paquete).getRutaPredefinida()));

                        particle.getVelocidad().set(k, velocity);
                                    
                        double newPosIndex = indexPos + velocity;

                        int posIndex = Particula.verifyLimits(newPosIndex, filteredRutasPred);

                        RutaPredefinida newPosition = rutasPred.get(posIndex);

                        RutaTiempoReal newRTR = newPosition.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales);

                        particle.getPosicion().put(paquete, newRTR);
                    }
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
        // return null;
    }

    public static List<RutaPredefinida> filterRutasForEnvio(List<RutaPredefinida> rutasPred, Envio envio) {
        
        String codigoIATAOrigen = envio.getCodigoIATAOrigen();
        String codigoIATADestino = envio.getCodigoIATADestino();
        int horaSalida = envio.getFechaHoraOrigen().getHour()*100 + envio.getFechaHoraOrigen().getMinute();
        int horaLlegada = envio.getFechaHoraOrigen().getHour()*100 + envio.getFechaHoraOrigen().getMinute();

        List<RutaPredefinida> filteredRutasPred = rutasPred.stream()
            .filter(ruta -> ruta.getCodigoIATAOrigen().equals(codigoIATAOrigen) 
                && ruta.getCodigoIATADestino().equals(codigoIATADestino)
                && ruta.getHoraLlegada().getHour()*100 + ruta.getHoraLlegada().getMinute() >= horaLlegada
                && (ruta.getHoraSalida().getHour()*100 + ruta.getHoraSalida().getMinute() <= horaSalida
                || (ruta.getHoraSalida().getHour()*100 + ruta.getHoraSalida().getMinute() >= horaSalida
                && ruta.getDuracion() < 1))).toList();

        return filteredRutasPred;
    }

}
