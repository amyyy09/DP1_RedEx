package src.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import src.model.*;
import src.service.ApiServices;
import src.service.ApiServicesDiario;
import src.service.EnvioService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {

    @Autowired
    private ApiServices apiServices;

    @Autowired
    private ApiServicesDiario apiServicesDiario;

    @PostMapping("/pso")
    public String ejecutarPSO(@RequestBody PeticionPSO peticionPSO) {
        String JSON;
        String fechaHora = peticionPSO.getFechahora();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime fechaHoraParsed = LocalDateTime.parse(fechaHora, formatter);
        JSON = apiServices.ejecutarPso(fechaHoraParsed);

        return JSON;
    }

    @GetMapping("/limpiar")
    public void limpiarPSO() {
        apiServices.reiniciarTodo();
    }

    @GetMapping("/reporte")
    public String reporteSemanal() {
        try {
            Resumen reporte = ApiServices.getReportesResumen();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String jsonResult = mapper.writeValueAsString(reporte);
            return jsonResult;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Error processing JSON\"}";
        }
    }

    @PostMapping("/diario")
    public String ejecutarPSO(@RequestBody PeticionPSOD peticionPSO) {
        try {
            List<Envio> enviosProcesados = peticionPSO.getEnvios().stream()
                    .map(EnvioService::parseDataToFrontend)
                    .collect(Collectors.toList());

            String JSON = apiServicesDiario.ejecutarPsoDiario(enviosProcesados);
            return JSON;
        } catch (Exception e) {
            return "{\"error\": \"An error occurred while processing the request.\"}";
        }
    }
}
