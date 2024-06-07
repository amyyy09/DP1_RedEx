package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import src.dto.EnvioDTO;
import src.dto.PaqueteDTO;
import src.repository.EnvioRepository;
import src.repository.PaqueteRepository;
import src.utility.ConversionesModelDTO;
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
    
    @Autowired
    private ConversionesModelDTO conversionesModelDTO;

    VueloServices vueloService = new VueloServices();
    
    @Autowired
    private AeropuertoService aeropuertoService;

    private List<Envio> envios;
    private final String archivoRutaEnvios = "src/main/resources/combined.txt" ;

    public List<Envio> getEnvios() {
        return envios;
    }

    public List<Envio> getEnviosPorFechaHora(LocalDateTime fechaHora) {
        LocalDateTime fechaHoraFin = fechaHora.plusMinutes(40);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");
        List<Envio> envios = new ArrayList<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(archivoRutaEnvios))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] partes = line.split("-");
                String codigoIATAOrigen = partes[0];
                String idEnvio = partes[1];
                LocalDateTime fechaHoraI = LocalDateTime.parse(partes[2] + "-" + partes[3], formatter);
    
                // Filtrar por la ventana de tiempo relevante
                LocalDateTime fechaHoraGMT0 = convertirAGMT0(fechaHoraI, codigoIATAOrigen);
                if (!fechaHoraGMT0.isBefore(fechaHora) && fechaHoraGMT0.isBefore(fechaHoraFin)) {
                    String codigoIATADestino = partes[4].split(":")[0];
                    int cantPaquetes = Integer.parseInt(partes[4].split(":")[1]);
    
                    Envio envio = new Envio(idEnvio, fechaHoraI, 0, codigoIATAOrigen,
                            codigoIATADestino, cantPaquetes, null);
    
                    List<Paquete> paquetes = new ArrayList<>();
                    for (int i = 0; i < cantPaquetes; i++) {
                        paquetes.add(new Paquete(idEnvio, 0, envio));
                    }
    
                    envio.setPaquetes(paquetes);
    
                    envios.add(envio);
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

    public void guardarEnvios (List<Envio> envios) {
        //conversionesModelDTO.convertirEnviosToDTO(envios);
        
        
        // List<EnvioDTO> enviosDTO = conversionesModelDTO.convertirEnviosToDTO(envios);
        // for (EnvioDTO envioDTO : enviosDTO) {
        //     envioRepository.save(envioDTO);
        //     for (PaqueteDTO paqueteDTO : envioDTO.getPaquetes()) {
        //         paqueteRepository.save(paqueteDTO);
        //     }
        // }

        List<EnvioDTO> enviosDTO = conversionesModelDTO.convertirEnviosToDTO(envios);

        // Guardar todos los envíos en un solo lote
        envioRepository.saveAll(enviosDTO);

        // Guardar todos los paquetes en un solo lote
        for (EnvioDTO envioDTO : enviosDTO) {
            paqueteRepository.saveAll(envioDTO.getPaquetes());
        }
    }
    
}
