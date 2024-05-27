package src.controllers;

import src.model.*;
import src.service.ApiServices;
import src.service.EnvioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class api {

    @Autowired
    private ApiServices psoService;

    @Autowired  
    private EnvioService envioService;

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

        List<Envio> envios = envioService.getEnviosPorFechaHora(fechaHoraParsed);

        // Aquí pasarías los datos de envíos, almacenes y vuelos al algoritmo PSO
        // Por ejemplo:
        // psoAlgorithm.ejecutar(envios, peticionPSO.getAlmacenes(), peticionPSO.getVuelos());

        return "PSO ejecutado con éxito"; // Puedes devolver un resultado más significativo según sea necesario
    }
}
