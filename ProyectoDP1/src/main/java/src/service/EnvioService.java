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
        LocalDateTime fechaHoraFin = fechaHora.plusMinutes(50);
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
                String idEnvio = partes[0] + partes[1];
                LocalDateTime fechaHoraI = LocalDateTime.parse(partes[2] + "-" + partes[3], formatter);

                // Filtrar por la ventana de tiempo relevante
                LocalDateTime fechaHoraGMT0 = convertirAGMT0(fechaHoraI, codigoIATAOrigen);
                if (!fechaHoraGMT0.isBefore(fechaHora) && fechaHoraGMT0.isBefore(fechaHoraFin)) {
                    String[] destinoPaquetes = partes[4].split(":");
                    String codigoIATADestino = destinoPaquetes[0];
                    int cantPaquetes = Integer.parseInt(destinoPaquetes[1]);

                    Envio envio = new Envio(idEnvio, fechaHoraI, 0, codigoIATAOrigen, codigoIATADestino, cantPaquetes,
                            null);
                    List<Paquete> paquetes = new ArrayList<>(cantPaquetes);
                    for (int i = 1; i < cantPaquetes+1; i++) {
                        String paqueteId = idEnvio + "-" + i;
                        paquetes.add(new Paquete(paqueteId, 0, fechaHoraI,codigoIATAOrigen,codigoIATADestino,null));
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

    public LocalDateTime convertirAGMT0(LocalDateTime fechaHora, String codigoIATAOrigen) {
        int zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(codigoIATAOrigen);
        return fechaHora.minusHours(zonaHorariaGMT);
    }

    public static Envio parseDataToFrontend(Envio envio) {
        for (int i = 0; i < envio.getCantPaquetes(); i++) {
            String nombrePaquete = envio.getIdEnvio() + "-" + (i + 1);
            Paquete paquete = new Paquete(nombrePaquete, 0, envio.getFechaHoraOrigen(),envio.getCodigoIATAOrigen(),envio.getCodigoIATADestino(),null);
            envio.getPaquetes().add(paquete);
        }

        return envio;
    }

    public EnvioEntity convertToEntity(Envio envio) {
        EnvioEntity envioEntity = new EnvioEntity();
        envioEntity.setIdEnvio(envio.getIdEnvio());
        envioEntity.setFechaHoraOrigen(envio.getFechaHoraOrigen());
        envioEntity.setZonaHorariaGMT(envio.getZonaHorariaGMT());
        envioEntity.setCodigoIATAOrigen(envio.getCodigoIATAOrigen());
        envioEntity.setCodigoIATADestino(envio.getCodigoIATADestino());
        envioEntity.setCantPaquetes(envio.getCantPaquetes());

        // Convertir paquetes
        List<PaqueteEntity> paqueteEntities = envio.getPaquetes().stream()
                .map(paquete -> {
                    PaqueteEntity paqueteEntity = new PaqueteEntity();
                    paqueteEntity.setIdentificacion(paquete.getID());
                    paqueteEntity.setStatus(paquete.getStatus());
                    paqueteEntity.setHoraInicio(paquete.getHoraInicio());
                    paqueteEntity.setAeropuertoOrigen(paquete.getAeropuertoOrigen());
                    paqueteEntity.setAeropuertoDestino(paquete.getAeropuertoDestino());
                    paqueteEntity.setRuta(paquete.getRuta());
                    paqueteEntity.setEnvio(envioEntity);
                    return paqueteEntity;
                })
                .collect(Collectors.toList());
        envioEntity.setPaquetes(paqueteEntities);

        return envioEntity;
    }
}
