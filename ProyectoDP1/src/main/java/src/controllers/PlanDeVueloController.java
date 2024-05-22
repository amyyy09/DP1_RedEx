package src.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import src.entity.PlanDeVueloEntity;
import src.service.PlanDeVueloService;

@RestController
@RequestMapping("/plandevuelo")
@CrossOrigin
public class PlanDeVueloController {

    @Autowired
    private PlanDeVueloService planDeVuelo;

    @CrossOrigin
    @PostMapping(value = "/")
    PlanDeVueloEntity register(@RequestBody PlanDeVueloEntity plan) throws SQLException {
        PlanDeVueloEntity resultado = planDeVuelo.register(plan);
        if (resultado == null) {
            throw new SQLException("No se pudo registrar el aeropuerto");
        }
        return resultado;
    }

    @CrossOrigin
    @GetMapping(value = "/")
    List<PlanDeVueloEntity> getAll() throws SQLException {
        List<PlanDeVueloEntity> resultado = planDeVuelo.getAll();
        if (resultado == null) {
            throw new SQLException("No se pudo obtener los aeropuertos");
        }
        return resultado;
    }

}