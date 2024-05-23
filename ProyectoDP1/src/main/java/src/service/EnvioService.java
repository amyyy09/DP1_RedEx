package src.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import src.entity.EnvioEntity;
import src.model.Envio;
import src.repository.EnvioRepository;
import src.repository.PaqueteRepository;

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
    
    public void guardarEnvios (List<Envio> envios) {
        //conversionesModelDTO.convertirEnviosToDTO(envios);
        
        
        // List<EnvioDTO> enviosDTO = conversionesModelDTO.convertirEnviosToDTO(envios);
        // for (EnvioDTO envioDTO : enviosDTO) {
        //     envioRepository.save(envioDTO);
        //     for (PaqueteDTO paqueteDTO : envioDTO.getPaquetes()) {
        //         paqueteRepository.save(paqueteDTO);
        //     }
        // }

        List<EnvioEntity> enviosDTO = envios.convertirEnviosToDTO(envios);

        

        // Guardar todos los envíos en un solo lote
        envioRepository.saveAll(enviosDTO);

        // Guardar todos los paquetes en un solo lote
        for (EnvioEntity envioDTO : enviosDTO) {
            paqueteRepository.saveAll(envioDTO.getPaquetes());
        }
    }
    
}
