package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import src.model.*;
import src.utility.*;

@Service
public class ApiService {

    @Autowired
    PlanificacionService planificacionService;

    @Autowired
    VueloService vueloService;

    public Map<Paquete, RutaTiempoReal> ejecutarPso() {
        Map<Paquete, RutaTiempoReal> resultado = null;
        try {
            List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
            String archivoRutaEnvios = "ProyectoDP1/src/main/resources/pack_enviado_ZBAA.txt";
            String archivoRutaPlanes = "ProyectoDP1/src/main/resources/planes_vuelo.v3.txt";
            
            List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
            envios = envios.subList(0, 50);
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);
            List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);
            List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());

            List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo);
            Map<String, Aeropuerto> aeropuertosMap = aeropuertos.stream().collect(Collectors.toMap(Aeropuerto::getCodigoIATA, a -> a));

            for (Envio envio : envios) {
                Aeropuerto aeropuerto = aeropuertosMap.get(envio.getCodigoIATAOrigen());
                if (aeropuerto != null) {
                    aeropuerto.setCantPaquetes(aeropuerto.getCantPaquetes() + envio.getCantPaquetes());
                }
            }

            if (!envios.isEmpty() && !vuelosActuales.isEmpty()) {
                resultado = planificacionService.PSO(envios, paquetes, rutasPred, aeropuertosMap, planesDeVuelo, aeropuertos, vuelosActuales);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }
}