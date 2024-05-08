package src.service;

import src.model.*;
import src.utility.AsignarRutas;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanificacionService {
    private double probabilidadSeleccion = 0.7;
    private double probabilidadMutacion = 0.1;
    private double probabilidadCruce = 0.85;
    private int numCromosomas = 100;
    private int tamanoTorneo = 5;
    private int numDescendientes = 50;
    private int numGeneraciones = 150;

    private static FitnessEvaluatorService evaluator = new FitnessEvaluatorService();
    private static Random rand = new Random();
    private static AsignarRutas asignador = new AsignarRutas();

    @Autowired
    public PlanificacionService() {
        rand = new Random();
        PlanificacionService.evaluator = new FitnessEvaluatorService();
    }

    public Cromosoma ejecutarAlgoritmoGenetico(List<Envio> envios, List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planesDeVuelo,List<RutaPredefinida> rutasPred,List<Almacen> almacenes,List<Vuelo> vuelosActuales) throws IOException {

        
        List<Cromosoma> poblacion = createPopulation(envios, numCromosomas, aeropuertos,planesDeVuelo,rutasPred,vuelosActuales); //LISTO NO TOCAR
        Random rand = new Random();
        double fitnesfinal=0;
        
        for (int generacion = 0; generacion < numGeneraciones; generacion++) {
            List<Double> fitnessAgregado = evaluator.calcularFitnessAgregado(poblacion, aeropuertos,vuelosActuales);//FALTA Revisar porque todos los fitness salen iguales 
            
            evaluator.ordernarPoblacion(poblacion, fitnessAgregado);//LISTO NO TOCAR

            if (!fitnessAgregado.isEmpty() && fitnessAgregado.get(0) >= 0) {
                System.out.println(fitnessAgregado.get(0));
                System.out.println("Se ha encontrado una solución satisfactoria en la generación " + generacion);
                return poblacion.get(0);
            }

            List<Cromosoma> matingPool = TournamentSelection(poblacion, probabilidadSeleccion, tamanoTorneo,fitnessAgregado); //LISTO NO TOCAR
            List<Cromosoma> descendientes = new ArrayList<>();

            // Generar descendientes
            for (int j = 0; j < numDescendientes/2; j++) {
                int indexPadre1 = rand.nextInt(matingPool.size());
                int indexPadre2 = rand.nextInt(matingPool.size());
                if (Math.random() < probabilidadCruce) {
                    List<Cromosoma> hijos = new ArrayList<>();
                    hijos= crossover(matingPool.get(indexPadre1), matingPool.get(indexPadre2)); //LISTO NO TOCAR
                    
                    for (Cromosoma hijo : hijos) {
                        if (Math.random() < probabilidadMutacion) {
                            mutarHijo(hijo, probabilidadMutacion, rand,rutasPred,aeropuertos,vuelosActuales);
                        }
                    }
                    descendientes.addAll(hijos);
                }
            }
            List<Double> fitnessAgregadoDesc = evaluator.calcularFitnessAgregado(descendientes, aeropuertos,vuelosActuales);


            //NO TOCAR nada de aqui 
            //agregar a mi poblacion los descendientes
            poblacion.addAll(descendientes);
            //agregar el fitness agregado de los descendientes
            fitnessAgregado.addAll(fitnessAgregadoDesc);

            //ordenar la poblacion
            evaluator.ordernarPoblacion(poblacion, fitnessAgregado);

            //eliminar los peores
            poblacion = poblacion.subList(0, numCromosomas);
            fitnesfinal=fitnessAgregado.get(0);
            //NO TOCAR hasta de aqui 
        }
        
        System.out.println(fitnesfinal);
        System.out
                .println("No se encontró una solución satisfactoria después de " + numGeneraciones + " generaciones.");
        return null; // Devolver el mejor cromosoma encontrado o null si no se encontró solución
    }

    public List<RutaPredefinida> filtrarRutasPorCodigoIATAOrigen(List<RutaPredefinida> rutas, String codigoIATAOrigen) {
        return rutas.stream()
                    .filter(ruta -> ruta.getCodigoIATAOrigen().equals(codigoIATAOrigen))
                    .collect(Collectors.toList());
    }

    public void mutarHijo(Cromosoma cromosoma, double probabilidadMutacion, Random rand,List<RutaPredefinida> rutasPred,List<Aeropuerto> aeropuertos,List<Vuelo> vuelosActuales) {
        // Iterar sobre cada gen en el cromosoma
        for (Map.Entry<Paquete, RutaTiempoReal> entry : cromosoma.getGen().entrySet()) {
            if (rand.nextDouble() < probabilidadMutacion) {
                // Seleccionar un nuevo plan de vuelo al azar para mutar este gen
                Envio envio = entry.getKey().getEnvio();
                List<RutaPredefinida> filteredRutasPred = rutasPred.stream().filter(ruta -> ruta.getCodigoIATAOrigen().equals(envio.getCodigoIATAOrigen()) && ruta.getCodigoIATADestino().equals(envio.getCodigoIATADestino()) && ruta.getHoraSalida().getHour() >= envio.getFechaHoraOrigen().getHour() && ruta.getHoraLlegada().getHour() <= envio.getFechaHoraOrigen().getHour()).collect(Collectors.toList());
                    if(!filteredRutasPred.isEmpty()){
                        RutaPredefinida randomRoute = filteredRutasPred.get(new Random().nextInt(filteredRutasPred.size()));
                        RutaTiempoReal randTiempoReal = randomRoute.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales);
                        entry.setValue(randTiempoReal);
                    }else{
                        entry.setValue(null);
                    }
            }
        }
    }

    public void mutarHijo(Cromosoma hijo, List<RutaPredefinida> rutasDisponibles) {
        Random rand = new Random();

        if (Math.random() < probabilidadMutacion) {
            //SELECCIONA UN GEN AL AZAR PARA MUTAR DONDE MI CROMOSOMA ES DEL TIPO MAP<PAQUETE, RUTAPREDEFINIDA>

            

            // Selecciona un gen (ruta) al azar para mutar.
            List<RutaTiempoReal> rutas = new ArrayList<>(hijo.getGen().values());
            //RutaPredefinida rutaAMutar = rutas.get(rand.nextInt(rutas.size()));

            // Selecciona una nueva ruta diferente a la actual.
            // RutaPredefinida nuevaRuta;
            // do {
            //     nuevaRuta = rutasDisponibles.get(rand.nextInt(rutasDisponibles.size()));
            // } while (nuevaRuta.equals(rutaAMutar));

            // for (Map.Entry<Paquete, RutaPredefinida> entry : hijo.getGen().entrySet()) {
            //     if (entry.getValue().equals(rutaAMutar)) {
            //         // Actualiza la asignación de la ruta
            //         hijo.getGen().put(entry.getKey(), nuevaRuta);
            //         break; // Romper el bucle después de actualizar el primer paquete encontrado
            //     }
            // }
        }
    }
    

    private static List<Cromosoma> TournamentSelection(List<Cromosoma> poblacion, double ps, int tamanoTorneo, List<Double> fitnessAgregado) {

        List<Cromosoma> seleccionados = new ArrayList<>();
        Random rand = new Random();
            
        // Realizar la selección tantas veces como el tamaño de la población

        for (int i = 0; i < poblacion.size()/2; i++) {
            List<Cromosoma> torneo = new ArrayList<>();
            List<Double> fitnessTorneo = new ArrayList<>();
            
            // Seleccionar aleatoriamente los participantes del torneo
            for (int j = 0; j < tamanoTorneo; j++) {
                int idx = rand.nextInt(poblacion.size());
                torneo.add(poblacion.get(idx));
                fitnessTorneo.add(fitnessAgregado.get(idx));
            }
            
            // Determinar el ganador del torneo
            int indexGanador = 0;
            for (int j = 1; j < torneo.size(); j++) {
                if (rand.nextDouble() < ps) {
                    if (fitnessTorneo.get(j) >= fitnessTorneo.get(indexGanador)) {
                        indexGanador = j;
                        
                    }
                }
            }
            
            seleccionados.add(torneo.get(indexGanador));
            

        }
            
            return seleccionados;
                
        // List<Cromosoma> matingPool = new ArrayList<>();
        // Random rand = new Random();
        // for (int i = 0; i < poblacion.size(); i++) {
        //     List<Cromosoma> torneo = new ArrayList<>();
        //     for (int j = 0; j < tamanoTorneo; j++) {
        //         torneo.add(poblacion.get(rand.nextInt(poblacion.size())));
        //     }
        //     torneo.sort(Comparator.comparing(c -> fitnessAgregado.get(poblacion.indexOf(c))));
        //     matingPool.add(torneo.get(torneo.size() - 1)); // Agregar el de mejor fitness
        // }
        // return matingPool;
    }

    public static List<Cromosoma> crossover(Cromosoma padre1, Cromosoma padre2) {

        Map<Paquete,RutaTiempoReal> genPadre1 = new LinkedHashMap<>(padre1.getGen());
        Map<Paquete,RutaTiempoReal> genPadre2 = new LinkedHashMap<>(padre2.getGen());

        List<Map.Entry<Paquete,RutaTiempoReal>> listaGenPadre1 = new ArrayList<>(genPadre1.entrySet());
        List<Map.Entry<Paquete,RutaTiempoReal>> listaGenPadre2 = new ArrayList<>(genPadre2.entrySet());

        Random random = new Random();
        int puntoCruce = random.nextInt(listaGenPadre1.size());

        for (int i = puntoCruce; i < listaGenPadre1.size(); i++) {
            Map.Entry<Paquete,RutaTiempoReal> temp = listaGenPadre1.get(i);
            listaGenPadre1.set(i, listaGenPadre2.get(i));
            listaGenPadre2.set(i, temp);
        }

        Map<Paquete,RutaTiempoReal> genHijo1 = new LinkedHashMap<>();
        Map<Paquete,RutaTiempoReal> genHijo2 = new LinkedHashMap<>();
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

    public static List<Cromosoma> createPopulation(List<Envio> envios,
            int numCromosomas, List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planesDeVuelo, List<RutaPredefinida> rutasPred,List<Vuelo> vuelosActuales) {
        List<Cromosoma> poblacion = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < numCromosomas; i++) {
            Map<Paquete,RutaTiempoReal> gen = new HashMap<>();
            for (Envio envio : envios) {
                List<Paquete> paquetes = envio.getPaquetes();
                String codigoIATADestinoEnvio = envio.getCodigoIATADestino();
                int j=1;
                for (Paquete paquete : paquetes) {
                    
                    Paquete paqueteactual= new Paquete();
                    paqueteactual.setIdEnvio(paquete.getIdEnvio());
                    paqueteactual.setStatus(1); 
                    String uniqueId = paquete.getIdEnvio() + "-" + j;
                    
                    paqueteactual.setIdEnvio(uniqueId);
                    paqueteactual.setCodigoIATADestino(codigoIATADestinoEnvio);
                    paqueteactual.setEnvio(envio);

                    List<RutaPredefinida> filteredRutasPred = rutasPred.stream().filter(ruta -> ruta.getCodigoIATAOrigen().equals(envio.getCodigoIATAOrigen()) && ruta.getCodigoIATADestino().equals(envio.getCodigoIATADestino()) && ruta.getHoraSalida().getHour() >= envio.getFechaHoraOrigen().getHour() && ruta.getHoraLlegada().getHour() <= envio.getFechaHoraOrigen().getHour()).collect(Collectors.toList());
                    if(!filteredRutasPred.isEmpty()){
                        RutaPredefinida randomRoute = filteredRutasPred.get(new Random().nextInt(filteredRutasPred.size()));
                        RutaTiempoReal randTiempoReal = randomRoute.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales);
                        gen.put(paqueteactual, randTiempoReal);
                    }else{
                        gen.put(paqueteactual, null);
                    }
                    j++;
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
        List<Aeropuerto> destinos = aeropuertos.stream()
                                           .filter(a -> destinosEspecificos.contains(a.getCodigoIATA()))
                                           .collect(Collectors.toList());
        for (Aeropuerto destino  : destinos) {
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

   
}
