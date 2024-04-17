package src.Clases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class Utilities {
    public Utilities() {
    }

    public static List<Vuelo> getVuelosActualesTesting(List<PlanDeVuelo> planesDeVuelo) {
        List<Vuelo> vuelosActuales = new ArrayList<>();
        OffsetTime ahora = OffsetTime.now(); // Captura la hora actual con su zona horaria correspondiente.

        int vueloId = 1; 
        for (PlanDeVuelo plan : planesDeVuelo) {
            if (ahora.isAfter(plan.getHoraSalida()) && ahora.isBefore(plan.getHoraLlegada())) {
                // Creamos un nuevo vuelo en tránsito.
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

    public static List<PlanDeVuelo> getPlanesDeVuelo(List<Aeropuerto> aeropuertos) {
        List<PlanDeVuelo> planesDeVuelo = new ArrayList<>();
        String archivo = "src/main/resources/Planes.vuelo.v1.incompleto.txt";
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

private static OffsetTime getOffsetTimeForAirport(String codigoIATA, LocalTime localTime,
                List<Aeropuerto> aeropuertos) {
        return aeropuertos.stream()
                        .filter(a -> a.getCodigoIATA().equals(codigoIATA))
                        .findFirst()
                        .map(a -> OffsetTime.of(localTime, ZoneOffset.ofHours(a.getZonaHorariaGMT())))
                        .orElse(null); // Retorna null si no se encuentra el aeropuerto.
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
}
