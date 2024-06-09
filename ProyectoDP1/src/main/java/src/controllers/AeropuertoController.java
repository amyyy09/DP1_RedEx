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

import src.dto.AeropuertoDTO;
import src.service.AeropuertoService;


@RestController
@RequestMapping("/aeropuerto")
@CrossOrigin
public class AeropuertoController {
    
    @Autowired
    private AeropuertoService aeropuertoService;

    @CrossOrigin
    @PostMapping(value= "/")
    AeropuertoDTO register(@RequestBody AeropuertoDTO aeropuerto) throws SQLException {
        AeropuertoDTO resultado = aeropuertoService.register(aeropuerto);
        if(resultado == null) {
            throw new SQLException("No se pudo registrar el aeropuerto");
        }
        return resultado;
    }

    @CrossOrigin
    @GetMapping(value= "/")
    List<AeropuertoDTO> getAll() throws SQLException {
        List<AeropuertoDTO> resultado = aeropuertoService.getAll();
        if(resultado == null) {
            throw new SQLException("No se pudo obtener los aeropuertos");
        }
        return resultado;
    }

    @CrossOrigin
    @GetMapping(value= "/{id}")
    AeropuertoDTO getById(@PathVariable Long id) throws SQLException {
        AeropuertoDTO resultado = aeropuertoService.getById(id);
        if(resultado == null) {
            throw new SQLException("No se pudo obtener el aeropuerto");
        }
        return resultado;
    }

    @CrossOrigin
    @PutMapping(value= "/")
    AeropuertoDTO update(@RequestBody AeropuertoDTO aeropuerto) throws SQLException {
        AeropuertoDTO resultado = aeropuertoService.update(aeropuerto);
        if(resultado == null) {
            throw new SQLException("No se pudo actualizar el aeropuerto");
        }
        return resultado;
    }

    @CrossOrigin
    @DeleteMapping(value= "/{id}")
    boolean delete(@PathVariable Long id) throws SQLException {
        boolean resultado = aeropuertoService.delete(id);
        if(!resultado) {
            throw new SQLException("No se pudo eliminar el aeropuerto");
        }
        return resultado;
    }
    
}
