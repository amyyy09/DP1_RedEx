package src.services;

import src.model.*;
import src.service.AeropuertoService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.concurrent.RecursiveTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.ConcurrentHashMap;

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

    public Map<Paquete, RutaTiempoReal> PSO(List<Envio> envios, List<Paquete> paquetes, List<RutaPredefinida> rutasPred,
            Map<String, Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActuales, LocalDateTime fechaHoraEjecucion) {
        List<Particula> population = new ArrayList<>();
        int numParticles = 8;
        int numIterationsMax = 30;
        double w = 0.5, c1 = 1, c2 = 2;

        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredMap = createMap(rutasPred);
        
        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(Particula.inicializarPosicion(envios, rutasPredMap, aeropuertos, vuelosActuales, fechaHoraEjecucion));
            particle.setVelocidad(Particula.inicializarVelocidad(paquetes.size()));
            particle.setPbest(particle.getPosicion());
            particle.setFbest(evaluator.fitness(particle.getPbest(), almacenes, vuelosActuales, false));
            population.add(particle);
        }
        Map<Paquete, RutaTiempoReal> gbest = Particula.determineGbest(population, almacenes, vuelosActuales);
        int noImprovementCounter = 0;
        // for (int j = 0; j < numIterationsMax; j++) {
        int j=0;
        while (noImprovementCounter < numIterationsMax && j < 350) {
            // if(evaluator.fitness(gbest, aeropuertos, vuelosActuales) == 0){
            //     return gbest;
            // }
            for (Particula particle : population) {
                for (int k = 0; k < envios.size(); k++) {
                    List<RutaPredefinida> filteredRutasPred = filterRutasForEnvio(rutasPredMap, envios.get(k));
                    for (int l = 0; l< envios.get(k).getPaquetes().size(); l++) {
                        // Update velocity and position for each package (paquete) in the envio
                        double r1 = rand.nextDouble(), r2 = rand.nextDouble();
                        Paquete paquete = envios.get(k).getPaquetes().get(l);
                        int indexPos=0;
                        int posIndex=0;
                        try{
                        if(particle.getPosicion().get(paquete)==null){
                            continue;
                        }
                        indexPos = filteredRutasPred.indexOf(particle.getPosicion().get(paquete).getRutaPredefinida());
                        double velocity=0;
                        try{
                        velocity = w * particle.getVelocidad().get(k) +
                            c1 * r1 * (indexPos
                                - filteredRutasPred.indexOf(particle.getPbest().get(paquete).getRutaPredefinida()))
                            +
                            c2 * r2 * (indexPos
                                - filteredRutasPred.indexOf(gbest.get(paquete).getRutaPredefinida()));
                        }catch(Exception e){
                            System.err.println(k);
                            e.printStackTrace();
                        }

                        particle.getVelocidad().set(k, velocity);
                                    
                        double newPosIndex = indexPos + velocity;

                        posIndex = Particula.verifyLimits(newPosIndex, filteredRutasPred);

                        RutaPredefinida newPosition = rutasPred.get(posIndex);

                        RutaTiempoReal newRTR = newPosition.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales, fechaHoraEjecucion);

                        particle.getPosicion().put(paquete, newRTR);
                        } catch(Exception e){
                            System.err.println(posIndex);
                            e.printStackTrace();
                        }
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
            if (evaluator.fitness(currentGbest, almacenes, vuelosActuales, false) > evaluator.fitness(gbest, almacenes, vuelosActuales, false)) {
                gbest = currentGbest;
                noImprovementCounter = 0;  // reset the counter when there's an improvement
            } else {
                // Double fit = evaluator.fitness(gbest, almacenes, vuelosActuales, false);
                noImprovementCounter++;  // increment the counter when there's no improvement
            }
            j++;
        }
        // double fit = evaluator.fitness(gbest, almacenes, vuelosActuales, true);
        return gbest;
        // return null;
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

    public static Map<Paquete, Resultado> transformResult(Map<Paquete, RutaTiempoReal> originalResult) {
        Map<Paquete, Resultado> transformedResult = new HashMap<>();
    
        for (Map.Entry<Paquete, RutaTiempoReal> entry : originalResult.entrySet()) {
            RutaTiempoReal ruta = entry.getValue();
            if (ruta == null) {
                transformedResult.put(entry.getKey(), null);
                continue;
            }
            
            Resultado resultado = new Resultado();
            resultado.setIdRuta(ruta.getIdRuta());
    
            if (ruta.getOrigen() != null) {
                String origen = ruta.getOrigen().getCodigoIATA();
                resultado.setAeropuertoOrigen(origen);
            }
    
            if (ruta.getDestino() != null) {
                String destino = ruta.getDestino().getCodigoIATA();
                resultado.setAeropuertoDestino(destino);
            }
    
            resultado.setHoraInicio(ruta.getHoraInicio());
            resultado.setHoraLlegada(ruta.getHoraLlegada());
    
            if (ruta.getVuelos() != null) {
                List<Vuelo> vuelosSimples = new ArrayList<>();
                for (Vuelo vuelo : ruta.getVuelos()) {
                    Vuelo vueloSimple = new Vuelo();
                    vueloSimple.setIdVuelo(vuelo.getIdVuelo());
                    vueloSimple.setCantPaquetes(vuelo.getCantPaquetes());
                    vueloSimple.setCapacidad(vuelo.getCapacidad());
                    vueloSimple.setStatus(vuelo.getStatus());
                    vueloSimple.setIndexPlan(vuelo.getIndexPlan());
                    vueloSimple.setHoraSalida(vuelo.getHoraSalida());
                    vueloSimple.setHoraLlegada(vuelo.getHoraLlegada());
                    vuelosSimples.add(vueloSimple);
                }
                resultado.setVuelos(vuelosSimples);
            }
    
            resultado.setStatus(ruta.getStatus());
    
            if (ruta.getXAlmacen() != null) {
                int almacenSimple = ruta.getXAlmacen().getCantPaquetes();
                resultado.setCapacidadAlmacen(almacenSimple);
            }
    
            transformedResult.put(entry.getKey(), resultado);
        }
    
        return transformedResult;
    }

    public static List<VueloNuevo> transformarResultado(Map<Paquete, Resultado> json) {
        List<VueloNuevo> acumuladoVuelos = new ArrayList<>();

        for (Map.Entry<Paquete, Resultado> entry : json.entrySet()) {
            Resultado resultado = entry.getValue();

            if (resultado == null) {
                continue; 
            }

            String aeropuertoOrigen = resultado.getAeropuertoOrigen();
            String aeropuertoDestino = resultado.getAeropuertoDestino();

            if (resultado.getVuelos() == null) {
                continue; 
            }

            for (Vuelo vuelo : resultado.getVuelos()) {
                VueloNuevo vueloNuevo = new VueloNuevo();
                vueloNuevo.setCantPaquetes(vuelo.getCantPaquetes());
                vueloNuevo.setCapacidad(vuelo.getCapacidad());
                vueloNuevo.setStatus(vuelo.getStatus());
                vueloNuevo.setIndexPlan(vuelo.getIndexPlan());
                vueloNuevo.setHoraSalida(vuelo.getHoraSalida());
                vueloNuevo.setHoraLlegada(vuelo.getHoraLlegada());
                vueloNuevo.setAeropuertoOrigen(aeropuertoOrigen);
                vueloNuevo.setAeropuertoDestino(aeropuertoDestino);
                vueloNuevo.setIdVuelo(vuelo.getIdVuelo());

                acumuladoVuelos.add(vueloNuevo);
            }
        }

        return acumuladoVuelos;
    }

}
