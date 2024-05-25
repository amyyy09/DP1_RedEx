package src.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import src.entity.EnvioEntity;
import src.model.Envio;
import src.service.EnvioService;



@RestController
@RequestMapping("/envio")
@CrossOrigin
public class EnvioController {
    
    @Autowired  
    private EnvioService envioService;

    @CrossOrigin
    @GetMapping(value = "/")
    List<Envio> LoadEnvios () throws SQLException {
        List<Envio> resultado = envioService.getEnvios();
        if (resultado == null) {
            throw new SQLException("No se pudo obtener los envios");
        }
        return resultado;
    }
}
