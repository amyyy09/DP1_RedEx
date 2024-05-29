package src.controllers;

import src.model.*;
import src.service.ApiServices;
import src.service.EnvioService;
import src.service.RutaPredefinidaService;

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

    @Autowired
    private RutaPredefinidaService rutaPredefinidaService;

    @CrossOrigin

    @PostMapping("/pso")
    public String ejecutarPSO(@RequestBody PeticionPSO peticionPSO) {
        
        String fechaHora = peticionPSO.getFechahora();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora, formatter);
        List<Aeropuerto> aeropuertos = peticionPSO.getAeropuertos();
        List<Vuelo> vuelos = peticionPSO.getVuelos();
        List<Envio> envios = envioService.getEnviosPorFechaHora(fechaHoraParsed);
        List<RutaPredefinida> rutasPredefinidas = rutaPredefinidaService.getRutasPredefinidas();
        
        ApiServices.ejecutarPso(aeropuertos,vuelos,envios,rutasPredefinidas);

        return "PSO ejecutado con éxito"; // Puedes devolver un resultado más significativo según sea necesario
    }
}
