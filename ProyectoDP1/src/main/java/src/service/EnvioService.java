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
import java.util.concurrent.CopyOnWriteArrayList;
import src.service.*;

import javax.annotation.PostConstruct;

@Service
public class EnvioService {

    VueloService vueloService = new VueloService();

    private List<Envio> envios;
    private final String archivoRutaEnvios = "ProyectoDP1/src/main/resources/pack_enviado_ZBAA.txt";

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

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    public List<Envio> getEnvios() {
        return envios;
    }

}
