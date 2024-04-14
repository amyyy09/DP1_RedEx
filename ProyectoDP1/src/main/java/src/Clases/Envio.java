package src.Clases;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    private List<Paquete> paquetes;

    public static List<Envio> obtenerEnvios() {
        List<Envio> envios = new ArrayList<>();
        String archivo = "src/main/resources/pack_enviado/pack_enviado_EBCI.txt";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("-");
                String codigoIATAOrigen = partes[0].substring(0, 4);
                String idEnvio = partes[0].substring(4); // Extrae el ID del envío
                LocalDateTime fechaHora = LocalDateTime.parse(partes[1] + "-" + partes[2], formatter);
                String codigoIATADestino = partes[3].split(":")[0];
                int cantPaquetes = Integer.parseInt(partes[3].split(":")[1]);

                List<Paquete> paquetes = new ArrayList<>();
                for (int i = 0; i < cantPaquetes; i++) {
                    paquetes.add(new Paquete(idEnvio, 0)); // Estado inicial 0 para cada paquete
                }

                envios.add(new Envio(idEnvio, OffsetDateTime.of(fechaHora, zoneOffset), codigoIATAOrigen,
                        codigoIATADestino, cantPaquetes, paquetes));
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return envios;
    }
}
