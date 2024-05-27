package src.controllers;

import src.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import src.service.ApiService;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {

    @Autowired
    ApiService psoService;

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, value = "/ejecutarPso")
    @ResponseBody
    public Map<Paquete, RutaTiempoReal> ejecutarPso() {
        return psoService.ejecutarPso();
    }
}