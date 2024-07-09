package src.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import src.utility.DatosAeropuertos;
import src.global.GlobalVariables;
import src.model.Aeropuerto;
import src.model.PlanDeVuelo;
import src.services.VueloServices;

import javax.annotation.PostConstruct;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

@Configuration
public class AppConfig {

    private List<Aeropuerto> aeropuertosGuardados;
    private List<PlanDeVuelo> planesDeVueloGuardados;
    private final VueloServices vueloService;

    @Autowired
    public AppConfig(VueloServices vueloServices) {
        this.vueloService = vueloServices;
    }

    @PostConstruct
    public void init() {
        aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());

        // Inicializa los planes de vuelo una vez
        String archivoRutaPlanes = GlobalVariables.PATH + "planes_vuelo.v4.txt";
        try {
            planesDeVueloGuardados = vueloService.getPlanesDeVuelo(aeropuertosGuardados, archivoRutaPlanes);
        } catch (IOException e) {
            e.printStackTrace();
            // Maneja la excepción según tu necesidad, puedes lanzar una RuntimeException o loguear el error
            throw new RuntimeException("Error al cargar los planes de vuelo", e);
        }
    }

    @Bean
    public List<Aeropuerto> getAeropuertosGuardados() {
        return aeropuertosGuardados;
    }

    @Bean
    public List<PlanDeVuelo> getPlanesDeVueloGuardados() {
        return planesDeVueloGuardados;
    }

}