package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import src.services.*;
import src.global.GlobalVariables;
import src.model.*;
import src.repository.EnvioRepository;

@Service
public class EnvioService {

    VueloServices vueloService = new VueloServices();

    @Autowired
    private AeropuertoService aeropuertoService;

    private List<Envio> envios;
    private final String archivoRutaEnvios = GlobalVariables.PATH + "combined.txt";

    public List<Envio> getEnvios() {
        return envios;
    }

    public List<Envio> getEnviosPorFechaHora(LocalDateTime fechaHora, List<Aeropuerto> aeropuertosGuardados) {
        LocalDateTime fechaHoraFin = fechaHora.plusMinutes(40);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");
        List<Envio> envios = new ArrayList<>();

        // Crear un mapa para búsqueda rápida de aeropuertos
        Map<String, Aeropuerto> aeropuertoMap = aeropuertosGuardados.stream()
                .collect(Collectors.toMap(Aeropuerto::getCodigoIATA, aeropuerto -> aeropuerto));

        try (BufferedReader br = new BufferedReader(new FileReader(archivoRutaEnvios))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] partes = line.split("-");
                if (partes.length < 5)
                    continue; // Skip malformed lines

                String codigoIATAOrigen = partes[0];
                String idEnvio = partes[1];
                LocalDateTime fechaHoraI = LocalDateTime.parse(partes[2] + "-" + partes[3], formatter);

                // Filtrar por la ventana de tiempo relevante
                LocalDateTime fechaHoraGMT0 = convertirAGMT0(fechaHoraI, codigoIATAOrigen);
                if (!fechaHoraGMT0.isBefore(fechaHora) && fechaHoraGMT0.isBefore(fechaHoraFin)) {
                    String[] destinoPaquetes = partes[4].split(":");
                    String codigoIATADestino = destinoPaquetes[0];
                    int cantPaquetes = Integer.parseInt(destinoPaquetes[1]);

                    Envio envio = new Envio(idEnvio, fechaHoraI, 0, codigoIATAOrigen, codigoIATADestino, cantPaquetes,
                            null);
                    List<Paquete> paquetes = new ArrayList<>(cantPaquetes); // Pre-allocate list size
                    for (int i = 0; i < cantPaquetes; i++) {
                        paquetes.add(new Paquete(idEnvio, 0, envio));
                    }
                    envio.setPaquetes(paquetes);
                    envios.add(envio);

                    // Actualizar el almacén del aeropuerto de origen
                    Aeropuerto aeropuerto = aeropuertoMap.get(envio.getCodigoIATAOrigen());
                    if (aeropuerto != null) {
                        Almacen almacen = aeropuerto.getAlmacen();
                        almacen.getPaquetes().addAll(paquetes);
                        almacen.setCantPaquetes(almacen.getCantPaquetes() + cantPaquetes);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Manejo básico de la excepción. Puedes personalizarlo según tus necesidades.
        }

        return envios;
    }

    public boolean guardarEnvios(List<Envio> envios) {
        try {
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // Manejo básico de la excepción. Puedes personalizarlo según tus necesidades.
            return false;
        }
    }

    public LocalDateTime convertirAGMT0(LocalDateTime fechaHora, String codigoIATAOrigen) {
        int zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(codigoIATAOrigen);
        return fechaHora.minusHours(zonaHorariaGMT);
    }

    public static Envio parseDataToFrontend(Envio data) {
        String idEnvio = generateId();
        LocalDateTime fechaHoraOrigen = data.getFechaHoraOrigen();
        int zonaHorariaGMT = data.getZonaHorariaGMT();
        String codigoIATAOrigen = data.getCodigoIATAOrigen();
        String codigoIATADestino = data.getCodigoIATADestino();
        int cantPaquetes = data.getCantPaquetes();

        Envio envio = new Envio(idEnvio, fechaHoraOrigen, zonaHorariaGMT, codigoIATAOrigen, codigoIATADestino,
                cantPaquetes, new ArrayList<>());

        for (int i = 0; i < cantPaquetes; i++) {
            Paquete paquete = new Paquete(idEnvio, i + 1, envio);
            envio.getPaquetes().add(paquete);
        }

        return envio;
    }

    private static String generateId() {
        String[] adjectives = { "Happy", "Sunny", "Brave", "Lucky", "Fierce", "Wise", "Swift", "Glorious", "Mighty",
                "Daring" };
        String[] nouns = { "Eagle", "Lion", "Dragon", "Phoenix", "Tiger", "Bear", "Wolf", "Sword", "Shield", "Star" };

        String adjective = adjectives[(int) (Math.random() * adjectives.length)];
        String noun = nouns[(int) (Math.random() * nouns.length)];
        int number = (int) (Math.random() * 1000);

        return String.format("%s%s%d", adjective, noun, number);
    }
}
