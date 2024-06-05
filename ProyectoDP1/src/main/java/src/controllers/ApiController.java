package src.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import src.model.Aeropuerto;
import src.model.Envio;
import src.model.PeticionPSO;
import src.model.PeticionPSOD;
import src.model.RutaPredefinida;
import src.model.Vuelo;
import src.service.ApiServices;
import src.service.EnvioService;
import src.service.RutaPredefinidaService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {

    @Autowired
    private ApiServices apiServices;

    @Autowired
    private EnvioService envioService;

    @Autowired
    private RutaPredefinidaService rutaPredefinidaService;

    @PostMapping("/pso")
    public String ejecutarPSO(@RequestBody PeticionPSO peticionPSO) {
        String JSON;
        String fechaHora = peticionPSO.getFechahora();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora, formatter);
        List<Aeropuerto> aeropuertos =null;
        List<Vuelo> vuelos = ApiServices.getVuelosGuardados();
        List<Envio> envios = envioService.getEnviosPorFechaHora(fechaHoraParsed);
        List<RutaPredefinida> rutasPredMap = rutaPredefinidaService.getRutasPredefinidas();
        JSON = apiServices.ejecutarPso(aeropuertos, vuelos, envios, rutasPredMap,fechaHoraParsed);
        
        return JSON;
    }

    @GetMapping("/limpiar")
    public void limpiarPSO() {
        apiServices.reiniciarTodo();
    }


    @PostMapping("/diario")
    public String ejecutarPSO(@RequestBody PeticionPSOD peticionPSO) {
        String JSON;
        LocalDateTime fechaHoraParsed = LocalDateTime.now();
        List<Aeropuerto> aeropuertos =null;
        List<Vuelo> vuelos = ApiServices.getVuelosGuardados();
        List<Envio> envios = peticionPSO.getEnvios();
        //Agregar los envios a los envios existentes
        List<RutaPredefinida> rutasPredMap = rutaPredefinidaService.getRutasPredefinidas();
        JSON = apiServices.ejecutarPso(aeropuertos, vuelos, envios, rutasPredMap,fechaHoraParsed);
        
        return JSON;
    }
}

