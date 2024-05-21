package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.entity.Aeropuerto;
import src.service.AeropuertoService;

import java.util.List;

@RestController
@RequestMapping("/api/aeropuertos")
public class AeropuertoController {

    @Autowired
    private AeropuertoService aeropuertoService;

    @GetMapping
    public List<Aeropuerto> getAllAeropuertos() {
        return aeropuertoService.getAllAeropuertos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aeropuerto> getAeropuertoById(@PathVariable Long id) {
        return aeropuertoService.getAeropuertoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Aeropuerto createAeropuerto(@RequestBody Aeropuerto aeropuerto) {
        return aeropuertoService.saveAeropuerto(aeropuerto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aeropuerto> updateAeropuerto(@PathVariable Long id,
            @RequestBody Aeropuerto aeropuertoDetails) {
        return ResponseEntity.ok(aeropuertoService.updateAeropuerto(id, aeropuertoDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAeropuerto(@PathVariable Long id) {
        aeropuertoService.deleteAeropuerto(id);
        return ResponseEntity.noContent().build();
    }
}
