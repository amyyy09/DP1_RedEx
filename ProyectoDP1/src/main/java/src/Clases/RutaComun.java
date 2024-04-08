package src.Clases;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetTime;


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
                int ndaysm = 0;
                List<List<PlanDeVuelo>> escalas = generarEscalas(origen, destino, planes, ndaysm);
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

public static List<List<PlanDeVuelo>> generarEscalas(Aeropuerto origen, Aeropuerto destino, List<PlanDeVuelo> planes, int ndaysm) {
    List<List<PlanDeVuelo>> allRoutes = new ArrayList<>();
    List<Integer> daysm = new ArrayList<>();
    List<PlanDeVuelo> currentRoute = new ArrayList<>();
    dfs(origen, destino, currentRoute, allRoutes, planes, daysm);
    return allRoutes;
}

private static void dfs(Aeropuerto current, Aeropuerto destination, List<PlanDeVuelo> currentRoute, List<List<PlanDeVuelo>> allRoutes, List<PlanDeVuelo> planes, List<Integer> daysm) {
    if (current.equals(destination)) {
        allRoutes.add(new ArrayList<>(currentRoute));
        return;
    }
    for (PlanDeVuelo plan : planes) {
        if (plan.getAeropuertoOrigen().equals(current)) {
            OffsetTime arrivalTime = plan.getHoraLlegada();
            OffsetTime departureTime = plan.getHoraSalida();
            boolean sameContinent;
            if(currentRoute.size() > 0){
                sameContinent = currentRoute.get(0).getAeropuertoOrigen().getCiudad().getPais().getContinente().equals(destination.getCiudad().getPais().getContinente());
            }
            else {
                sameContinent = plan.getAeropuertoOrigen().getCiudad().getPais().getContinente().equals(destination.getCiudad().getPais().getContinente());
            }
            if(maxDays == 0 && sameContinent){
                maxDays = 1;
            }
            else if(maxDays == 0 && !sameContinent){
                maxDays = 2;
            }

            if(arrivalTime.isBefore(departureTime)){
                //plus 1 day
                
            }

            if (Duration.between(departureTime, arrivalTime).toMinutes() <= maxTime) {
                currentRoute.add(plan);
                dfs(plan.getAeropuertoDestino(), destination, currentRoute, allRoutes, planes);
                currentRoute.remove(currentRoute.size() - 1);  // Backtrack
            }
        }
    }
}

}
