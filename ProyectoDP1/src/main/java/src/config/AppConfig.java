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

    @PostConstruct
    public void init() {
        aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
    }

    @Bean
    public List<Aeropuerto> getAeropuertosGuardados() {
        return aeropuertosGuardados;
    }

}