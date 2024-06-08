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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RutaPredefinidaService {
    private static final DateTimeFormatter OFFSET_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mmXXX");
    private static final DateTimeFormatter OFFSET_TIME = DateTimeFormatter.ofPattern("HH:mm");
    
    public Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredefinidas;

     public Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> getRutasPredefinidas(List<Envio> envios) {
        try {
            rutasPredefinidas = cargarRutas(envios,
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_ZBAA.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_EBCI.csv", 
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_EDDI.csv", 
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_LATI.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_LBSF.csv", 
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_LKPR.csv", 
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_LOWW.csv", 
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SABE.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SBBR.csv", 
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SCEL.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SEQM.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SGAS.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SKBO.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SLLP.csv", 
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SPIM.csv", 
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SUAA.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_EHAM.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_EKCH.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_LDZA.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_OMDB.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_RJTT.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_RKSI.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_RPLL.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_UMMS.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_VTBS.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_WIII.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_WMKK.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_WSSS.csv",
            "ProyectoDP1/src/main/resources/rutasPred/rutas_predefinidas_SVMI.csv")
        
        ;
        } catch (IOException e) {
            System.err.println("Error al cargar las rutas predefinidas: " + e.getMessage());
            rutasPredefinidas = new HashMap<>();
        }
        return rutasPredefinidas;
    }

    public Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> cargarRutas(List<Envio> envios, String... archivos) throws IOException {
    List<RutaPredefinida> rutas = new ArrayList<>();
    Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredMap= new HashMap<>();

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

        for (RutaPredefinida ruta : rutas) {
            String origen = ruta.getCodigoIATAOrigen();
            String destino = ruta.getCodigoIATADestino();
            int horaSalida = ruta.getHoraSalida().getHour()*100 + ruta.getHoraSalida().getMinute();
            int horaLlegada = ruta.getHoraLlegada().getHour()*100 + ruta.getHoraLlegada().getMinute();

            Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>> origenMap = rutasPredMap.getOrDefault(origen, new HashMap<>());
            TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>> destinoMap = origenMap.getOrDefault(destino, new TreeMap<>());
            TreeMap<Integer, List<RutaPredefinida>> llegadaMap = destinoMap.getOrDefault(horaLlegada, new TreeMap<>());
            List<RutaPredefinida> rutasA = llegadaMap.getOrDefault(horaSalida, new ArrayList<>());
            rutasA.add(ruta);
            llegadaMap.put(horaSalida, rutasA);
            destinoMap.put(horaLlegada, llegadaMap);
            origenMap.put(destino, destinoMap);
            rutasPredMap.put(origen, origenMap);
        }
    }

    return rutasPredMap;
    }
}