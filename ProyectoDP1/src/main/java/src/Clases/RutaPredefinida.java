package src.Clases;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RutaPredefinida {

    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private OffsetTime horaSalida;
    private OffsetTime horaLlegada;
    private List<PlanDeVuelo> escalas;
    private int ndays;

    public static void guardarRutasEnCSV(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes,
            String archivoDestino) {
        List<RutaPredefinida> rutas = generarRutas(aeropuertos, planes);
        List<String> lineas = new ArrayList<>();
        lineas.addAll(rutas.stream().map(RutaPredefinida::formatoRutaCSV).collect(Collectors.toList()));

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(archivoDestino))) {
            for (String linea : lineas) {
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    private static String formatoRutaCSV(RutaPredefinida ruta) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String detallesPlanes = ruta.getEscalas().stream()
                .map(plan -> String.format("%s,%s,%s,%s,%d",
                        plan.getCodigoIATAOrigen(),
                        plan.getCodigoIATADestino(),
                        plan.getHoraSalida() != null ? plan.getHoraSalida().format(formatter) : "N/D",
                        plan.getHoraLlegada() != null ? plan.getHoraLlegada().format(formatter) : "N/D",
                        plan.getCapacidad()))
                .collect(Collectors.joining("|"));

        return detallesPlanes;
    }

    public static List<RutaPredefinida> obtenerRutasConEscalas() {
        String archivoRutas = "src/main/resources/rutPred.txt";
        List<RutaPredefinida> rutas = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        try (Stream<String> lineas = Files.lines(Paths.get(archivoRutas))) {
            lineas.forEach(linea -> {
                String[] vuelos = linea.split("\\|");
                List<PlanDeVuelo> planDeVuelos = new ArrayList<>();

                for (String vuelo : vuelos) {
                    String[] detalles = vuelo.split(",");
                    String origen = detalles[0];
                    String destino = detalles[1];
                    OffsetTime salida = OffsetTime.parse(detalles[2], formatter);
                    OffsetTime llegada = OffsetTime.parse(detalles[3], formatter);
                    int capacidad = Integer.parseInt(detalles[4]);
                    PlanDeVuelo planDeVuelo = new PlanDeVuelo(origen, destino, salida, llegada, capacidad, false); // flt
                    planDeVuelos.add(planDeVuelo);
                }

                String origenInicial = planDeVuelos.get(0).getCodigoIATAOrigen();
                String destinoFinal = planDeVuelos.get(planDeVuelos.size() - 1).getCodigoIATADestino();
                OffsetTime horaInicial = planDeVuelos.get(0).getHoraSalida();
                OffsetTime horaFinal = planDeVuelos.get(planDeVuelos.size() - 1).getHoraLlegada();
                int dias = Integer.parseInt(vuelos[vuelos.length - 1].split(",")[4]); // Obtener días del último vuelo

                RutaPredefinida ruta = new RutaPredefinida(origenInicial, destinoFinal, horaInicial, horaFinal,
                        planDeVuelos, dias);
                rutas.add(ruta);
            });
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return rutas;
    }

    public static List<RutaPredefinida> generarRutas(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes) {
        List<RutaPredefinida> rutas = new ArrayList<>();
        for (Aeropuerto origen : aeropuertos) {
            for (Aeropuerto destino : aeropuertos) {
                if (!origen.equals(destino) && origen.getContinente()
                        .equals(destino.getContinente())) {
                    List<Integer> daysm = new ArrayList<>();
                    List<List<PlanDeVuelo>> _planesRutas = generarEscalas(origen, destino, planes, daysm);
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

        if (currentRoute.size() > 10) {
            return; // Si se exceden 20 escalas, detiene la recursión para esta ruta
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
                            .anyMatch(p -> p.getCodigoIATAOrigen().equals(plan.getCodigoIATADestino()))) {
                        continue;
                    }
                }

                if (arrivalTime.isBefore(departureTime) || (finalTime != null && departureTime.isBefore(finalTime))) {
                    days++;
                }

                if (plan.isSameContinent()) {
                    if (!currentRoute.contains(plan)) {
                        if (days <= 1) {
                            currentRoute.add(plan);
                            dfs(plan.getCodigoIATADestino(), destination, currentRoute, allRoutes, planes, daysm, days);
                            currentRoute.remove(currentRoute.size() - 1);
                        }
                        if (arrivalTime.isBefore(departureTime)
                                || (finalTime != null && departureTime.isBefore(finalTime))) {
                            days--;
                        }
                    }
                } else {
                    if (!currentRoute.contains(plan)) {
                        if (days <= 2) {
                            currentRoute.add(plan);
                            dfs(plan.getCodigoIATADestino(), destination, currentRoute, allRoutes, planes, daysm, days);
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
