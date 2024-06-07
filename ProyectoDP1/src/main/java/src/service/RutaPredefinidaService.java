package src.service;
import org.springframework.stereotype.Service;
import src.model.*;

import javax.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RutaPredefinidaService {
    private static final DateTimeFormatter OFFSET_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mmXXX");
    private static final DateTimeFormatter OFFSET_TIME = DateTimeFormatter.ofPattern("HH:mm");
    
    private List<RutaPredefinida> rutasPredefinidas;

    public List<RutaPredefinida> getRutasPredefinidas(List<Envio> envios) {
        try {
            rutasPredefinidas = cargarRutas(envios,
            "src/main/resources/rutasPred/rutas_predefinidas_ZBAA.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_EBCI.csv", 
            "src/main/resources/rutasPred/rutas_predefinidas_EDDI.csv", 
            "src/main/resources/rutasPred/rutas_predefinidas_LATI.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_LBSF.csv", 
            "src/main/resources/rutasPred/rutas_predefinidas_LKPR.csv", 
            "src/main/resources/rutasPred/rutas_predefinidas_LOWW.csv", 
            "src/main/resources/rutasPred/rutas_predefinidas_SABE.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_SBBR.csv", 
            "src/main/resources/rutasPred/rutas_predefinidas_SCEL.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_SEQM.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_SGAS.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_SKBO.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_SLLP.csv", 
            "src/main/resources/rutasPred/rutas_predefinidas_SPIM.csv", 
            "src/main/resources/rutasPred/rutas_predefinidas_SUAA.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_EHAM.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_EKCH.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_LDZA.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_OMDB.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_RJTT.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_RKSI.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_RPLL.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_UMMS.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_VTBS.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_WIII.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_WMKK.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_WSSS.csv",
            "src/main/resources/rutasPred/rutas_predefinidas_SVMI.csv" );
        } catch (IOException e) {
            System.err.println("Error al cargar las rutas predefinidas: " + e.getMessage());
            rutasPredefinidas = new ArrayList<>();
        }
        return rutasPredefinidas;
    }

    public List<RutaPredefinida> cargarRutas(List<Envio> envios, String... archivos) throws IOException {
    List<RutaPredefinida> rutas = new ArrayList<>();

    for (String archivo : archivos) {
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(archivo, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue; // Ignorar líneas vacías

                String[] partesRuta = line.split("\\|");
                String[] primeraParte = partesRuta[0].split(",");

                String codigoIATAOrigen = primeraParte[0];
                String codigoIATADestino = primeraParte[1];

                boolean envioExiste = envios.stream().anyMatch(envio ->
                        envio.getCodigoIATAOrigen().equals(codigoIATAOrigen) &&
                        envio.getCodigoIATADestino().equals(codigoIATADestino)
                );

                if (!envioExiste) continue; 
                
                OffsetTime horaSalida = OffsetTime.parse(primeraParte[2], OFFSET_TIME_FORMATTER);
                OffsetTime horaLlegada = OffsetTime.parse(primeraParte[3], OFFSET_TIME_FORMATTER);
                long duracion = Long.parseLong(primeraParte[4]);
                boolean sameContinent = Boolean.parseBoolean(primeraParte[5]);

                List<PlanDeVuelo> escalas = new ArrayList<>();
                for (int i = 1; i < partesRuta.length; i++) {
                    String[] escalaPartes = partesRuta[i].split(",");
                    int index = Integer.parseInt(escalaPartes[0]);
                    String escalaOrigen = escalaPartes[1];
                    String escalaDestino = escalaPartes[2];
                    OffsetTime escalaHoraSalida = OffsetTime.parse(escalaPartes[3], OFFSET_TIME_FORMATTER);
                    OffsetTime escalaHoraLlegada = OffsetTime.parse(escalaPartes[4], OFFSET_TIME_FORMATTER);
                    int escalaDuracion = Integer.parseInt(escalaPartes[5]);

                    PlanDeVuelo escala = new PlanDeVuelo(index, escalaOrigen, escalaDestino, escalaHoraSalida, escalaHoraLlegada, escalaDuracion, false);
                    escalas.add(escala);
                }

                RutaPredefinida ruta = new RutaPredefinida(codigoIATAOrigen, codigoIATADestino, horaSalida, horaLlegada, escalas, duracion, sameContinent);
                rutas.add(ruta);
            }
        }
    }

    return rutas;
    }
}