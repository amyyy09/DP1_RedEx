package src.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import src.model.*;
import src.repository.EnvioRepository;
import src.service.ApiServices;
import src.service.ApiServicesDiario;
import src.service.EnvioService;
import src.service.TareaProgramadaService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {
    @Autowired
    private EnvioRepository envioRepository;
    @Autowired
    private ApiServices apiServices;
    @Autowired
    private ApiServicesDiario apiServicesDiario;
    @Autowired
    private EnvioService envioService;
    @Autowired
    private TareaProgramadaService tareaProgramadaService;
    private List<Envio> enviosDiario=new ArrayList<>();
    private String jsonDiario=null;
    private int contador=0;
    private PaquetePSOD resultadoDiario;

    //Simulación Semanal
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

    //Simulación Diaria
    @PostMapping("/registro")
    public String registrarEnvios(@RequestBody PeticionPSOD peticionPSO) {
        List<Envio> envios = peticionPSO.getEnvios();
        List<EnvioEntity> envioEntities = envios.stream()
                .map(envioService::convertToEntity)
                .collect(Collectors.toList());
        envioRepository.saveAll(envioEntities);
        enviosDiario.addAll(envios);
        return "Envios registrados exitosamente. Total de envios: " + enviosDiario.size();
    }

    @GetMapping("/iniciarCronometro")
    public String iniciarCronometro() {
        if (!tareaProgramadaService.isRunning()) {
            tareaProgramadaService.iniciarTareaProgramada(this::actualizarJsonDiario);
            return "Cronometro iniciado.";
        } else {
            LocalDateTime horaSimulada = tareaProgramadaService.getHoraSimulada();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return "Hora simulada actual: " + horaSimulada.format(formatter);
        }
    }

    @PostMapping("/detenerCronometro")
    public String detenerCronometro() {
        tareaProgramadaService.detenerTareaProgramada();
        return "Cronometro detenido.";
    }

    public void actualizarJsonDiario() {
        jsonDiario = apiServicesDiario.ejecutarPsoDiario(enviosDiario);
        enviosDiario.clear();
        System.out.println("jsonDiario actualizado: ");
        contador++;
    }

    @GetMapping("/psoDiario")
    public String psoDiario() {
        if(jsonDiario!=null){
            resultadoDiario.setJson(jsonDiario);
            resultadoDiario.setNroEnvio(contador);
            return jsonDiario;
        }else{
            return "Aún no hay ejecución" ;
        }
    }

}

