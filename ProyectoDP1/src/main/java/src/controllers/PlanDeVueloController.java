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

import src.entity.AeropuertoEntity;
import src.service.AeropuertoService;

@RestController
@RequestMapping("/plandevuelo")
@CrossOrigin
public class PlanDeVueloController {

    @Autowired
    private AeropuertoService aeropuertoService;

    @CrossOrigin
    @PostMapping(value = "/")
    AeropuertoEntity register(@RequestBody AeropuertoEntity aeropuerto) throws SQLException {
        AeropuertoEntity resultado = aeropuertoService.register(aeropuerto);
        if (resultado == null) {
            throw new SQLException("No se pudo registrar el aeropuerto");
        }
        return resultado;
    }

    @CrossOrigin
    @GetMapping(value = "/")
    List<AeropuertoEntity> getAll() throws SQLException {
        List<AeropuertoEntity> resultado = aeropuertoService.getAll();
        if (resultado == null) {
            throw new SQLException("No se pudo obtener los aeropuertos");
        }
        return resultado;
    }

}