package src.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import src.global.GlobalVariables;
import src.model.*;
import src.repository.EnvioRepository;
import src.service.ApiServices;
import src.service.ApiServicesDiario;
import src.service.EnvioService;
import src.service.TareaProgramadaService;
import src.services.VueloServices;
import src.utility.DatosAeropuertos;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

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
    private VueloServices vueloService;
    @Autowired
    private TareaProgramadaService tareaProgramadaService;
    private List<Envio> enviosDiario=new ArrayList<>();
    private String jsonCompleto=null;
    private int contador=0;
    private PaquetePSOD resultadoDiario = new PaquetePSOD();
    String archivoRutaPlanes = GlobalVariables.PATH + "planes_vuelo.v4.txt";
    private List<Aeropuerto> aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
    private List<PlanDeVuelo> planesDeVuelo=null;
    private ResultadoFinal finalD;
    @PostConstruct
    public void init() {
        try {
            this.planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertosGuardados, archivoRutaPlanes);
        } catch (IOException e) {
            e.printStackTrace();
            this.planesDeVuelo = new ArrayList<>(); // Inicializa con una lista vacía si ocurre un error
        }
    }
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
        actualizarAlmacenes(envios);
        return "Envios registrados exitosamente. Total de envios: " + enviosDiario.size();
    }

    private void actualizarAlmacenes(List<Envio> envios) {
        for (Envio envio : envios) {
            Optional<Aeropuerto> aeropuertoOrigen = aeropuertosGuardados.stream()
                .filter(a -> a.getCodigoIATA().equals(envio.getCodigoIATAOrigen()))
                .findFirst();
            if (aeropuertoOrigen.isPresent()) {
                Almacen almacenOrigen = aeropuertoOrigen.get().getAlmacen();
                if (almacenOrigen != null) {
                    almacenOrigen.setCantPaquetes(almacenOrigen.getCantPaquetes() + envio.getCantPaquetes());
                    almacenOrigen.getPaquetes().addAll(envio.getPaquetes());
                }
            }
        }
    }

    @GetMapping("/iniciar")
    public String iniciarCronometro() {
        if (!tareaProgramadaService.isRunning()) {
            tareaProgramadaService.iniciarTareaProgramada(this::actualizarJsonDiario);
        } 
        LocalDateTime horaSimulada = tareaProgramadaService.getHoraSimulada();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Hora simulada actual: " + horaSimulada.format(formatter);
    }

    @GetMapping("/detener")
    public String detenerCronometro() {
        aeropuertosGuardados = DatosAeropuertos.getAeropuertosInicializados();
        apiServicesDiario.reiniciarTodo();
        tareaProgramadaService.detenerTareaProgramada();
        return "Cronometro detenido.";
    }

    public void actualizarJsonDiario() {
        System.out.println("Ejecutando PSO Diario...");
        finalD = apiServicesDiario.ejecutarPsoDiario(enviosDiario, tareaProgramadaService.getHoraSimulada().plusHours(5), aeropuertosGuardados);
        enviosDiario.clear();
        System.out.println("jsonDiario actualizado: " + finalD);
        contador++;
    }

    @GetMapping("/psoDiario")
    public String psoDiario() {
        try {
            if (finalD != null) {
                // aeropuertosGuardados = finalD.getAeropuertos();
                // LocalDateTime horaSimulada = tareaProgramadaService.getHoraSimulada().plusHours(5);
                // actualizarPaquetes(horaSimulada);
                // finalD.setAeropuertos(aeropuertosGuardados);                
                // jsonDiario = mapper.writeValueAsString(finalD);
                resultadoDiario.setJson(finalD);
                resultadoDiario.setNroEnvio(contador);
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                jsonCompleto = mapper.writeValueAsString(resultadoDiario);
                return jsonCompleto;
            } else {
                return "Aún no hay ejecución";
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.err.println("Error al convertir el objeto a JSON: " + e.getMessage());
            return "Error al procesar JSON: " + e.getMessage();
        }
    }

    private void actualizarPaquetes(LocalDateTime horaSimulada) {
        for (Aeropuerto aeropuerto : aeropuertosGuardados) {
            Almacen almacen = aeropuerto.getAlmacen();
            if (almacen != null) {
                Iterator<Paquete> iterator = almacen.getPaquetes().iterator();
                while (iterator.hasNext()) {
                    Paquete paquete = iterator.next();
                    if (paqueteDebeSalir(paquete, horaSimulada)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private boolean paqueteDebeSalir(Paquete paquete, LocalDateTime horaSimulada) {
    String ruta = paquete.getRuta();
    if (ruta != null && !ruta.isEmpty() && !ruta.contentEquals("No asignada")) {
        String[] indices = ruta.split(";");
        int primerIndice = Integer.parseInt(indices[0]);
        PlanDeVuelo primerPlan = obtenerPlanDeVueloPorIndice(primerIndice);
        if (primerPlan != null) {
            OffsetTime horaSalida = primerPlan.getHoraSalida();
            OffsetTime horaSalidaGMT = horaSalida.withOffsetSameInstant(ZoneOffset.UTC);
            LocalDateTime horaSalidaLocal = horaSimulada.toLocalDate().atTime(horaSalidaGMT.toLocalTime());
            if(horaSalidaLocal.isBefore(tareaProgramadaService.getHorainicio())){
                horaSalidaLocal.plusDays(1);
            }
            return horaSimulada.isAfter(horaSalidaLocal);
        }
    }
    return false;
    }

    private PlanDeVuelo obtenerPlanDeVueloPorIndice(int indice) {
        for (PlanDeVuelo plan : planesDeVuelo) {
            if (plan.getIndexPlan() == indice) {
                return plan;
            }
        }
        return null;
    }
}

