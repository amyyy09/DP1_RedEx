package src.controllers;

import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import src.entity.RutaPredefinidaEntity;
import src.service.RutaPredefinidaService;

@RestController
@RequestMapping("/rutaPredefinida")
@CrossOrigin
public class RutaPredefinidaController {

    @Autowired
    private RutaPredefinidaService rutaPredefinidaService;

    @PostMapping(value = "/")
    public ResponseEntity<RutaPredefinidaEntity> register(@RequestBody RutaPredefinidaEntity ruta) {
        try {
            RutaPredefinidaEntity resultado = rutaPredefinidaService.register(ruta);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping(value = "/")
    public List<RutaPredefinidaEntity> getAll() {
        return rutaPredefinidaService.getAll();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RutaPredefinidaEntity> getById(@PathVariable Long id) {
        RutaPredefinidaEntity resultado = rutaPredefinidaService.getById(id);
        if (resultado != null) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/")
    public RutaPredefinidaEntity update(@RequestBody RutaPredefinidaEntity ruta) {
        return rutaPredefinidaService.update(ruta);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean wasDeleted = rutaPredefinidaService.delete(id);
        if (wasDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/generate")
    public ResponseEntity<String> generatePredefinedRoutes() {
        try {
            rutaPredefinidaService.generarRutasPredefinidas();
            return ResponseEntity.ok("Predefined routes generated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to generate routes: " + e.getMessage());
        }
    }
}
