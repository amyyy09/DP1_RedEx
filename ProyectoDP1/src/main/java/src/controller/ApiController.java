package src.controller;

import src.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import src.service.PlanificacionService;
import src.service.VueloService;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PlanificacionService planificacionService;
    private final VueloService vueloService;

    @Autowired
    public ApiController(PlanificacionService planificacionService, VueloService vueloService) {
        this.planificacionService = planificacionService;
        this.vueloService = vueloService;
    }

    @PostMapping("/ejecutar-algoritmo-genetico")
    public Cromosoma ejecutarAlgoritmoGenetico(@RequestBody List<Envio> envios,
            @RequestBody List<Aeropuerto> aeropuertos,
            @RequestBody List<Vuelo> vuelosActuales, @RequestBody List<PlanDeVuelo> planesDeVuelo) throws IOException {
        return planificacionService.PSO(envios, aeropuertos, vuelosActuales, planesDeVuelo);
    }

    @GetMapping("/get-vuelos-actuales")
    public List<Vuelo> getVuelosActuales(@RequestBody List<Envio> envios,
            @RequestBody List<Aeropuerto> aeropuertos,
            @RequestBody List<Vuelo> vuelosActuales) throws IOException {
        List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, "ruta-de-tus-planes.csv");
        return vueloService.getVuelosActuales(planesDeVuelo);
    }

    // Puedes agregar más endpoints según tus necesidades para probar otros
    // servicios.
}
