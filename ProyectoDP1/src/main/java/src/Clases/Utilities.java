package src.Clases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Utilities {
        public Utilities() {
        }

        public static List<Vuelo> getVuelosActualesTesting(List<PlanDeVuelo> planesDeVuelo) {
                List<Vuelo> vuelosActuales = new ArrayList<>();
                OffsetTime ahora = OffsetTime.now(); // Captura la hora actual con su zona horaria correspondiente.

                int vueloId = 1;
                for (PlanDeVuelo plan : planesDeVuelo) {
                        if (ahora.isAfter(plan.getHoraSalida()) && ahora.isBefore(plan.getHoraLlegada())) {
                                Vuelo vuelo = new Vuelo();
                                vuelo.setIdVuelo(vueloId++); // Genera un ID aleatorio para el ejemplo.
                                vuelo.setCantPaquetes(0); // Inicialmente sin paquetes.
                                vuelo.setCapacidad(plan.getCapacidad());
                                vuelo.setStatus(1); // Establece el estado en tránsito.
                                vuelo.setPlanDeVuelo(plan);

                                vuelosActuales.add(vuelo);
                        }
                }

                return vuelosActuales;
        }

        public static List<PlanDeVuelo> getPlanesDeVuelo(List<Aeropuerto> aeropuertos, String archivo) {
                List<PlanDeVuelo> planesDeVuelo = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                                String[] parts = line.split("-");
                                if (parts.length < 5)
                                        continue; // Asegura que todas las partes necesarias están presentes.

                                String codigoIATAOrigen = parts[0];
                                String codigoIATADestino = parts[1];
                                LocalTime horaSalidaLocal = LocalTime.parse(parts[2]);
                                LocalTime horaLlegadaLocal = LocalTime.parse(parts[3]);
                                int capacidad = Integer.parseInt(parts[4]);

                                OffsetTime horaSalidaOffset = getOffsetTimeForAirport(codigoIATAOrigen, horaSalidaLocal,
                                                aeropuertos);
                                OffsetTime horaLlegadaOffset = getOffsetTimeForAirport(codigoIATADestino,
                                                horaLlegadaLocal, aeropuertos);
                                boolean isSameContinent = isSameContinent(codigoIATAOrigen, codigoIATADestino,
                                                aeropuertos);

                                if (horaSalidaOffset != null && horaLlegadaOffset != null) {
                                        PlanDeVuelo plan = new PlanDeVuelo(codigoIATAOrigen, codigoIATADestino,
                                                        horaSalidaOffset, horaLlegadaOffset, capacidad,
                                                        isSameContinent);
                                        planesDeVuelo.add(plan);
                                }
                        }
                } catch (

                IOException e) {
                        e.printStackTrace();
                }
                return planesDeVuelo;
        }

        public static List<Envio> getEnvios(String archivo) {
                List<Envio> envios = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");
                try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                        String linea;
                        while ((linea = reader.readLine()) != null) {
                                String[] partes = linea.split("-");
                                String codigoIATAOrigen = partes[0].substring(0, 4);
                                String idEnvio = partes[0].substring(4);
                                LocalDateTime fechaHora = LocalDateTime.parse(partes[1] + "-" + partes[2], formatter);
                                String codigoIATADestino = partes[3].split(":")[0];
                                int cantPaquetes = Integer.parseInt(partes[3].split(":")[1]);

                                List<Paquete> paquetes = new ArrayList<>();
                                for (int i = 0; i < cantPaquetes; i++) {
                                        paquetes.add(new Paquete(idEnvio, 0));
                                }

                                envios.add(new Envio(idEnvio, fechaHora, 0, codigoIATAOrigen,
                                                codigoIATADestino, cantPaquetes, paquetes));
                        }
                } catch (IOException e) {
                        System.err.println("Error al leer el archivo: " + e.getMessage());
                }
                return envios;
        }

        private static OffsetTime getOffsetTimeForAirport(String codigoIATA, LocalTime localTime,
                        List<Aeropuerto> aeropuertos) {
                return aeropuertos.stream()
                                .filter(a -> a.getCodigoIATA().equals(codigoIATA))
                                .findFirst()
                                .map(a -> OffsetTime.of(localTime, ZoneOffset.ofHours(a.getZonaHorariaGMT())))
                                .orElse(null); // Retorna null si no se encuentra el aeropuerto.
        }

        public static String chooseFile() {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "text");
                fileChooser.setFileFilter(filter);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                        return fileChooser.getSelectedFile().getPath();
                } else {
                        return null;
                }
        }

        public static boolean isSameContinent(String codigoIATAOrigen, String codigoIATADestino,
                        List<Aeropuerto> aeropuertos) {
                Aeropuerto origen = aeropuertos.stream().filter(a -> a.getCodigoIATA().equals(codigoIATAOrigen))
                                .findFirst().orElse(null);
                Aeropuerto destino = aeropuertos.stream().filter(a -> a.getCodigoIATA().equals(codigoIATADestino))
                                .findFirst().orElse(null);
                if (origen != null && destino != null) {
                        return origen.getContinente().equals(destino.getContinente());
                }
                return false;
        }

        private static String formatoRutaCSV(RutaPredefinida ruta) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String detallesPlanes = ruta.getEscalas().stream()
                                .map(plan -> String.format("%s,%s,%s,%s,%d",
                                                plan.getCodigoIATAOrigen(),
                                                plan.getCodigoIATADestino(),
                                                plan.getHoraSalida() != null ? plan.getHoraSalida().format(formatter)
                                                                : "N/D",
                                                plan.getHoraLlegada() != null ? plan.getHoraLlegada().format(formatter)
                                                                : "N/D",
                                                plan.getCapacidad()))
                                .collect(Collectors.joining("|"));
                return detallesPlanes;
        }

        public static void guardarRutasEnCSV(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes,
                        String archivoDestino) {
                List<RutaPredefinida> rutas = generarRutas(aeropuertos, planes);
                List<String> lineas = rutas.stream()
                                .map(Utilities::formatoRutaCSV)
                                .collect(Collectors.toList());

                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(archivoDestino))) {
                        for (String linea : lineas) {
                                writer.write(linea);
                                writer.newLine();
                        }
                } catch (IOException e) {
                        System.err.println("Error al escribir en el archivo: " + e.getMessage());
                }
        }

        public static List<RutaPredefinida> obtenerRutasConEscalas(List<Aeropuerto> aeropuertos, String archivoRutas) {
                List<RutaPredefinida> rutas = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                try (Stream<String> lineas = Files.lines(Paths.get(archivoRutas))) {
                        lineas.forEach(linea -> {
                                String[] vuelos = linea.split("\\|");
                                List<PlanDeVuelo> planDeVuelos = new ArrayList<>();
                                long duracion = 0;

                                for (String vuelo : vuelos) {
                                        String[] detalles = vuelo.split(",");
                                        String origen = detalles[0];
                                        String destino = detalles[1];
                                        OffsetTime salida = OffsetTime.parse(detalles[2], formatter);
                                        OffsetTime llegada = OffsetTime.parse(detalles[3], formatter);
                                        int capacidad = Integer.parseInt(detalles[4]);
                                        PlanDeVuelo planDeVuelo = new PlanDeVuelo(origen, destino, salida, llegada,
                                                        capacidad, false); // flt
                                        planDeVuelos.add(planDeVuelo);
                                }

                                String origenInicial = planDeVuelos.get(0).getCodigoIATAOrigen();
                                String destinoFinal = planDeVuelos.get(planDeVuelos.size() - 1).getCodigoIATADestino();
                                OffsetTime horaInicial = planDeVuelos.get(0).getHoraSalida();
                                OffsetTime horaFinal = planDeVuelos.get(planDeVuelos.size() - 1).getHoraLlegada();

                                RutaPredefinida ruta = new RutaPredefinida(origenInicial, destinoFinal, horaInicial,
                                                horaFinal,
                                                planDeVuelos, duracion);
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
                                        List<List<PlanDeVuelo>> _planesRutas = generarEscalas(origen, destino, planes,
                                                        daysm);
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

                if (currentRoute.size() > 8) {
                        return; // Si se exceden 8 escalas, detiene la recursión para esta ruta
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
                                                        .anyMatch(p -> p.getCodigoIATAOrigen()
                                                                        .equals(plan.getCodigoIATADestino()))) {
                                                continue;
                                        }
                                }

                                if (arrivalTime.isBefore(departureTime)
                                                || (finalTime != null && departureTime.isBefore(finalTime))) {
                                        days++;
                                }

                                if (plan.isSameContinent()) {
                                        if (!currentRoute.contains(plan)) {
                                                if (days <= 1) {
                                                        currentRoute.add(plan);
                                                        dfs(plan.getCodigoIATADestino(), destination, currentRoute,
                                                                        allRoutes, planes, daysm, days);
                                                        currentRoute.remove(currentRoute.size() - 1);
                                                }
                                                if (arrivalTime.isBefore(departureTime)
                                                                || (finalTime != null
                                                                                && departureTime.isBefore(finalTime))) {
                                                        days--;
                                                }
                                        }
                                } else {
                                        if (!currentRoute.contains(plan)) {
                                                if (days <= 2) {
                                                        currentRoute.add(plan);
                                                        dfs(plan.getCodigoIATADestino(), destination, currentRoute,
                                                                        allRoutes, planes, daysm, days);
                                                        currentRoute.remove(currentRoute.size() - 1);
                                                }
                                                if (arrivalTime.isBefore(departureTime)
                                                                || (finalTime != null
                                                                                && departureTime.isBefore(finalTime))) {
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
