package src.service;
import org.springframework.stereotype.Service;
import src.model.PlanDeVuelo;
import src.model.RutaPredefinida;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        rutasPredefinidas = new CopyOnWriteArrayList<>();
        try {
            rutasPredefinidas = cargarRutas(
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredZBAA.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredEBCI.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredEDDI.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredLATI.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredLBSF.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredLKPR.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredLOWW.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSABE.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSBBR.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSCEL.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSEQM.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSGAS.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSKBO.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSLLP.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSPIM.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSUAA.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutasPredSVMI.csv"
        );
            System.out.println("Rutas predefinidas cargadas correctamente.");
        } catch (IOException e) {
            System.err.println("Error al cargar las rutas predefinidas: " + e.getMessage());
            rutasPredefinidas = new ArrayList<>();
        }
        
    }

    public List<RutaPredefinida> getRutasPredefinidas() {
        return rutasPredefinidas;
    }

    public List<RutaPredefinida> cargarRutas(String... archivos) throws IOException {
    List<RutaPredefinida> rutas = new ArrayList<>();

    for (String archivo : archivos) {
        List<String> lines = Files.readAllLines(Paths.get(archivo), StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line.isEmpty()) continue; // Ignorar líneas vacías

            String[] partesRuta = line.split("\\|");
            String[] primeraParte = partesRuta[0].split(",");

            String codigoIATAOrigen = primeraParte[0];
            String codigoIATADestino = primeraParte[1];
            OffsetTime horaSalida = OffsetTime.parse(primeraParte[2], OFFSET_TIME_FORMATTER);
            OffsetTime horaLlegada = OffsetTime.parse(primeraParte[3], OFFSET_TIME_FORMATTER);
            long duracion = Long.parseLong(primeraParte[4]);
            boolean sameContinent = Boolean.parseBoolean(primeraParte[5]);

            List<PlanDeVuelo> escalas = new ArrayList<>();
            for (int i = 1; i < partesRuta.length; i++) {
                String[] escalaPartes = partesRuta[i].split(",");
                String escalaOrigen = escalaPartes[0];
                String escalaDestino = escalaPartes[1];
                OffsetTime escalaHoraSalida = OffsetTime.parse(escalaPartes[2], OFFSET_TIME_FORMATTER);
                OffsetTime escalaHoraLlegada = OffsetTime.parse(escalaPartes[3], OFFSET_TIME_FORMATTER);
                int escalaDuracion = Integer.parseInt(escalaPartes[4]);

                PlanDeVuelo escala = new PlanDeVuelo(i, escalaOrigen, escalaDestino, escalaHoraSalida, escalaHoraLlegada, escalaDuracion, false);
                escalas.add(escala);
            }

            RutaPredefinida ruta = new RutaPredefinida(codigoIATAOrigen, codigoIATADestino, horaSalida, horaLlegada, escalas, duracion, sameContinent);
            rutas.add(ruta);
            }
        }

        return rutas;
    }
}