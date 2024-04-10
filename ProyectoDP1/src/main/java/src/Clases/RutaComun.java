package src.Clases;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RutaComun {

    // private int IdRuta;
    private OffsetTime horaSalida;
    private OffsetTime horaLlegada;
    private Aeropuerto aeropuertoOrigen;
    private Aeropuerto aeropuertoDestino;
    private Escala escala;
    private int ndays;

    public static List<RutaComun> generarRutas(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes) {
        List<RutaComun> rutas = new ArrayList<>();
        for (Aeropuerto origen : aeropuertos) {
            for (Aeropuerto destino : aeropuertos) {
                if (!origen.equals(destino)) {
                    List<Integer> daysm = new ArrayList<>();
                    List<List<PlanDeVuelo>> escalas = generarEscalas(origen, destino, planes, daysm);
                    for (List<PlanDeVuelo> escala : escalas) {
                        RutaComun ruta = new RutaComun();
                        ruta.setAeropuertoOrigen(origen);
                        ruta.setAeropuertoDestino(destino);
                        ruta.setEscala(new Escala(ruta, escala));
                        rutas.add(ruta);
                    }
                }
            }
        }
        return rutas;
    }

    public static List<List<PlanDeVuelo>> generarEscalas(Aeropuerto origen, Aeropuerto destino, List<PlanDeVuelo> planes, List<Integer> daysm) {
        List<List<PlanDeVuelo>> allRoutes = new ArrayList<>();
        
        List<PlanDeVuelo> currentRoute = new ArrayList<>();

        dfs(origen, destino, currentRoute, allRoutes, planes, daysm, 0);
        return allRoutes;
    }

    private static void dfs(Aeropuerto current, Aeropuerto destination, List<PlanDeVuelo> currentRoute, List<List<PlanDeVuelo>> allRoutes, List<PlanDeVuelo> planes, List<Integer> daysm, int days) {
        // System.out.println("Current airport: " + current.getNombre());
        // System.out.println("Destination airport: " + destination.getNombre());
        // System.out.println("Current route: " + currentRoute.stream().map(PlanDeVuelo::getAeropuertoOrigen).map(Aeropuerto::getNombre).collect(Collectors.joining(" -> ")));
        if (current.equals(destination)) {
            System.out.println("Destination airport: " + destination.getNombre() + " " + destination.getCiudad().getGMT());
            List<PlanDeVuelo> routeToAdd = new ArrayList<>(currentRoute);
            if (!containsRoute(allRoutes, routeToAdd)) {
                System.out.println("Added route: " + routeToAdd.stream()
                .map(plan -> plan.getAeropuertoOrigen().getNombre() + " (Departure: " + plan.getHoraSalida() + ", Arrival: " + plan.getHoraLlegada() + ")")
                .collect(Collectors.joining(" -> ")));
                System.out.println("Ndays: " + days);
                allRoutes.add(routeToAdd);
                daysm.add(days);
                return;
            }
        }
        for (PlanDeVuelo plan : planes) {
            if (plan.getAeropuertoOrigen().equals(current)) {
                // System.out.println("Found a flight from current airport");
                OffsetTime arrivalTime = plan.getHoraLlegada();
                OffsetTime departureTime = plan.getHoraSalida();
                OffsetTime finalTime=null;
                boolean sameContinent;
                if(currentRoute.size() > 0){
                    sameContinent = currentRoute.get(0).getAeropuertoOrigen().getCiudad().getPais().getContinente().equals(destination.getCiudad().getPais().getContinente());

                    PlanDeVuelo lastPlan = currentRoute.get(currentRoute.size() - 1);
                    finalTime = lastPlan.getHoraLlegada();
                    if (!finalTime.plusMinutes(5).isBefore(departureTime)) {
                        // System.out.println("Next flight's departure time is not at least 5 minutes after the previous flight's arrival time");
                        continue;
                    }
                    if (currentRoute.stream().anyMatch(p -> p.getAeropuertoOrigen().equals(plan.getAeropuertoDestino()))) {
                        // System.out.println("Route is going back to a previously visited airport");
                        continue;
                    }
                }
                else {
                    sameContinent = plan.getAeropuertoOrigen().getCiudad().getPais().getContinente().equals(destination.getCiudad().getPais().getContinente());
                    // finalTime = plan.getHoraLlegada().withOffsetSameLocal(destination.getCiudad().getGMT());
                }

                if(arrivalTime.isBefore(departureTime) || (finalTime != null && departureTime.isBefore(finalTime))){
                    // System.out.println("Flight arrives before it departs (crosses date line)");
                    days++;               
                }

                if(sameContinent){
                    if (!currentRoute.contains(plan)) {
                        if (days <= 1) {
                            if (days ==1 && currentRoute.size()>0 && arrivalTime.isAfter(currentRoute.get(0).getHoraSalida().withOffsetSameLocal(destination.getCiudad().getGMT()))){
                                continue;
                            }
                            currentRoute.add(plan);
                            
                            // System.out.println("Adding flight to current route. Ndays: " + days);
                            
                            dfs(plan.getAeropuertoDestino(), destination, currentRoute, allRoutes, planes, daysm, days);
                            // System.out.println("Removing flight from current route");
                            currentRoute.remove(currentRoute.size() - 1); 
                        }
                        if(arrivalTime.isBefore(departureTime) || (finalTime != null && departureTime.isBefore(finalTime))){
                            days--;               
                        }
                    }
                }

                else{
                    if (!currentRoute.contains(plan)) {
                        if(days <= 2 ){
                           if(days == 2 && (arrivalTime.isAfter(currentRoute.get(0).getHoraSalida().withOffsetSameLocal(destination.getCiudad().getGMT())))){
                                continue;
                           }
                            currentRoute.add(plan);
                            dfs(plan.getAeropuertoDestino(), destination, currentRoute, allRoutes, planes, daysm, days);
                            currentRoute.remove(currentRoute.size() - 1);
                        }
                        
                        if(arrivalTime.isBefore(departureTime) || (finalTime != null && departureTime.isBefore(finalTime))){
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
}
