package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import src.dto.EnvioDTO;
import src.dto.PaqueteDTO;
import src.model.Envio;
import src.repository.EnvioRepository;
import src.repository.PaqueteRepository;
import src.utility.ConversionesModelDTO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
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

    private List<Envio> envios;
    private final String archivoRutaEnvios = "ProyectoDP1/src/main/resources/combined.txt" ;

    @PostConstruct
    public void init() {
        envios = new CopyOnWriteArrayList<>();
        try {
            envios=vueloService.getEnvios(archivoRutaEnvios);
            System.out.println("Se cargaron los envios");
        } catch (IOException e) {
            // Maneja la excepción de acuerdo a tus necesidades
            System.err.println("Error al cargar los envíos: " + e.getMessage());
        }
    }

    public List<Envio> getEnvios() {
        return envios;
    }

    public List<Envio> getEnviosPorFechaHora(LocalDateTime fechaHora) {
        LocalDateTime fechaHoraFin = fechaHora.plusMinutes(20);
        return envios.stream()
                     .filter(envio -> !envio.getFechaHoraOrigen().isBefore(fechaHora) && envio.getFechaHoraOrigen().isBefore(fechaHoraFin))
                     .collect(Collectors.toList());
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