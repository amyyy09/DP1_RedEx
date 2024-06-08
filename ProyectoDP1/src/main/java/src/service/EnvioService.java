package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import src.dto.EnvioDTO;
import src.dto.PaqueteDTO;
import src.repository.EnvioRepository;
import src.repository.PaqueteRepository;
import src.utility.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import src.services.*;
import src.model.*;

import javax.annotation.PostConstruct;

@Service
public class EnvioService {
    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    VueloServices vueloService = new VueloServices();
    
    @Autowired
    private AeropuertoService aeropuertoService;

    private List<Envio> envios;
    private final String archivoRutaEnvios = "ProyectoDP1/src/main/resources/combined.txt" ;

    public List<Envio> getEnvios() {
        return envios;
    }

    public List<Envio> getEnviosPorFechaHora(LocalDateTime fechaHora, List<Aeropuerto> aeropuertosGuardados) {
        LocalDateTime fechaHoraFin = fechaHora.plusMinutes(40);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");
        List<Envio> envios = new ArrayList<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(archivoRutaEnvios))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] partes = line.split("-");
                if (partes.length < 5) continue; // Skip malformed lines
    
                String codigoIATAOrigen = partes[0];
                String idEnvio = partes[1];
                LocalDateTime fechaHoraI = LocalDateTime.parse(partes[2] + "-" + partes[3], formatter);
    
                // Filtrar por la ventana de tiempo relevante
                LocalDateTime fechaHoraGMT0 = convertirAGMT0(fechaHoraI, codigoIATAOrigen);
                if (!fechaHoraGMT0.isBefore(fechaHora) && fechaHoraGMT0.isBefore(fechaHoraFin)) {
                    String[] destinoPaquetes = partes[4].split(":");
                    String codigoIATADestino = destinoPaquetes[0];
                    int cantPaquetes = Integer.parseInt(destinoPaquetes[1]);
    
                    Envio envio = new Envio(idEnvio, fechaHoraI, 0, codigoIATAOrigen, codigoIATADestino, cantPaquetes, null);
                    List<Paquete> paquetes = new ArrayList<>(cantPaquetes); // Pre-allocate list size
                    for (int i = 0; i < cantPaquetes; i++) {
                        paquetes.add(new Paquete(idEnvio, 0, envio));
                    }
                    envio.setPaquetes(paquetes);
                    envios.add(envio);
    
                    // Actualizar el almacén del aeropuerto de origen
                    Aeropuerto aeropuerto = aeropuertosGuardados.stream()
                        .filter(a -> a.getCodigoIATA().equals(envio.getCodigoIATAOrigen()))
                        .findFirst()
                        .orElse(null);
                    if (aeropuerto != null) {
                        Almacen almacen = aeropuerto.getAlmacen();
                        for (Paquete paquete : envio.getPaquetes()) {
                            almacen.getPaquetes().add(paquete);
                            almacen.setCantPaquetes(almacen.getCantPaquetes() + 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Manejo básico de la excepción. Puedes personalizarlo según tus necesidades.
        }
    
        return envios;
    }
    

    public LocalDateTime convertirAGMT0(LocalDateTime fechaHora, String codigoIATAOrigen) {
        int zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(codigoIATAOrigen);
        return fechaHora.minusHours(zonaHorariaGMT);
    }
    
}
