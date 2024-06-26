package src.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import src.model.*;
import src.service.ApiServices;
import src.service.ApiServicesDiario;
import src.service.EnvioService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {

    @Autowired
    private ApiServices apiServices;

    @Autowired
    private ApiServicesDiario apiServicesDiario;

    @PostMapping("/pso")
    public String ejecutarPSO(@RequestBody PeticionPSO peticionPSO) {
        String JSON;
        String fechaHora = peticionPSO.getFechahora();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora, formatter);
        JSON = apiServices.ejecutarPso(fechaHoraParsed);
        
        return JSON;
    }

    @GetMapping("/limpiar")
    public void limpiarPSO() {
        apiServices.reiniciarTodo();
    }


    @PostMapping("/diario")
    public String ejecutarPSO(@RequestBody PeticionPSOD peticionPSO) {
        String JSON;
        List<Envio> envios = peticionPSO.getEnvios();
        List<Envio> enviosProcesados = envios.stream()
                .map(EnvioService::parseDataToFrontend)
                .collect(Collectors.toList());
        JSON = apiServicesDiario.ejecutarPsoDiario(enviosProcesados);
        
        return JSON;
    }
}

