package src.controllers;

import src.model.*;
import src.service.ApiServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

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
}
