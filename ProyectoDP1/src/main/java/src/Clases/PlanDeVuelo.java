package src.Clases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PlanDeVuelo {
        private int capacidad;
        private OffsetTime horaSalida;
        private OffsetTime horaLlegada;
        private String codigoIATAOrigen;
        private String codigoIATADestino;

        public static List<PlanDeVuelo> leerPlanesDeVuelo(List<Aeropuerto> aeropuertos) {
                List<PlanDeVuelo> planesDeVuelo = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(
                                new FileReader("ProyectoDP1/src/main/resources/Planes.vuelo.v1.incompleto.txt"))) {
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

                                if (horaSalidaOffset != null && horaLlegadaOffset != null) {
                                        PlanDeVuelo plan = new PlanDeVuelo(capacidad, horaSalidaOffset,
                                                        horaLlegadaOffset,
                                                        codigoIATAOrigen, codigoIATADestino);
                                        planesDeVuelo.add(plan);
                                }
                        }
                } catch (IOException e) {
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
}
