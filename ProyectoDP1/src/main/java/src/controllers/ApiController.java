package src.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import src.DAO.PaqueteDAO;
import src.dto.VueloDTO;
import src.global.GlobalVariables;
import src.model.*;
import src.service.ApiServices;
import src.service.ApiServicesDiario;
import src.service.EnvioService;
import src.services.VueloServices;
import src.utility.DatosAeropuertos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiController {

    @Autowired
    private ApiServices apiServices;

    @Autowired
    private ApiServicesDiario apiServicesDiario;

    @Autowired
    private PaqueteDAO paqueteDAO;

    @Autowired
    private VueloServices vueloService;

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

    @GetMapping("/paquete/{idPaquete}")
    public String getVuelosByPaqueteId(@PathVariable String idPaquete) {
    String vuelos = paqueteDAO.getVuelosByIdPaquete(idPaquete);
    if (vuelos != null) {
        try {
            List<VueloDTO> vueloDTOs = parseVuelos(vuelos);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(vueloDTOs);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Error processing JSON\"}";
        }
    } else {
        return "{\"error\": \"Paquete not found\"}";
    }
    }
    
    private List<VueloDTO> parseVuelos(String vuelos) {
        List<VueloDTO> vueloDTOs = new ArrayList<>();
        String[] vuelosArray = vuelos.split("->");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String archivoRutaPlanes = GlobalVariables.PATH + "planes_vuelo.v4.txt";
        List<Aeropuerto> aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
    
        try {
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertosGuardados, archivoRutaPlanes);
            Map<Integer, PlanDeVuelo> planDeVueloMap = planesDeVuelo.stream()
                    .collect(Collectors.toMap(PlanDeVuelo::getIndexPlan, plan -> plan));
    
            for (String vuelo : vuelosArray) {
                String[] vueloData = vuelo.split("-", 2);
                if (vueloData.length == 2) {
                    VueloDTO vueloDTO = new VueloDTO();
                    vueloDTO.setIndexPlan(Integer.parseInt(vueloData[0]));
                    LocalDate fechaSalida = LocalDate.parse(vueloData[1], formatter);
                    
                    PlanDeVuelo planDeVueloS = planDeVueloMap.get(vueloDTO.getIndexPlan());
                    if (planDeVueloS != null) {
                        // Sumar hora de salida a la fecha de salida
                        LocalDateTime fechaSalidaConHora = fechaSalida.atTime(planDeVueloS.getHoraSalida().toLocalTime());
                        vueloDTO.setFechaSalida(fechaSalidaConHora);
                        
                        // Calcular la fecha de llegada
                        LocalDateTime fechaLlegadaConHora = fechaSalida.atTime(planDeVueloS.getHoraLlegada().toLocalTime());
                        if (fechaLlegadaConHora.isBefore(fechaSalidaConHora)) {
                            fechaLlegadaConHora = fechaLlegadaConHora.plusDays(1);
                        }
                        vueloDTO.setFechaLLegada(fechaLlegadaConHora);
                        
                        vueloDTO.setAeropuertoDestino(planDeVueloS.getCodigoIATADestino());
                        vueloDTO.setAeropuertoSalida(planDeVueloS.getCodigoIATAOrigen());
                    } else {
                        System.err.println("Plan de vuelo no encontrado para indexPlan: " + vueloDTO.getIndexPlan());
                    }
    
                    vueloDTOs.add(vueloDTO);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return vueloDTOs;
    }
}

