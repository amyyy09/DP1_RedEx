package pe.edu.pucp.packetsoft.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import pe.edu.pucp.packetsoft.models.Proyecto;
import pe.edu.pucp.packetsoft.services.ProyectoService;

@RestController//Esta anotación indica que la clase UsuarioController es un controlador de Spring que manejará solicitudes HTTP y devolverá respuestas en formato JSON
@RequestMapping("/proyecto")//Esta anotación especifica que todas las rutas manejadas por este controlador estarán bajo el contexto 
@CrossOrigin//Esta anotación permite las solicitudes desde orígenes cruzados (CORS). Esto significa que el controlador puede manejar solicitudes de dominios diferentes al del servidor donde se encuentra la aplicación
public class ProyectoController {
    @Autowired // Esta anotación se utiliza para inyectar una instancia de UsuarioService en el controlador. El UsuarioService generalmente contiene la lógica empresarial relacionada con los usuarios
    private ProyectoService proyectoService;

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/")
    List<Proyecto> getAll(){
        return proyectoService.getAll();
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @GetMapping(value = "/{id}")
    Proyecto get(@PathVariable int id){
        return proyectoService.get(id);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PostMapping(value = "/")
    Proyecto register(@RequestBody Proyecto proyecto) throws SQLException{
        Proyecto resultado = proyectoService.register(proyecto);
        if(resultado == null){
            throw new SQLException();
        }
        return resultado;
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @PutMapping(value = "/")
    Proyecto update(@RequestBody Proyecto proyecto) throws SQLException{
        return proyectoService.update(proyecto);
    }

    @CrossOrigin(origins = "https://proyectaserver.inf.pucp.edu.pe")
    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable int id){
        proyectoService.delete(id);
    }

}
