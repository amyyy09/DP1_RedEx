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

import src.DAO.PaqueteDAO;
import src.global.GlobalVariables;
import src.model.*;
import src.services.PlanificacionService;
import src.services.VueloServices;
import src.utility.*;


@Service
public class ApiServices {

    @Autowired
    private PlanificacionService planificacionService;

    @Autowired
    private VueloServices vueloService;

    @Autowired
    private EnvioService envioService;

    @Autowired
    private AeropuertoService aeropuertoService;

    private static List<Vuelo> vuelosGuardados = new ArrayList<>();
    private static Resumen reportResumen = null;

    private static List<Aeropuerto> aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
    public String ejecutarPso(LocalDateTime fechaHora) {
        List<Vuelo> vuelos = getVuelosGuardados();
        List<Envio> envios = envioService.getEnviosPorFechaHora(fechaHora, aeropuertosGuardados);
        List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());
        Map<Paquete, Resultado> jsonprevio = null;
        Map<Paquete, RutaTiempoReal> resultado = null;
        List<Vuelo> json = null;
        String jsonResult = null;
        
        try {
            String archivoRutaPlanes =  GlobalVariables.PATH + "planes_vuelo.v3.txt";
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertosGuardados, archivoRutaPlanes);
            List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo, vuelos);
            Map<String, Almacen> almacenes = aeropuertosGuardados.stream()
                .collect(Collectors.toMap(Aeropuerto::getCodigoIATA, Aeropuerto::getAlmacen));

            System.out.println("Empezando a ejecutar PSO... en el tiempo de ejecución: " + System.currentTimeMillis());
            if (!envios.isEmpty()) {
                resultado = planificacionService.PSO(envios, paquetes, almacenes, planesDeVuelo, aeropuertosGuardados, vuelosActuales, fechaHora);
                jsonprevio = planificacionService.transformResult(resultado);
                json = planificacionService.transformarResultadosDiario(jsonprevio, planesDeVuelo);
                reportResumen = planificacionService.generarResumen(jsonprevio,planesDeVuelo);
                

                LocalDateTime fechaHoraLimite = fechaHora.plusHours(6);
                LocalDateTime fechaHoraReal = fechaHora.plusHours(1);
                int zonaHorariaGMT;
                LocalDateTime horallegadaGMT0;
                LocalDateTime horaSalidaGMT0;
                for (Vuelo vn : json) {
                    zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(vn.getAeropuertoDestino());
                    horallegadaGMT0=vn.getHoraLlegada().plusHours(zonaHorariaGMT);
                    if (horallegadaGMT0.isAfter(fechaHora) && horallegadaGMT0.isBefore(fechaHoraReal)) {
                        Aeropuerto aeropuertoDestino = aeropuertosGuardados.stream()
                            .filter(a -> a.getCodigoIATA().equals(vn.getAeropuertoDestino()))
                            .findFirst()
                            .orElse(null);

                        if (aeropuertoDestino != null) {
                            Almacen almacen = aeropuertoDestino.getAlmacen();
                            almacen.setCantPaquetes(almacen.getCantPaquetes() + vn.getCantPaquetes());
                        }
                    }
                }

                List<Vuelo> jsonVuelosActuales = new ArrayList<>();
                List<Vuelo> jsonVuelosProximos = new ArrayList<>();

                for (Vuelo vn : json) {
                    zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(vn.getAeropuertoOrigen());// la hora salida esta con la hora del origen o destino? si es destino cambiar por vn.getAeropuertoDestino() si es origen cambiarlo a vn.getAeropuertoOrigen()
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

                // Convertir el resultado a JSON
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                jsonResult = mapper.writeValueAsString(jsonVuelosActuales);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static List<Vuelo> getVuelosGuardados() {
        return vuelosGuardados;
    }

    public static Resumen getReportesResumen() {
        return reportResumen;
    }

    public static void clearVuelosGuardados() {
        vuelosGuardados.clear();
    }

    public void reiniciarTodo() {
        vuelosGuardados.clear();
        aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
    }
}
