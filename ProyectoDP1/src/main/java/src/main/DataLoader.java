package src.main;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import src.Application;
import src.dto.AeropuertoDTO;
import src.model.*;
import src.service.AeropuertoService;


import src.services.*;
import src.utility.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "src")


public class DataLoader {
    @Autowired
    private AeropuertoService aeropuertoService;

    public static void main(String[] args) {
		
        VueloServices vueloService = new VueloServices();
        ApplicationContext context = SpringApplication.run(Application.class, args);

        AeropuertoService aeropuertoService = context.getBean(AeropuertoService.class);
        //VueloService vueloService = context.getBean(VueloService.class);


        try {
            //carga de aeropuertos
            List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
            //aeropuertoService.saveAllAeropuertos(aeropuertos);

            String archivoRutaEnvios = "ProyectoDP1/src/main/resources/pack_enviado_ZBAA.txt";
                
            List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
        
            List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());

            System.out.println("Paquetes: " + paquetes.size());
            
    
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
}
