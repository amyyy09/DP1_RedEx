package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import src.global.GlobalVariables;
import src.model.*;
import src.services.PlanificacionService;
import src.services.VueloServices;

@Service
public class ApiServicesDiario {

    @Autowired
    private PlanificacionService planificacionService;

    @Autowired
    private VueloServices vueloService;

    @Autowired
    private AeropuertoService aeropuertoService;

    private static List<Vuelo> vuelosGuardados = new ArrayList<>();
    private static Resumen reportResumen = null;

    private static Map<Paquete, Resultado> jsonprevio = null;
    ResultadoFinal finalD = new ResultadoFinal();

    public ResultadoFinal ejecutarPsoDiario(List<Envio> envios, LocalDateTime fechaHora,
            List<Aeropuerto> aeropuertosGuardados) {
        try {
            if (envios.isEmpty()) {
                LocalDateTime fechaHoraLimite = fechaHora.plusHours(2);
                LocalDateTime fechaHoraReal = fechaHora.plusMinutes(10);
                int zonaHorariaGMT;
                LocalDateTime horaSalidaGMT0;
                List<Vuelo> json = vuelosGuardados;
                List<Vuelo> jsonVuelosActuales = new ArrayList<>();
                List<Vuelo> jsonVuelosProximos = new ArrayList<>();

                for (Vuelo vn : json) {
                    zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(vn.getAeropuertoOrigen());
                    horaSalidaGMT0 = vn.getHoraSalida().minusHours(zonaHorariaGMT);
                    if (horaSalidaGMT0.isAfter(fechaHora) && horaSalidaGMT0.isBefore(fechaHoraLimite)) {
                        jsonVuelosActuales.add(vn);
                    }
                    if (horaSalidaGMT0.isAfter(fechaHoraReal)) {
                        jsonVuelosProximos.add(vn);
                    }
                }
                clearVuelosGuardados();
                envios.clear();
                for (Vuelo vn : jsonVuelosProximos) {
                    vuelosGuardados.add(vn);
                }
                for (Aeropuerto aeropuerto : aeropuertosGuardados) {
                    aeropuerto.getAlmacen().actualizarCantPaquetes();
                }
                finalD.setAeropuertos(aeropuertosGuardados);
                finalD.setVuelos(jsonVuelosActuales);
                return finalD;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonprevio = null;
        List<Vuelo> vuelos = getVuelosGuardados();
        List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream)
                .collect(Collectors.toList());
        Map<Paquete, RutaTiempoReal> resultado = null;
        List<Vuelo> json = null;
        try {
            String archivoRutaPlanes = GlobalVariables.PATH + "planes_vuelo.v4.txt";
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertosGuardados, archivoRutaPlanes);
            List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo, vuelos);
            Map<String, Almacen> almacenes = aeropuertosGuardados.stream()
                    .collect(Collectors.toMap(Aeropuerto::getCodigoIATA, Aeropuerto::getAlmacen));

            System.out.println("Empezando a ejecutar PSO... en el tiempo de ejecución: " + System.currentTimeMillis());
            if (!envios.isEmpty()) {
                resultado = planificacionService.PSO(envios, paquetes, almacenes, planesDeVuelo, aeropuertosGuardados,
                        vuelosActuales, fechaHora);
                jsonprevio = planificacionService.transformResult(resultado);
                json = planificacionService.transformarResultadosDiario(jsonprevio, planesDeVuelo);
                LocalDateTime fechaHoraLimite = fechaHora.plusHours(2);
                LocalDateTime fechaHoraReal = fechaHora.plusHours(10);
                int zonaHorariaGMT;
                LocalDateTime horaSalidaGMT0;
                LocalDateTime horaLlegadaGMT0;
                List<Vuelo> jsonVuelosActuales = new ArrayList<>();
                List<Vuelo> jsonVuelosProximos = new ArrayList<>();

                for (Vuelo vn : json) {
                    zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(vn.getAeropuertoOrigen());
                    horaSalidaGMT0 = vn.getHoraSalida().minusHours(zonaHorariaGMT);
                    horaLlegadaGMT0 = vn.getHoraLlegada().minusHours(zonaHorariaGMT);
                    if (horaSalidaGMT0.isBefore(fechaHoraLimite)) {
                        jsonVuelosActuales.add(vn);
                    }
                    if (horaLlegadaGMT0.isAfter(fechaHoraReal)) {
                        jsonVuelosProximos.add(vn);
                    }
                }
                clearVuelosGuardados();
                envios.clear();
                for (Vuelo vn : jsonVuelosProximos) {
                    vuelosGuardados.add(vn);
                }
                for (Aeropuerto aeropuerto : aeropuertosGuardados) {
                    aeropuerto.getAlmacen().actualizarCantPaquetes();
                }
                finalD.setAeropuertos(aeropuertosGuardados);
                finalD.setVuelos(jsonVuelosActuales);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalD;
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
    }
}
