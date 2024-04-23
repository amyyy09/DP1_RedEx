package src.Clases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

        public static List<Envio> getEnvios() {
                List<Envio> envios = new ArrayList<>();
                String archivo = "src/main/resources/pack_enviado/pack_enviado_EBCI.txt";
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
}
