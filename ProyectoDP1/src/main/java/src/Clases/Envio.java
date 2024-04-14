package src.Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Envio {
    private String idEnvio;
    private OffsetDateTime fechaHoraOrigen;
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private int cantPaquetes;
    private List<Paquete> paquetes; // Asumimos que la clase Paquete ya está definida en otro lugar

    public static List<Envio> leerEnvios(List<String> lineasEnvio) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");
        ZoneOffset zoneOffset = ZoneOffset.UTC; // Asumimos UTC para simplificar, ajusta según necesidad

        return lineasEnvio.stream().map(linea -> {
            String[] partes = linea.split("-");
            String ciudadOrigen = partes[0].substring(0, 4);
            String idEnvio = partes[0].substring(4); // Extrae el ID del envío
            LocalDateTime fechaHora = LocalDateTime.parse(partes[1] + "-" + partes[2], formatter);
            String ciudadDestino = partes[3].split(":")[0];
            int cantPaquetes = Integer.parseInt(partes[3].split(":")[1]);

            // Crea el objeto Envio
            return new Envio(idEnvio, OffsetDateTime.of(fechaHora, zoneOffset), ciudadOrigen, ciudadDestino,
                    cantPaquetes,
                    new ArrayList<Paquete>());
        }).collect(Collectors.toList());
    }

}
