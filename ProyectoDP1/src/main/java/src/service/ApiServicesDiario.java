package src.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import src.model.*;
import src.services.PlanificacionService;
import src.services.VueloServices;
import src.utility.*;


@Service
public class ApiServicesDiario {

    @Autowired
    private PlanificacionService planificacionService;

    @Autowired
    private VueloServices vueloService;

    @Autowired
    private AeropuertoService aeropuertoService;

    private static List<Vuelo> vuelosGuardados = new ArrayList<>();

    private static List<Aeropuerto> aeropuertosGuardados;
    ResultadoFinal finalD = new ResultadoFinal();
    public String ejecutarPsoDiario(List<Envio> envios) {
        aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
        List<Vuelo> vuelos = getVuelosGuardados();
        List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());
        Map<Paquete, Resultado> jsonprevio = null;
        Map<Paquete, RutaTiempoReal> resultado = null;
        List<Vuelo> json = null;
        String jsonResult = null;
        LocalDateTime fechaHora= LocalDateTime.now();
        List<PaqueteDTO> paquetesEnvio = null;
        try {
            String archivoRutaPlanes = "ProyectoDP1/src/main/resources/planes_vuelo.v4.txt";
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertosGuardados, archivoRutaPlanes);
            List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo, vuelos);
            Map<String, Almacen> almacenes = aeropuertosGuardados.stream()
                .collect(Collectors.toMap(Aeropuerto::getCodigoIATA, Aeropuerto::getAlmacen));
            
            System.out.println("Empezando a ejecutar PSO... en el tiempo de ejecución: " + System.currentTimeMillis());
            if (!envios.isEmpty()) {
                resultado = planificacionService.PSODiario(envios, paquetes, almacenes, planesDeVuelo, aeropuertosGuardados, vuelosActuales, fechaHora);
                jsonprevio = planificacionService.transformResult(resultado);
                json = planificacionService.transformarResultadosDiario(jsonprevio, planesDeVuelo);
                // paquetesEnvio = PaqueteDTO.fromMap(jsonprevio);
                // PaqueteDAO paqueteDAO = new PaqueteDAO();
                // paqueteDAO.insertPaquetes(paquetesEnvio);

                LocalDateTime fechaHoraLimite = fechaHora.plusHours(6);
                LocalDateTime fechaHoraReal = fechaHora.plusMinutes(10);
                int zonaHorariaGMT;
                LocalDateTime horaSalidaGMT0;

                List<Vuelo> jsonVuelosActuales = new ArrayList<>();
                List<Vuelo> jsonVuelosProximos = new ArrayList<>();

                for (Vuelo vn : json) {
                    zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(vn.getAeropuertoOrigen());
                    horaSalidaGMT0=vn.getHoraSalida().minusHours(zonaHorariaGMT);
                    if (horaSalidaGMT0.isAfter(fechaHora) && horaSalidaGMT0.isBefore(fechaHoraLimite)) {
                        jsonVuelosActuales.add(vn);
                    } 
                    if (horaSalidaGMT0.isAfter(fechaHoraReal)){
                        jsonVuelosProximos.add(vn);
                    }
                }

                clearVuelosGuardados();
                envios.clear();
                for (Vuelo vn : jsonVuelosProximos) {
                    vuelosGuardados.add(vn);
                }
                finalD.setAeropuertos(aeropuertosGuardados);
                finalD.setVuelos(jsonVuelosActuales);
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                jsonResult = mapper.writeValueAsString(finalD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static List<Vuelo> getVuelosGuardados() {
        return vuelosGuardados;
    }

    public static void clearVuelosGuardados() {
        vuelosGuardados.clear();
    }

    public void reiniciarTodo() {
        vuelosGuardados.clear();
        aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
        // PaqueteDAO paqueteDAO = new PaqueteDAO();
        // paqueteDAO.deleteAllPaquetes();
    }
}
