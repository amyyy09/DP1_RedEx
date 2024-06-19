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
    List<Envio> LoadEnvios() throws SQLException {
        List<Envio> resultado = envioService.getEnvios();
        if (resultado == null) {
            throw new SQLException("No se pudo obtener los envios");
        }
        return resultado;
    }

    // @GetMapping("/filtrar")
    // public List<Envio> getEnviosByFechaHora(@RequestParam String fechaHora) {
    // DateTimeFormatter formatter =
    // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    // LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora, formatter);
    // return envioService.getEnviosPorFechaHora(fechaHoraParsed);

    @CrossOrigin
    @PostMapping("/guardarLista")
    public String guardarEnvios(@RequestBody List<Envio> envios) throws SQLException {
        boolean resultado = envioService.guardarEnvios(envios);
        if (!resultado) {
            throw new SQLException("No se pudieron guardar los envios");
        }
        return "Envios guardados exitosamente";
    }
}
