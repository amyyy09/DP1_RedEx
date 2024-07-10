package src.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import src.DAO.PaqueteDAO;
import src.model.*;
import src.service.ApiServices;
import src.service.ApiServicesDiario;
import src.services.VueloServices;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {

    @Autowired
    private ApiServices apiServices;

    @Autowired
    private ApiServicesDiario apiServicesDiario;

    private List<Envio> enviosDiario=new ArrayList<>();

    private String jsonDiario=null;

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
        enviosDiario.addAll(envios);
        return "Envios registrados exitosamente. Total de envios: " + enviosDiario.size();
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void actualizarJsonDiario() {
        if (!enviosDiario.isEmpty()) {
            jsonDiario = apiServicesDiario.ejecutarPsoDiario(enviosDiario);
            enviosDiario.clear();
            System.out.println("jsonDiario actualizado: ");
        }
    }

    @GetMapping("/psoDiario")
    public String psoDiario() {
        if(jsonDiario!=null){
            return jsonDiario;
        }else{
            return "Aún no termina la ejecucion" ;
        }
    }

}

