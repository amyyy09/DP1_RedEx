package src.service;
import org.springframework.stereotype.Service;

import src.global.GlobalVariables;
import src.model.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class RutaPredefinidaService {
    private static final DateTimeFormatter OFFSET_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mmXXX");
    
    public Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredefinidas;

     public Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> getRutasPredefinidas() {
        try {
            rutasPredefinidas = cargarRutas(
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_EBCI.csv", 
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_EDDI.csv", 
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_EHAM.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_EKCH.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_LATI.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_LBSF.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_LDZA.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_LKPR.csv", 
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_LOWW.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_OAKB.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_OERK.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_OJAI.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_OMDB.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_OOMS.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_OPKC.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_OSDI.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_OYSN.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SABE.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SBBR.csv", 
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SCEL.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SEQM.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SGAS.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SKBO.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SLLP.csv", 
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SPIM.csv", 
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SUAA.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_SVMI.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_UBBB.csv",
            //  GlobalVariables.PATH + "rutasPred/rutas_predefinidas_UMMS.csv",
             GlobalVariables.PATH + "rutasPred/rutas_predefinidas_VIDP.csv"
              );
        } catch (IOException e) {
            System.err.println("Error al cargar las rutas predefinidas: " + e.getMessage());
            rutasPredefinidas = new HashMap<>();
        }
        return rutasPredefinidas;
    }

    public Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> cargarRutas( String... archivos) throws IOException {
    Map<String, Map<String, TreeMap<Integer, TreeMap<Integer, List<RutaPredefinida>>>>> rutasPredMap = new HashMap<>();

    // Create a set of valid routes for quick lookup
   Set<String> validRoutes = new HashSet<>();
   validRoutes.add("VIDP-SVMI");

    for (String archivo : archivos) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue; // Ignore empty lines

                String[] partesRuta = line.split("\\|");
                String[] primeraParte = partesRuta[0].split(",");
                String codigoIATAOrigen = primeraParte[0];
                String codigoIATADestino = primeraParte[1];

                // Check if the route exists in the set of valid routes
                if (!validRoutes.contains(codigoIATAOrigen + "-" + codigoIATADestino)) continue;

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
                    int capacidad = Integer.parseInt(escalaPartes[5]);
                    int diasDuracion = Integer.parseInt(escalaPartes[6]);
                    capacidad = capacidad-220;//ajuste

                    PlanDeVuelo escala = new PlanDeVuelo(index, escalaOrigen, escalaDestino, escalaHoraSalida, escalaHoraLlegada, capacidad, false, diasDuracion);
                    escalas.add(escala);
                }

                RutaPredefinida ruta = new RutaPredefinida(codigoIATAOrigen, codigoIATADestino, horaSalida, horaLlegada, escalas, duracion, sameContinent);

                // Directly add to rutasPredMap
                int horaSalidaKey = horaSalida.getHour() * 100 + horaSalida.getMinute();
                int horaLlegadaKey = horaLlegada.getHour() * 100 + horaLlegada.getMinute();

                rutasPredMap
                    .computeIfAbsent(codigoIATAOrigen, k -> new HashMap<>())
                    .computeIfAbsent(codigoIATADestino, k -> new TreeMap<>())
                    .computeIfAbsent(horaLlegadaKey, k -> new TreeMap<>())
                    .computeIfAbsent(horaSalidaKey, k -> new ArrayList<>())
                    .add(ruta);
            }
        }
    }

    return rutasPredMap;
}

}