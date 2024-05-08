package src.service;

import src.model.*;

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
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanificacionService {
    private double probabilidadSeleccion = 0.2;
    private double probabilidadMutacion = 0.1;
    private double probabilidadCruce = 0.85;
    private int numCromosomas = 70;
    private int tamanoTorneo = 5;
    private int numDescendientes = 20;
    private int numGeneraciones = 40;

    private static FitnessEvaluatorService evaluator = new FitnessEvaluatorService();
    private static Random rand = new Random();

    @Autowired
    public PlanificacionService() {
        rand = new Random();
        PlanificacionService.evaluator = new FitnessEvaluatorService();
    }

    public Cromosoma ejecutarAlgoritmoGenetico(List<Envio> envios, List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planesDeVuelo,List<RutaPredefinida> rutasPred, Map<String, Almacen> almacenes,List<Vuelo> vuelosActuales) throws IOException {

        
        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredMap = createMap(rutasPred);
        List<Cromosoma> poblacion = createPopulation(envios, numCromosomas, aeropuertos,planesDeVuelo,rutasPredMap,vuelosActuales); //LISTO NO TOCAR
        System.out.println();
        Random rand = new Random();
        double fitnesfinal=0;
        Cromosoma cromosomafinal = new Cromosoma();
        for (int generacion = 0; generacion < numGeneraciones; generacion++) {
            //System.out.println("Generación " + generacion);
            List<Double> fitnessAgregado = evaluator.calcularFitnessAgregado(poblacion, aeropuertos,vuelosActuales,almacenes, false);//FALTA Revisar porque todos los fitness salen iguales 
            
            evaluator.ordernarPoblacion(poblacion, fitnessAgregado);//LISTO NO TOCAR

            if (!fitnessAgregado.isEmpty() && fitnessAgregado.get(0) >= 0) {
                System.out.println(fitnessAgregado.get(0));
                List<Cromosoma> cromofinalfinal= new ArrayList<>();
                cromosomafinal=poblacion.get(0);
                cromofinalfinal.add(cromosomafinal);
                List<Double> fitnessAgregadofinal= evaluator.calcularFitnessAgregado(cromofinalfinal, aeropuertos,vuelosActuales,almacenes, true);
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
                            mutarHijo(hijo, probabilidadMutacion, rand, rutasPredMap,aeropuertos,vuelosActuales);
                        }
                    }
                    descendientes.addAll(hijos);
                }
            }
            List<Double> fitnessAgregadoDesc = evaluator.calcularFitnessAgregado(descendientes, aeropuertos,vuelosActuales,almacenes,false);


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
            //System.out.println(fitnessAgregado.get(0));
            cromosomafinal=poblacion.get(0);
        }
        
        List<Cromosoma> cromofinal= new ArrayList<>();
        cromofinal.add(cromosomafinal);
        List<Double> fitnessAgregado = evaluator.calcularFitnessAgregado(cromofinal, aeropuertos,vuelosActuales,almacenes, true);
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

    public void mutarHijo(Cromosoma cromosoma, double probabilidadMutacion, Random rand, Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPred ,List<Aeropuerto> aeropuertos,List<Vuelo> vuelosActuales) {
        // Iterar sobre cada gen en el cromosoma
        for (Map.Entry<Paquete, RutaTiempoReal> entry : cromosoma.getGen().entrySet()) {
            if (rand.nextDouble() < probabilidadMutacion) {
                // Seleccionar un nuevo plan de vuelo al azar para mutar este gen
                Envio envio = entry.getKey().getEnvio();
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

                if (filteredRutasPred.isEmpty()) {
                    System.err.println("ayuda, revisar el filtrado de rutas predefinidas");
                
                    if(!filteredRutasPred.isEmpty()){
                        RutaPredefinida randomRoute = filteredRutasPred.get(rand.nextInt(filteredRutasPred.size()));
                        RutaTiempoReal randTiempoReal = randomRoute.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales);
                        entry.setValue(randTiempoReal);
                    }else{
                        entry.setValue(null);
                    }
                }
            }
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

    // public static List<Cromosoma> createPopulation(List<Envio> envios,
    //         int numCromosomas, List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planesDeVuelo, List<RutaPredefinida> rutasPred,List<Vuelo> vuelosActuales) {
    //     List<Cromosoma> poblacion = new ArrayList<>();
    //     Random random = new Random();
        
    //     for (int i = 0; i < numCromosomas; i++) {
    //         Map<Paquete,RutaTiempoReal> gen = new HashMap<>();
    //         for (Envio envio : envios) {
    //             List<Paquete> paquetes = envio.getPaquetes();
    //             String codigoIATADestinoEnvio = envio.getCodigoIATADestino();
    //             int j=1;
    //             for (Paquete paquete : paquetes) {
                    
    //                 Paquete paqueteactual= new Paquete();
    //                 paqueteactual.setIdEnvio(paquete.getIdEnvio());
    //                 paqueteactual.setStatus(1); 
    //                 String uniqueId = paquete.getIdEnvio() + "-" + j;
                    
    //                 paqueteactual.setIdEnvio(uniqueId);
    //                 paqueteactual.setCodigoIATADestino(codigoIATADestinoEnvio);
    //                 paqueteactual.setEnvio(envio);

    //                 List<RutaPredefinida> filteredRutasPred = rutasPred.stream().filter(ruta -> ruta.getCodigoIATAOrigen().equals(envio.getCodigoIATAOrigen()) && ruta.getCodigoIATADestino().equals(envio.getCodigoIATADestino()) && ruta.getHoraSalida().getHour() >= envio.getFechaHoraOrigen().getHour() && ruta.getHoraLlegada().getHour() <= envio.getFechaHoraOrigen().getHour()).collect(Collectors.toList());
    //                 if(!filteredRutasPred.isEmpty()){
    //                     RutaPredefinida randomRoute = filteredRutasPred.get(new Random().nextInt(filteredRutasPred.size()));
    //                     RutaTiempoReal randTiempoReal = randomRoute.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales);
    //                     gen.put(paqueteactual, randTiempoReal);
    //                 }else{
    //                     gen.put(paqueteactual, null);
    //                 }
    //                 j++;
    //             }
    //         }
    //         Cromosoma cromosoma = new Cromosoma(gen);
    //         poblacion.add(cromosoma);
    //     }

    //     return poblacion;
    // }
    // public static List<Cromosoma> createPopulation(List<Envio> envios,
    //                                                int numCromosomas, List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planesDeVuelo,
    //                                                List<RutaPredefinida> rutasPred, List<Vuelo> vuelosActuales) {
    //     List<Cromosoma> poblacion = new ArrayList<>();
    //     Random random = new Random();

    //     // Crear un mapa para acceso rápido a rutas por origen y destino
    //     Map<String, List<RutaPredefinida>> rutaCache = rutasPred.stream()
    //             .collect(Collectors.groupingBy(r -> r.getCodigoIATAOrigen() + "-" + r.getCodigoIATADestino()));

    //     // Generar población
    //     envios.parallelStream().forEach(envio -> {
    //         Map<Paquete, RutaPredefinida> gen = new HashMap<>();
    //         List<RutaPredefinida> filteredRutasPred = rutaCache.getOrDefault(envio.getCodigoIATAOrigen() + "-" + envio.getCodigoIATADestino(), Collections.emptyList());

    //         for (Paquete paquete : envio.getPaquetes()) {
    //             RutaPredefinida randomRoute = !filteredRutasPred.isEmpty()
    //                     ? filteredRutasPred.get(random.nextInt(filteredRutasPred.size()))
    //                     : null;
    //             gen.put(paquete, randomRoute);
    //         }
    //         synchronized (poblacion) {
                
    //             poblacion.add(new Cromosoma(gen));
    //         }
    //     });

    //     return poblacion;
    // }
    ///////////////////////

    public static List<Cromosoma> createPopulation(List<Envio> envios,
            int numCromosomas, List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planesDeVuelo, Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPred, List<Vuelo> vuelosActuales) {
        
        List<Cromosoma> poblacion = new ArrayList<>();
        Random random = new Random();
        
        
        for (int i = 0; i < numCromosomas; i++) {
            Map<Paquete, RutaTiempoReal> gen = new HashMap<>();
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

                if (filteredRutasPred.isEmpty()) {
                    System.err.println("ayuda, revisar el filtrado de rutas predefinidas");
                } 

                List<Paquete> paquetes = envio.getPaquetes();
                for (int j = 1; j <= paquetes.size(); j++) {

                    Paquete paqueteactual = clonePaquete(paquetes.get(j - 1), envio.getCodigoIATADestino(), j, envio);

                    if (!filteredRutasPred.isEmpty()) {
                        int index = new Random().nextInt(filteredRutasPred.size());
                        RutaPredefinida randomRoute = filteredRutasPred.get(index);
                        RutaTiempoReal randTiempoReal = randomRoute.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales);
                        gen.put(paqueteactual, randTiempoReal);
                    } else {
                        gen.put(paqueteactual, null);
                    }
                }
            }
            poblacion.add(new Cromosoma(gen));
        }

        return poblacion;
    }

    private static Paquete clonePaquete(Paquete original, String destinoIATA, int secuencia, Envio envio) {
        Paquete nuevo = new Paquete();
        nuevo.setIdEnvio(original.getIdEnvio() + "-" + secuencia);
        nuevo.setStatus(1);
        nuevo.setCodigoIATADestino(destinoIATA);
        nuevo.setEnvio(envio);
        return nuevo;
    }

    /////////////////////

    public List<RutaPredefinida> generarRutas(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes) {
        List<RutaPredefinida> rutas = new ArrayList<>();
        Aeropuerto origen = aeropuertos.stream()//en este caso solo tenemos de origen ZBAA
            .filter(a -> a.getCodigoIATA().equals("ZBAA"))
            .findFirst()
            .orElse(null);
            if (origen == null) return rutas;
        // List<String> destinosEspecificos = Arrays.asList("SKBO","SEQM","SVMI","SBBR","SPIM","SLLP","SCEL","SABE","SGAS","SUAA","LATI", "EDDI","LOWW","EBCI","UMMS","LBSF","LKPR","LDZA","EKCH","EHAM","VIDP","RKSI", "VTBS","OMDB","ZBAA","RJTT","WMKK","WSSS","WIII","RPLL"); //para nuestro experimento tenemos solo un aeropuerto destino WMKK
        // List<Aeropuerto> destinos = aeropuertos.stream()
        //                                    .filter(a -> destinosEspecificos.contains(a.getCodigoIATA()))
        //                                    .collect(Collectors.toList());
        for (Aeropuerto destino  : aeropuertos) {
            if (!origen.equals(destino)) {
                List<Integer> daysm = new ArrayList<>();
                Boolean sameContinent = origen.getContinente().equals(destino.getContinente());
                List<List<PlanDeVuelo>> planesRutas = generarEscalas(origen, destino, planes,daysm, sameContinent);                
                for (int i = 0; i < planesRutas.size(); i++) {
                    List<PlanDeVuelo> planRuta = planesRutas.get(i);
                    RutaPredefinida ruta = new RutaPredefinida(
                        origen.getCodigoIATA(),
                        destino.getCodigoIATA(),
                        planRuta.get(0).getHoraSalida(),
                        planRuta.get(planRuta.size() - 1).getHoraLlegada(),
                        planRuta,
                        daysm.get(i), // get the corresponding value from the daysm array
                        sameContinent
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

    public static List<RutaPredefinida> filterRutasForEnvio(
        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPred, Envio envio) {

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

        // Map<RutaPredefinida, Integer> filteredRutasPredMap = new HashMap<>();
        // for (int i = 0; i < filteredRutasPred.size(); i++) {
        //     filteredRutasPredMap.put(filteredRutasPred.get(i), i);
        // }

        return filteredRutasPred;
    }

    public static Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> createMap(List<RutaPredefinida> rutasPred) {
        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredMap = new HashMap<>();
        for (RutaPredefinida ruta : rutasPred) {
            String origen = ruta.getCodigoIATAOrigen();
            String destino = ruta.getCodigoIATADestino();
            int horaSalida = ruta.getHoraSalida().getHour()*100 + ruta.getHoraSalida().getMinute();
            int horaLlegada = ruta.getHoraLlegada().getHour()*100 + ruta.getHoraLlegada().getMinute();

            Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>> origenMap = rutasPredMap.getOrDefault(origen, new HashMap<>());
            TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>> destinoMap = origenMap.getOrDefault(destino, new TreeMap<>());
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
