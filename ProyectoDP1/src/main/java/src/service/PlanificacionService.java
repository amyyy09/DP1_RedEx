package src.service;

import src.dto.*;
import src.repository.EscalasRepository;
import src.repository.PlanDeVueloRepository;
import src.repository.RutaPredefinidaRepository;

import java.io.IOException;
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

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanificacionService {
    
    @Autowired
    private RutaPredefinidaRepository rutaPredefinidaRepository;

    @Autowired
    private PlanDeVueloRepository planDeVueloRepository;

    @Autowired
    private EscalasRepository escalasRepository;
   
    @Transactional
    public void generarYGuardarRutas(List<AeropuertoDTO> aeropuertos, List<PlanDeVueloDTO> planes) {
        List<RutaPredefinidaDTO> rutas = generarRutas(aeropuertos, planes);
        guardarRutas(rutas);
    }

    private void guardarRutas(List<RutaPredefinidaDTO> rutas) {
        for (RutaPredefinidaDTO ruta : rutas) {
            rutaPredefinidaRepository.save(ruta);
            
        }
    }
   
    public List<RutaPredefinidaDTO> generarRutas(List<AeropuertoDTO> aeropuertos, List<PlanDeVueloDTO> planes) {
        List<RutaPredefinidaDTO> rutas = new ArrayList<>();
        AeropuertoDTO origen = aeropuertos.stream()//en este caso solo tenemos de origen ZBAA
            .filter(a -> a.getCodigoIATA().equals("ZBAA"))
            .findFirst()
            .orElse(null);
            if (origen == null) return rutas;
        // List<String> destinosEspecificos = Arrays.asList("SKBO","SEQM","SVMI","SBBR","SPIM","SLLP","SCEL","SABE","SGAS","SUAA","LATI", "EDDI","LOWW","EBCI","UMMS","LBSF","LKPR","LDZA","EKCH","EHAM","VIDP","RKSI", "VTBS","OMDB","ZBAA","RJTT","WMKK","WSSS","WIII","RPLL"); //para nuestro experimento tenemos solo un aeropuerto destino WMKK
        // List<Aeropuerto> destinos = aeropuertos.stream()
        //                                    .filter(a -> destinosEspecificos.contains(a.getCodigoIATA()))
        //                                    .collect(Collectors.toList());
        for (AeropuertoDTO destino  : aeropuertos) {
            if (!origen.equals(destino)) {
                List<Integer> daysm = new ArrayList<>();
                Boolean sameContinent = origen.getContinente().equals(destino.getContinente());
                List<List<PlanDeVueloDTO>> planesRutas = generarEscalas(origen, destino, planes,daysm, sameContinent);                
                for (int i = 0; i < planesRutas.size(); i++) {
                    List<PlanDeVueloDTO> planRuta = planesRutas.get(i);
                    RutaPredefinidaDTO ruta = new RutaPredefinidaDTO(
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

    public static List<List<PlanDeVueloDTO>> generarEscalas(AeropuertoDTO origen, AeropuertoDTO destino, List<PlanDeVueloDTO> planes, List<Integer> daysm, Boolean sameContinent) {
        List<List<PlanDeVueloDTO>> allRoutes = new ArrayList<>();
        List<PlanDeVueloDTO> currentRoute = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        dfs(origen.getCodigoIATA(), destino.getCodigoIATA(), currentRoute, allRoutes, planes, daysm, 0, sameContinent, visited);
        return allRoutes;
    }

    private static void dfs(String current, String destination, List<PlanDeVueloDTO> currentRoute,
                 List<List<PlanDeVueloDTO>> allRoutes, List<PlanDeVueloDTO> planes, List<Integer> daysm,
                 int totalDays, boolean sameContinent, Set<String> visited) {

        if (current.equals(destination)) {
            if (!currentRoute.isEmpty() && !allRoutes.contains(currentRoute)) {
                List<PlanDeVueloDTO> routeToAdd = new ArrayList<>(currentRoute);
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
        List<PlanDeVueloDTO> filteredPlanes = planes.stream()
            .filter(plan -> plan.getCodigoIATAOrigen().equals(current))
            .collect(Collectors.toList());

        for (PlanDeVueloDTO plan : filteredPlanes) {
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