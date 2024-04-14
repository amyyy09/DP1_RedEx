package src.Clases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RutaComun {

    private OffsetTime horaSalida;
    private OffsetTime horaLlegada;
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private List<PlanDeVuelo> planRuta;
    private int ndays;

    public static void guardarRutasEnCSV(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes,
            String archivoDestino) {
        List<RutaComun> rutas = generarRutas(aeropuertos, planes);
        List<String> lineas = new ArrayList<>();
        lineas.add("CodigoIATAOrigen,CodigoIATADestino,HoraSalida,HoraLlegada,NDays,PlanesDeVuelo");
        for (RutaComun ruta : rutas) {
            String detallesRuta = String.format("%s,%s,%s,%s,%d",
                    ruta.getCodigoIATAOrigen(),
                    ruta.getCodigoIATADestino(),
                    ruta.getHoraSalida(),
                    ruta.getHoraLlegada(),
                    ruta.getNdays());
            String detallesPlanes = ruta.getPlanRuta().stream()
                    .map(plan -> String.format("%s,%s,%s,%s,%d",
                            plan.getCodigoIATAOrigen(),
                            plan.getCodigoIATADestino(),
                            plan.getHoraSalida(),
                            plan.getHoraLlegada(),
                            plan.getCapacidad()))
                    .collect(Collectors.joining("|")); // Separador entre planes de vuelo
            lineas.add(detallesRuta + "," + detallesPlanes);
        }

        try {
            Files.write(Paths.get(archivoDestino), lineas);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static List<RutaComun> leerRutasDesdeCSV(String archivoRutas) {
        List<RutaComun> rutas = new ArrayList<>();
        try (Stream<String> lineas = Files.lines(Paths.get(archivoRutas))) {
            lineas.skip(1) // Saltar el encabezado
                    .forEach(linea -> {
                        String[] partes = linea.split(",");
                        String origen = partes[0];
                        String destino = partes[1];
                        OffsetTime salida = OffsetTime.parse(partes[2]);
                        OffsetTime llegada = OffsetTime.parse(partes[3]);
                        int dias = Integer.parseInt(partes[4]);

                        RutaComun ruta = new RutaComun(salida, llegada, origen, destino, null, dias);
                        rutas.add(ruta);
                    });
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return rutas;
    }

    public static List<RutaComun> generarRutas(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes) {
        List<RutaComun> rutas = new ArrayList<>();
        for (Aeropuerto origen : aeropuertos) {
            for (Aeropuerto destino : aeropuertos) {
                if (!origen.equals(destino) && origen.getContinente()
                        .equals(destino.getContinente())) {
                    List<Integer> daysm = new ArrayList<>();
                    List<List<PlanDeVuelo>> _planesRutas = generarEscalas(origen, destino, planes, daysm);
                    for (List<PlanDeVuelo> _planRuta : _planesRutas) {
                        RutaComun ruta = new RutaComun();
                        ruta.setCodigoIATAOrigen(origen.getCodigoIATA());
                        ruta.setCodigoIATADestino(destino.getCodigoIATA());
                        ruta.setPlanRuta(_planRuta);
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

        dfs(origen, destino, currentRoute, allRoutes, planes, daysm, 0);
        return allRoutes;
    }

    private static void dfs(Aeropuerto current, Aeropuerto destination, List<PlanDeVuelo> currentRoute,
            List<List<PlanDeVuelo>> allRoutes, List<PlanDeVuelo> planes, List<Integer> daysm, int days) {

        if (current.equals(destination)) {
            System.out.println(
                    "Destination airport: " + destination.getCiudad() + " " + destination.getZonaHorariaGMT());
            List<PlanDeVuelo> routeToAdd = new ArrayList<>(currentRoute);
            if (!containsRoute(allRoutes, routeToAdd)) {
                System.out.println("Added route: " + routeToAdd.stream()
                        .map(plan -> plan.getAeropuertoOrigen().getCiudad() + " (Departure: " + plan.getHoraSalida()
                                + ", Arrival: " + plan.getHoraLlegada() + ")")
                        .collect(Collectors.joining(" -> ")));
                System.out.println("Days: " + days);
                allRoutes.add(routeToAdd);
                daysm.add(days);
                return;
            }
        }
        for (PlanDeVuelo plan : planes) {
            if (plan.getAeropuertoOrigen().equals(current)) {
                OffsetTime arrivalTime = plan.getHoraLlegada();
                OffsetTime departureTime = plan.getHoraSalida();
                OffsetTime finalTime = null;
                boolean sameContinent;
                OffsetTime salidaConOffset;

                if (currentRoute.size() > 0) {
                    sameContinent = currentRoute.get(0).getAeropuertoOrigen().getContinente()
                            .equals(destination.getContinente());

                    PlanDeVuelo lastPlan = currentRoute.get(currentRoute.size() - 1);
                    finalTime = lastPlan.getHoraLlegada();
                    if (!finalTime.plusMinutes(5).isBefore(departureTime)) {
                        continue;
                    }
                    if (currentRoute.stream()
                            .anyMatch(p -> p.getAeropuertoOrigen().equals(plan.getAeropuertoDestino()))) {
                        continue;
                    }
                } else {
                    sameContinent = plan.getAeropuertoOrigen().getContinente()
                            .equals(destination.getContinente());
                }

                if (arrivalTime.isBefore(departureTime) || (finalTime != null && departureTime.isBefore(finalTime))) {
                    days++;
                }

                if (sameContinent) {
                    if (!currentRoute.contains(plan)) {
                        if (days <= 1) {
                            if (days == 1 && currentRoute.size() > 0) {
                                salidaConOffset = currentRoute.get(0).getHoraSalida()
                                        .withOffsetSameInstant(ZoneOffset.ofHours(destination.getZonaHorariaGMT()));

                                if (arrivalTime.isAfter(salidaConOffset)) {
                                    continue;
                                }
                            }
                            currentRoute.add(plan);

                            dfs(plan.getAeropuertoDestino(), destination, currentRoute, allRoutes, planes, daysm, days);
                            currentRoute.remove(currentRoute.size() - 1);
                        }
                        if (arrivalTime.isBefore(departureTime)
                                || (finalTime != null && departureTime.isBefore(finalTime))) {
                            days--;
                        }
                    }
                }

                else {
                    if (!currentRoute.contains(plan)) {
                        if (days <= 2) {
                            if (days == 2 && currentRoute.size() > 0) {
                                salidaConOffset = currentRoute.get(0).getHoraSalida()
                                        .withOffsetSameInstant(ZoneOffset.ofHours(destination.getZonaHorariaGMT()));

                                if (arrivalTime.isAfter(salidaConOffset)) {
                                    continue;
                                }
                            }
                            currentRoute.add(plan);
                            dfs(plan.getAeropuertoDestino(), destination, currentRoute, allRoutes, planes, daysm, days);
                            currentRoute.remove(currentRoute.size() - 1);
                        }

                        if (arrivalTime.isBefore(departureTime)
                                || (finalTime != null && departureTime.isBefore(finalTime))) {
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
