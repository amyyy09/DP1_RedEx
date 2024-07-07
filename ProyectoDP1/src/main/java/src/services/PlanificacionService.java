package src.services;

import src.model.*;
import src.service.RutaPredefinidaService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class PlanificacionService {

    private static FitnessEvaluatorService evaluator = new FitnessEvaluatorService();

    @Autowired
    public PlanificacionService() {
        PlanificacionService.evaluator = new FitnessEvaluatorService();
    }

    @Autowired
    private RutaPredefinidaService rutaPredefinidaService;

    public List<RutaPredefinida> generarRutas(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes,String pais) {
        List<RutaPredefinida> rutas = new ArrayList<>();
        Aeropuerto origen = aeropuertos.stream()
            .filter(a -> a.getCodigoIATA().equals(pais))
            .findFirst()
            .orElse(null);
            if (origen == null) return rutas;
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
        if (currentRoute.size() > 2 || visited.contains(current)) {
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

    public Map<Paquete, RutaTiempoReal> PSO(List<Envio> envios, List<Paquete> paquetes,
        Map<String, Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo, List<Aeropuerto> aeropuertos,
        List<Vuelo> vuelosActuales, LocalDateTime fechaHoraEjecucion) {

        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPred = rutaPredefinidaService.getRutasPredefinidas(envios);
        List<Particula> population = new ArrayList<>();
        int numParticles = 3;
        int numIterationsMax = 10;
        double w = 0.5, c1 = 1, c2 = 2;

        // Initialize particles
        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(Particula.inicializarPosicion(envios, rutasPred, aeropuertos, vuelosActuales, fechaHoraEjecucion));
            particle.setVelocidad(Particula.inicializarVelocidad(paquetes.size()));
            particle.setPbest(particle.getPosicion());
            particle.setFbest(evaluator.fitness(particle.getPbest(), almacenes, vuelosActuales, false));
            population.add(particle);
        }

        Map<Paquete, RutaTiempoReal> gbest = Particula.determineGbest(population, almacenes, vuelosActuales);
        int noImprovementCounter = 0;
        int j = 0;

        while (noImprovementCounter < numIterationsMax && j < 10) {
            for (Particula particle : population) {
                for (int k = 0; k < envios.size(); k++) {
                    List<RutaPredefinida> filteredRutasPred = filterRutasForEnvio(rutasPred, envios.get(k)); // todas las rutas que sirvan para ese envio
                    for (Paquete paquete : envios.get(k).getPaquetes()) {
                        RutaTiempoReal currentRTR = particle.getPosicion().get(paquete);
                        if (currentRTR == null) {
                            continue;
                        }
                        int indexPos = filteredRutasPred.indexOf(currentRTR.getRutaPredefinida());
                        double r1 = Math.random(), r2 = Math.random();

                        double velocity = w * particle.getVelocidad().get(k) +
                                c1 * r1 * (indexPos - filteredRutasPred.indexOf(particle.getPbest().get(paquete).getRutaPredefinida())) +
                                c2 * r2 * (indexPos - filteredRutasPred.indexOf(gbest.get(paquete).getRutaPredefinida()));

                        particle.getVelocidad().set(k, velocity);

                        double newPosIndex = indexPos + velocity;
                        int posIndex = Particula.verifyLimits(newPosIndex, filteredRutasPred);

                        RutaPredefinida newPosition = filteredRutasPred.get(posIndex);
                        RutaTiempoReal newRTR = newPosition.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales, fechaHoraEjecucion);
                        particle.getPosicion().put(paquete, newRTR);
                    }
                }

                double fit = evaluator.fitness(particle.getPosicion(), almacenes, vuelosActuales, false);
                if (fit < particle.getFbest()) {
                    particle.setPbest(particle.getPosicion());
                    particle.setFbest(fit);
                }
            }

            Map<Paquete, RutaTiempoReal> currentGbest = Particula.determineGbest(population, almacenes, vuelosActuales);
            if (evaluator.fitness(currentGbest, almacenes, vuelosActuales, false) > evaluator.fitness(gbest, almacenes, vuelosActuales, false)) {
                gbest = currentGbest;
                noImprovementCounter = 0;
            } else {
                noImprovementCounter++;
            }
            j++;
        }
        return gbest;
    }

    public Map<Paquete, RutaTiempoReal> PSODiario(List<Envio> envios, List<Paquete> paquetes,
        Map<String, Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo, List<Aeropuerto> aeropuertos,
        List<Vuelo> vuelosActuales, LocalDateTime fechaHoraEjecucion) {

        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPred = rutaPredefinidaService.getRutasPredefinidas(envios);
        List<Particula> population = new ArrayList<>();
        int numParticles = 10;
        int numIterationsMax = 100;
        double w = 0.5, c1 = 1, c2 = 2;

        // Initialize particles
        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(Particula.inicializarPosicion(envios, rutasPred, aeropuertos, vuelosActuales, fechaHoraEjecucion));
            particle.setVelocidad(Particula.inicializarVelocidad(paquetes.size()));
            particle.setPbest(particle.getPosicion());
            particle.setFbest(evaluator.fitness(particle.getPbest(), almacenes, vuelosActuales, false));
            population.add(particle);
        }

        Map<Paquete, RutaTiempoReal> gbest = Particula.determineGbest(population, almacenes, vuelosActuales);
        int noImprovementCounter = 0;
        int j = 0;

        while (noImprovementCounter < numIterationsMax && j < 250) {
            for (Particula particle : population) {
                for (int k = 0; k < envios.size(); k++) {
                    List<RutaPredefinida> filteredRutasPred = filterRutasForEnvio(rutasPred, envios.get(k)); // todas las rutas que sirvan para ese envio
                    for (Paquete paquete : envios.get(k).getPaquetes()) {
                        RutaTiempoReal currentRTR = particle.getPosicion().get(paquete);
                        if (currentRTR == null) {
                            continue;
                        }
                        int indexPos = filteredRutasPred.indexOf(currentRTR.getRutaPredefinida());
                        double r1 = Math.random(), r2 = Math.random();

                        double velocity = w * particle.getVelocidad().get(k) +
                                c1 * r1 * (indexPos - filteredRutasPred.indexOf(particle.getPbest().get(paquete).getRutaPredefinida())) +
                                c2 * r2 * (indexPos - filteredRutasPred.indexOf(gbest.get(paquete).getRutaPredefinida()));

                        particle.getVelocidad().set(k, velocity);

                        double newPosIndex = indexPos + velocity;
                        int posIndex = Particula.verifyLimits(newPosIndex, filteredRutasPred);

                        RutaPredefinida newPosition = filteredRutasPred.get(posIndex);
                        RutaTiempoReal newRTR = newPosition.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActuales, fechaHoraEjecucion);
                        particle.getPosicion().put(paquete, newRTR);
                    }
                }

                double fit = evaluator.fitness(particle.getPosicion(), almacenes, vuelosActuales, false);
                if (fit < particle.getFbest()) {
                    particle.setPbest(particle.getPosicion());
                    particle.setFbest(fit);
                }
            }

            Map<Paquete, RutaTiempoReal> currentGbest = Particula.determineGbest(population, almacenes, vuelosActuales);
            if (evaluator.fitness(currentGbest, almacenes, vuelosActuales, false) > evaluator.fitness(gbest, almacenes, vuelosActuales, false)) {
                gbest = currentGbest;
                noImprovementCounter = 0;
            } else {
                noImprovementCounter++;
            }
            j++;
        }
        return gbest;
    }



    public static List<RutaPredefinida> filterRutasForEnvio(
        Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPred, Envio envio) {

        String codigoIATAOrigen = envio.getCodigoIATAOrigen();
        String codigoIATADestino = envio.getCodigoIATADestino();
        int horaLlegada = envio.getFechaHoraOrigen().getHour() * 100 + envio.getFechaHoraOrigen().getMinute();

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

    public Map<Paquete, Resultado> transformResult(Map<Paquete, RutaTiempoReal> originalResult) {
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

    
    public List<VueloNuevo> transformarResultados(Map<Paquete, Resultado> resultados, List<PlanDeVuelo> planesDeVuelo) {
        Map<String, VueloNuevo> vuelosNuevosMap = new HashMap<>();

        // Mapear indexPlan a PlanDeVuelo para facilitar la búsqueda
        Map<Integer, PlanDeVuelo> planesDeVueloMap = new HashMap<>();
        for (PlanDeVuelo plan : planesDeVuelo) {
            planesDeVueloMap.put(plan.getIndexPlan(), plan);
        }

        for (Map.Entry<Paquete, Resultado> entry : resultados.entrySet()) {
            Resultado resultado = entry.getValue();
            
            // Verificar si resultado es nulo
            if (resultado == null || resultado.getVuelos() == null) {
                continue; // Saltar iteración si resultado o vuelos es nulo
            }

            for (Vuelo vuelo : resultado.getVuelos()) {
                String idVuelo = vuelo.getIdVuelo();
                
                if (!vuelosNuevosMap.containsKey(idVuelo)) {
                    PlanDeVuelo planDeVuelo = planesDeVueloMap.get(vuelo.getIndexPlan());

                    // Verificar si planDeVuelo es nulo
                    if (planDeVuelo == null) {
                        continue; // Saltar iteración si planDeVuelo es nulo
                    }

                    VueloNuevo vueloNuevo = new VueloNuevo();
                    vueloNuevo.setIdVuelo(idVuelo);
                    vueloNuevo.setCantPaquetes(1);
                    vueloNuevo.setCapacidad(vuelo.getCapacidad());
                    vueloNuevo.setStatus(vuelo.getStatus());
                    vueloNuevo.setIndexPlan(vuelo.getIndexPlan());
                    vueloNuevo.setHoraSalida(vuelo.getHoraSalida());
                    vueloNuevo.setHoraLlegada(vuelo.getHoraLlegada());
                    vueloNuevo.setAeropuertoOrigen(planDeVuelo.getCodigoIATAOrigen());
                    vueloNuevo.setAeropuertoDestino(planDeVuelo.getCodigoIATADestino());

                    vuelosNuevosMap.put(idVuelo, vueloNuevo);
                } else {
                    VueloNuevo vueloExistente = vuelosNuevosMap.get(idVuelo);
                    vueloExistente.setCantPaquetes(vueloExistente.getCantPaquetes() + 1);
                }
            }
        }

        return new ArrayList<>(vuelosNuevosMap.values());
    }

    public List<Vuelo> transformarResultadosDiario(Map<Paquete, Resultado> resultados, List<PlanDeVuelo> planesDeVuelo) {
        Map<String, Vuelo> vuelosNuevosMap = new HashMap<>();

        // Mapear indexPlan a PlanDeVuelo para facilitar la búsqueda
        Map<Integer, PlanDeVuelo> planesDeVueloMap = new HashMap<>();
        for (PlanDeVuelo plan : planesDeVuelo) {
            planesDeVueloMap.put(plan.getIndexPlan(), plan);
        }

        for (Map.Entry<Paquete, Resultado> entry : resultados.entrySet()) {
            Paquete paquete = entry.getKey();
            Resultado resultado = entry.getValue();
            
            // Verificar si resultado es nulo
            if (resultado == null || resultado.getVuelos() == null) {
                continue; // Saltar iteración si resultado o vuelos es nulo
            }

            String rutaFinal = resultado.getVuelos().stream()
                        .map(vuelo -> String.valueOf(vuelo.getIndexPlan()))
                        .collect(Collectors.joining(";"));
            
            paquete.setRuta(rutaFinal);

            for (Vuelo vuelo : resultado.getVuelos()) {
                String idVuelo = vuelo.getIdVuelo();
                
                if (!vuelosNuevosMap.containsKey(idVuelo)) {
                    PlanDeVuelo planDeVuelo = planesDeVueloMap.get(vuelo.getIndexPlan());
                    if (planDeVuelo == null) {
                        continue;
                    }

                    Vuelo vueloNuevo = new Vuelo();
                    vueloNuevo.setIdVuelo(idVuelo);
                    vueloNuevo.setCantPaquetes(1);
                    vueloNuevo.setCapacidad(vuelo.getCapacidad());
                    vueloNuevo.setStatus(vuelo.getStatus());
                    vueloNuevo.setIndexPlan(vuelo.getIndexPlan());
                    vueloNuevo.setHoraSalida(vuelo.getHoraSalida());
                    vueloNuevo.setHoraLlegada(vuelo.getHoraLlegada());
                    vueloNuevo.setAeropuertoOrigen(planDeVuelo.getCodigoIATAOrigen());
                    vueloNuevo.setAeropuertoDestino(planDeVuelo.getCodigoIATADestino());
                    vueloNuevo.setPaquetes(new ArrayList<>());
                    vueloNuevo.getPaquetes().add(paquete);

                    vuelosNuevosMap.put(idVuelo, vueloNuevo);
                } else {
                    Vuelo vueloExistente = vuelosNuevosMap.get(idVuelo);
                    vueloExistente.setCantPaquetes(vueloExistente.getCantPaquetes() + 1);
                    vueloExistente.getPaquetes().add(paquete);
                }
            }
        }

        return new ArrayList<>(vuelosNuevosMap.values());
    }

    public static Resumen generarResumen(Map<Paquete, Resultado> resultado, List<PlanDeVuelo> planesDeVuelo) {
        // Verificar si resultado es null
        if (resultado == null) {
            return null;
        }
        
        List<Vuelo> todosVuelos = resultado.values().stream()
                .flatMap(res -> {
                    if (res == null || res.getVuelos() == null) {
                        return null;
                    }
                    return res.getVuelos().stream();
                })
                .collect(Collectors.toList());
    
        int totalPaquetes = resultado.size(); // Cantidad de entradas en el mapa
    
        // Verificar si planesDeVuelo es null
        if (planesDeVuelo == null) {
            throw new IllegalArgumentException("La lista de planes de vuelo no puede ser null");
        }
    
        Map<Integer, PlanDeVuelo> planesMap = planesDeVuelo.stream()
                .collect(Collectors.toMap(PlanDeVuelo::getIndexPlan, Function.identity()));
    
        Map<String, Long> frecuenciaDestino = todosVuelos.stream()
                .map(vuelo -> {
                    PlanDeVuelo plan = planesMap.get(vuelo.getIndexPlan());
                    if (plan == null) {
                        throw new IllegalArgumentException("No se encontró el plan de vuelo para el índice " + vuelo.getIndexPlan());
                    }
                    return plan.getCodigoIATADestino();
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    
        String aeropuertoDestinoMasFrecuente = frecuenciaDestino.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    
        Map<Integer, Long> vuelosPorHora = todosVuelos.stream()
                .collect(Collectors.groupingBy(
                        vuelo -> {
                            if (vuelo.getHoraSalida() == null) {
                                throw new IllegalArgumentException("La hora de salida del vuelo no puede ser null");
                            }
                            return vuelo.getHoraSalida().getHour();
                        },
                        Collectors.counting()
                ));
    
        // Encontrar la hora con más vuelos
        int horaConMasVuelos = vuelosPorHora.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
        
        double promedioPaquetesPorVuelo = Math.ceil((double) totalPaquetes / ((double) todosVuelos.size() / 2));
    
        double tiempoPromedioVuelo = todosVuelos.stream()
                .mapToLong(vuelo -> {
                    if (vuelo.getHoraSalida() == null || vuelo.getHoraLlegada() == null) {
                        throw new IllegalArgumentException("La hora de salida o llegada del vuelo no puede ser null");
                    }
                    return Duration.between(vuelo.getHoraSalida(), vuelo.getHoraLlegada()).toMinutes();
                })
                .average()
                .orElse(0);
    
        Resumen resumen = new Resumen();
        resumen.setNumeroVuelos(todosVuelos.size());
        resumen.setTotalPaquetes(totalPaquetes);
        resumen.setAeropuertoMasFrecuente(aeropuertoDestinoMasFrecuente);
        resumen.setHoraConMasVuelos(horaConMasVuelos);
        resumen.setPromedioPaquetesPorVuelo(promedioPaquetesPorVuelo);
        resumen.setTiempoPromedioVuelo(tiempoPromedioVuelo);
    
        return resumen;
    }

}