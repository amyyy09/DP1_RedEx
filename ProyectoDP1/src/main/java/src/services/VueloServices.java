package src.services;

import src.model.*;
import src.utility.FileUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class VueloServices {

    public static List<Envio>  getEnvios(String archivo) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");
        List<String> lines = FileUtils.readLines(archivo);
        List<Envio> envios = new ArrayList<>();

        for (String line : lines) {
            String[] partes = line.split("-");
            String codigoIATAOrigen = partes[0];
            String idEnvio = partes[1];
            LocalDateTime fechaHora = LocalDateTime.parse(partes[2] + "-" + partes[3], formatter);
            //LocalDateTime fechaHora = LocalDateTime.now();
            
            String codigoIATADestino = partes[4].split(":")[0];
            int cantPaquetes = Integer.parseInt(partes[4].split(":")[1]);

            Envio envio = new Envio(idEnvio, fechaHora, 0, codigoIATAOrigen,
                    codigoIATADestino, cantPaquetes, null);

            List<Paquete> paquetes = new ArrayList<>();
            for (int i = 0; i < cantPaquetes; i++) {
                paquetes.add(new Paquete(idEnvio, 0, envio));
            }

            envio.setPaquetes(paquetes);

            envios.add(envio);
        }
        return envios;

    }

    public static List<Vuelo> getVuelosActuales(List<PlanDeVuelo> planesDeVuelo,List<Vuelo> vuelos) {
        List<Vuelo> vuelosActuales = new ArrayList<>();

        // Crear un mapa para acceder a los planes de vuelo rápidamente por indexPlan
        Map<Integer, PlanDeVuelo> planDeVueloMap = planesDeVuelo.stream()
                .collect(Collectors.toMap(PlanDeVuelo::getIndexPlan, plan -> plan));

        // Iterar sobre la lista de vuelos y asignar el plan de vuelo correspondiente
        for (Vuelo vuelo : vuelos) {
            PlanDeVuelo planDeVuelo = planDeVueloMap.get(vuelo.getIndexPlan());
            if (planDeVuelo != null) {
                vuelo.setPlanDeVuelo(planDeVuelo);
                vuelo.setCapacidad(planDeVuelo.getCapacidad());
                vuelosActuales.add(vuelo);
            }
        }

        return vuelosActuales;
    }

    public static List<PlanDeVuelo> getPlanesDeVuelo(List<Aeropuerto> aeropuertos, String archivo) throws IOException {
        List<String> lines = FileUtils.readLines(archivo);
        List<PlanDeVuelo> planesDeVuelo = new ArrayList<>();

        int i=1;
        for (String line : lines) {
            String[] parts = line.split("-");
            if (parts.length < 5)
                continue; // Asegura que todas las partes necesarias están presentes.

            PlanDeVuelo plan = parsePlanDeVuelo(i, parts, aeropuertos);
            if (plan != null) {
                planesDeVuelo.add(plan);
                i++;
            }
        }

        return planesDeVuelo;
    }

    // public static List<RutaPredefinida> getRutasConEscalas(List<Aeropuerto> aeropuertos, String archivo)
    //         throws IOException {
    //     List<String> lines = FileUtils.readLines(archivo);
    //     List<RutaPredefinida> rutas = new ArrayList<>();
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    //     for (String line : lines) {
    //         String[] vuelos = line.split("\\|");
    //         List<PlanDeVuelo> planDeVuelos = new ArrayList<>();
    //         long duracion = 0;

    //         for (String vuelo : vuelos) {
    //             String[] detalles = vuelo.split(",");
    //             Aeropuerto aeropuertoOrigen = findAeropuertoByCodigo(aeropuertos, detalles[0]);
    //             Aeropuerto aeropuertoDestino = findAeropuertoByCodigo(aeropuertos, detalles[1]);

    //             if (aeropuertoOrigen == null || aeropuertoDestino == null)
    //                 continue; // Skip if no airport data

    //             LocalTime horaSalidaLocal = LocalTime.parse(detalles[2], formatter);
    //             LocalTime horaLlegadaLocal = LocalTime.parse(detalles[3], formatter);
    //             int capacidad = Integer.parseInt(detalles[4]);

    //             OffsetTime salida = OffsetTime.of(horaSalidaLocal,
    //                     ZoneOffset.ofHours(aeropuertoOrigen.getZonaHorariaGMT()));
    //             OffsetTime llegada = OffsetTime.of(horaLlegadaLocal,
    //                     ZoneOffset.ofHours(aeropuertoDestino.getZonaHorariaGMT()));

    //             PlanDeVuelo planDeVuelo = new PlanDeVuelo(detalles[0], detalles[1], salida,
    //                     llegada, capacidad, false);
    //             planDeVuelos.add(planDeVuelo);
    //         }

    //         if (!planDeVuelos.isEmpty()) {
    //             RutaPredefinida ruta = new RutaPredefinida(
    //                     planDeVuelos.get(0).getCodigoIATAOrigen(),
    //                     planDeVuelos.get(planDeVuelos.size() - 1)
    //                             .getCodigoIATADestino(),
    //                     planDeVuelos.get(0).getHoraSalida(),
    //                     planDeVuelos.get(planDeVuelos.size() - 1).getHoraLlegada(),
    //                     planDeVuelos, duracion, false);
    //             rutas.add(ruta);
    //         }
    //     }
    //     return rutas;
    // }

    private static String formatoRutaCSV(RutaPredefinida ruta) {
        DateTimeFormatter OFFSET_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mmXXX");
        // Formatear la información principal de la ruta
        String rutaInfo = String.format("%s,%s,%s,%s,%d,%b",
                ruta.getCodigoIATAOrigen(),
                ruta.getCodigoIATADestino(),
                ruta.getHoraSalida() != null ? ruta.getHoraSalida().format(OFFSET_TIME_FORMATTER)  : "N/D",
                ruta.getHoraLlegada() != null ? ruta.getHoraLlegada().format(OFFSET_TIME_FORMATTER)  : "N/D",
                ruta.getDuracion(),
                ruta.isSameContinent());

        String detallesPlanes = ruta.getEscalas().stream()
                .map(plan -> String.format("%s,%s,%s,%s,%d",
                        plan.getCodigoIATAOrigen(),
                        plan.getCodigoIATADestino(),
                        plan.getHoraSalida() != null ? plan.getHoraSalida().format(OFFSET_TIME_FORMATTER)  : "N/D",
                        plan.getHoraLlegada() != null ? plan.getHoraLlegada().format(OFFSET_TIME_FORMATTER) : "N/D",
                        plan.getCapacidad()))
                .collect(Collectors.joining("|"));
                
        return rutaInfo + "|" + detallesPlanes;
    }

    public static void guardarRutasEnCSV(List<RutaPredefinida> rutas, String archivoDestino) {
        List<String> lineas = rutas.stream()
                .map(VueloServices::formatoRutaCSV)
                .collect(Collectors.toList());

        try {
            FileUtils.writeLines(lineas, archivoDestino);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    private static PlanDeVuelo parsePlanDeVuelo(int i, String[] parts, List<Aeropuerto> aeropuertos) {
        String codigoIATAOrigen = parts[0];
        String codigoIATADestino = parts[1];
        LocalTime horaSalidaLocal = LocalTime.parse(parts[2]);
        LocalTime horaLlegadaLocal = LocalTime.parse(parts[3]);
        int capacidad = Integer.parseInt(parts[4]);

        OffsetTime horaSalidaOffset = getOffsetTimeForAirport(codigoIATAOrigen, horaSalidaLocal, aeropuertos);
        OffsetTime horaLlegadaOffset = getOffsetTimeForAirport(codigoIATADestino, horaLlegadaLocal, aeropuertos);

        if (horaSalidaOffset != null && horaLlegadaOffset != null) {
            boolean isSameContinent = isSameContinent(codigoIATAOrigen, codigoIATADestino, aeropuertos);
            return new PlanDeVuelo(i, codigoIATAOrigen, codigoIATADestino, horaSalidaOffset, horaLlegadaOffset, capacidad,
                    isSameContinent);
        }
        return null;
    }

    private static OffsetTime getOffsetTimeForAirport(String codigoIATA, LocalTime localTime, List<Aeropuerto> aeropuertos) {
        return aeropuertos.stream()
                .filter(a -> a.getCodigoIATA().equals(codigoIATA))
                .findFirst()
                .map(a -> OffsetTime.of(localTime, ZoneOffset.ofHours(a.getZonaHorariaGMT())))
                .orElse(null);
    }

    private static boolean isSameContinent(String codigoIATAOrigen, String codigoIATADestino, List<Aeropuerto> aeropuertos) {
        Aeropuerto origen = findAeropuertoByCodigo(aeropuertos, codigoIATAOrigen);
        Aeropuerto destino = findAeropuertoByCodigo(aeropuertos, codigoIATADestino);
        return origen != null && destino != null && origen.getContinente().equals(destino.getContinente());
    }

    private static Aeropuerto findAeropuertoByCodigo(List<Aeropuerto> aeropuertos, String codigoIATA) {
        return aeropuertos.stream()
                .filter(a -> a.getCodigoIATA().equals(codigoIATA))
                .findFirst()
                .orElse(null);
    }

    public static Vuelo encontrarVueloActual(List<Vuelo> vuelosActivos, RutaPredefinida ruta) {
        OffsetTime horaActual = OffsetTime.now();

        for (Vuelo vuelo : vuelosActivos) {
            if (vuelo.getPlanDeVuelo().getCodigoIATAOrigen().equals(ruta.getCodigoIATAOrigen()) &&
                    vuelo.getPlanDeVuelo().getCodigoIATADestino().equals(ruta.getCodigoIATADestino()) &&
                    vuelo.getPlanDeVuelo().getHoraSalida().isBefore(horaActual) &&
                    vuelo.getPlanDeVuelo().getHoraLlegada().isAfter(horaActual)) {
                return vuelo;
            }
        }
        return null;
    }

    public static Almacen encontrarAlmacenActual(List<Aeropuerto> aeropuertos, String codigoIATA) {
        for (Aeropuerto aeropuerto : aeropuertos) {
            if (aeropuerto.getCodigoIATA().equals(codigoIATA)) {
                return aeropuerto.getAlmacen();
            }
        }
        return null;
    }

    public static void actualizarUsoCapacidadAlmacen(Map<String, Integer> usoCapacidad, String codigoIATA,
            int cantidad) {
        usoCapacidad.put(codigoIATA, usoCapacidad.getOrDefault(codigoIATA, 0) + cantidad);
    }
}