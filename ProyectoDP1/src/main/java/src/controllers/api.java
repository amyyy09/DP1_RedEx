package src.controllers;

import src.model.*;
import src.service.ApiServices;
import src.service.EnvioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class api {

    @Autowired
    private ApiServices psoService;

    @Autowired
    private EnvioService envioService;

    @CrossOrigin

    @PostMapping("/pso")
    public String ejecutarPSO(@RequestBody PeticionPSO peticionPSO) {
        // Convertir fecha y hora a LocalDateTime
        String fechaHora = peticionPSO.getFechahora();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora, formatter);
        List<Aeropuerto> aeropuertos = peticionPSO.getAeropuertos();
        List<Vuelo> vuelos = peticionPSO.getVuelos();
        List<Envio> envios = envioService.getEnviosPorFechaHora(fechaHoraParsed);

        ApiServices.ejecutarPso(aeropuertos);
        // Aquí pasarías los datos de envíos, almacenes y vuelos al algoritmo PSO, también la fecha y hora
        // Por ejemplo:
        // psoAlgorithm.ejecutar(fechaHoraParsed, envios, peticionPSO.getAlmacenes(), peticionPSO.getVuelos());

        return "PSO ejecutado con éxito"; // Puedes devolver un resultado más significativo según sea necesario
    }
}
