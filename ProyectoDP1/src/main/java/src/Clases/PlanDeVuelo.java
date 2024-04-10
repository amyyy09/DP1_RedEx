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
import java.util.stream.IntStream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PlanDeVuelo {
    private int capacidadMaxima;
    private OffsetTime horaSalida;
    private OffsetTime horaLlegada;
    private Aeropuerto aeropuertoOrigen;
    private Aeropuerto aeropuertoDestino;

    public static List<PlanDeVuelo> leerPlanesDeVuelo(List<Aeropuerto> aeropuertos) {
        List<PlanDeVuelo> planesDeVuelo = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader("ProyectoDP1/src/main/resources/Planes.vuelo.v1.incompleto.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("-");
                LocalTime horaSalida = LocalTime.parse(parts[2]);
                LocalTime horaLlegada = LocalTime.parse(parts[3]);
                int capacidadMaxima = Integer.parseInt(parts[4]);
                int indexOrigen = IntStream.range(0, aeropuertos.size())
                        .filter(i -> aeropuertos.get(i).getCodigoIATA().equals(parts[0]))
                        .findFirst()
                        .orElse(-1);

                int indexDestino = IntStream.range(0, aeropuertos.size())
                        .filter(i -> aeropuertos.get(i).getCodigoIATA().equals(parts[1]))
                        .findFirst()
                        .orElse(-1);

                OffsetTime horaSalidaOffset = OffsetTime.of(horaSalida,
                        ZoneOffset.ofHours(aeropuertos.get(indexOrigen).getZonaHorariaGMT()));
                OffsetTime horaLlegadaOffset = OffsetTime.of(horaLlegada,
                        ZoneOffset.ofHours(aeropuertos.get(indexDestino).getZonaHorariaGMT()));

                PlanDeVuelo plan = new PlanDeVuelo(capacidadMaxima, horaSalidaOffset, horaLlegadaOffset,
                        aeropuertos.get(indexOrigen), aeropuertos.get(indexDestino));
                planesDeVuelo.add(plan);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return planesDeVuelo;
    }

}
