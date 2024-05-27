package src.controllers;

import src.model.*;
import src.service.ApiServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class api {

    @Autowired
    private ApiServices psoService;

    @CrossOrigin
    @GetMapping(value= "/pso")
    String ejecutarPso() {
        return psoService.ejecutarPso();
    }

    @PostMapping("/pso")
    public String ejecutarPSO(@RequestBody PeticionPSO peticionPSO) {
        // Convertir fecha y hora a LocalDateTime
        String fechaHora = peticionPSO.getFechahora();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora, formatter);

        

        // Aquí pasarías los datos de envíos, almacenes y vuelos al algoritmo PSO, también la fecha y hora
        // Por ejemplo:
        // psoAlgorithm.ejecutar(fechaHoraParsed, envios, peticionPSO.getAlmacenes(), peticionPSO.getVuelos());

        return "PSO ejecutado con éxito"; // Puedes devolver un resultado más significativo según sea necesario
    }
}
