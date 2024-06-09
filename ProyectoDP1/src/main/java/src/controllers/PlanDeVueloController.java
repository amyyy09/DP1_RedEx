package src.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import src.dto.PlanDeVueloDTO;
import src.service.PlanDeVueloService;

@RestController
@RequestMapping("/planDeVuelo")
@CrossOrigin
public class PlanDeVueloController {
    @Autowired
    private PlanDeVueloService planDeVueloService;

    @CrossOrigin
    @PostMapping(value= "/")
    PlanDeVueloDTO register(@RequestBody PlanDeVueloDTO planDeVuelo) throws SQLException {
        PlanDeVueloDTO resultado = planDeVueloService.register(planDeVuelo);
        if(resultado == null) {
            throw new SQLException("No se pudo registrar el plan de vuelo");
        }
        return resultado;
    }

    @CrossOrigin
    @GetMapping(value= "/")
    List<PlanDeVueloDTO> getAll() throws SQLException {
        List<PlanDeVueloDTO> resultado = planDeVueloService.getAll();
        if(resultado == null) {
            throw new SQLException("No se pudo obtener los planes de vuelo");
        }
        return resultado;
    }
}
