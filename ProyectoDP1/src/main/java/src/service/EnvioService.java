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

@Service
public class EnvioService {
    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;
    
    @Autowired
    private ConversionesModelDTO conversionesModelDTO;

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
