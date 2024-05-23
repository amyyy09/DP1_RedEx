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
    
    
    
}
