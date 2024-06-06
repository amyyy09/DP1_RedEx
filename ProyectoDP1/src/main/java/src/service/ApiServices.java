package src.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import src.model.*;
import src.services.PlanificacionService;
import src.services.VueloServices;
import src.utility.*;


@Service
public class ApiServices {

    @Autowired
    private static PlanificacionService planificacionService;

    @Autowired
    private static VueloServices vueloService;

    @Autowired
    private AeropuertoService aeropuertoService;

    private static List<Vuelo> vuelosGuardados = new ArrayList<>();

    private static List<Aeropuerto> aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
    
    public String ejecutarPso(List <Aeropuerto> modAero, List<Vuelo> vuelos,List<Envio> envios, List<RutaPredefinida> rutasPred,LocalDateTime fechaHora) {
        Map<Paquete, Resultado> jsonprevio= null;
        Map<Paquete, RutaTiempoReal> resultado = null;   
        List<VueloNuevo> json = null;
        planificacionService = new PlanificacionService();
        vueloService = new VueloServices();
        //aeropuertoService = new AeropuertoService();
        String jsonResult = null;
        try {
            String archivoRutaPlanes = "src/main/resources/planes_vuelo.v3.txt";
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertosGuardados, archivoRutaPlanes);//obtencion de planesdevuelos
            List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo,vuelos);
            List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());		
            Map<String, Almacen> almacenes = aeropuertosGuardados.stream()
    			.collect(Collectors.toMap(Aeropuerto::getCodigoIATA, Aeropuerto::getAlmacen));

			for (Envio envio : envios) {
				Aeropuerto aeropuerto = aeropuertosGuardados.stream().filter(a -> a.getCodigoIATA().equals(envio.getCodigoIATAOrigen())).findFirst().orElse(null);
				if (aeropuerto != null) {
					Almacen almacen = aeropuerto.getAlmacen();
					for (Paquete paquete : envio.getPaquetes()) {
						almacen.getPaquetes().add(paquete);
						almacen.setCantPaquetes(almacen.getCantPaquetes() + 1);
					}
				}
			}

			System.out.println("Empezando a ejecutar PSO... en el tiempo de ejecución: " + System.currentTimeMillis());
			if (!envios.isEmpty()) {
				resultado = planificacionService.PSO(envios, paquetes, rutasPred, almacenes, planesDeVuelo, aeropuertosGuardados, vuelosActuales, fechaHora);
                jsonprevio=planificacionService.transformResult(resultado);
                json=planificacionService.transformarResultados(jsonprevio, planesDeVuelo);

                LocalDateTime fechaHoraLimite = fechaHora.plusHours(6);
                int zonaHorariaGMT;
                LocalDateTime horallegadaGMT0;
                LocalDateTime horaSalidaGMT0;
                for (VueloNuevo vn : json) {
                    zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(vn.getAeropuertoDestino());
                    horallegadaGMT0=vn.getHoraLlegada().plusHours(zonaHorariaGMT);
                    if (horallegadaGMT0.isAfter(fechaHora) && horallegadaGMT0.isBefore(fechaHoraLimite)) {
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

                List<VueloNuevo> jsonVuelosActuales = new ArrayList<>();
                List<VueloNuevo> jsonVuelosProximos = new ArrayList<>();
                
                for (VueloNuevo vn : json) {
                    zonaHorariaGMT = aeropuertoService.getZonaHorariaGMT(vn.getAeropuertoOrigen());// la hora salida esta con la hora del origen o destino? si es destino cambiar por vn.getAeropuertoDestino() si es origen cambiarlo a vn.getAeropuertoOrigen()
                    horaSalidaGMT0=vn.getHoraSalida().minusHours(zonaHorariaGMT);
                    if (horaSalidaGMT0.isAfter(fechaHora) && horaSalidaGMT0.isBefore(fechaHoraLimite)) {
                        jsonVuelosActuales.add(vn);
                    } else {
                        jsonVuelosProximos.add(vn);
                    }
                }

                ApiServices.clearVuelosGuardados();
                for (VueloNuevo vn : jsonVuelosProximos) {
                    Vuelo vuelo = new Vuelo();
                    vuelo.setIdVuelo(vn.getIdVuelo());
                    vuelo.setCantPaquetes(vn.getCantPaquetes());
                    vuelo.setCapacidad(vn.getCapacidad());
                    vuelo.setStatus(vn.getStatus());
                    vuelo.setIndexPlan(vn.getIndexPlan());
                    vuelo.setHoraSalida(vn.getHoraSalida());
                    vuelo.setHoraLlegada(vn.getHoraLlegada());
                    vuelosGuardados.add(vuelo);
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

    public static void clearVuelosGuardados() {
        vuelosGuardados.clear();
    }

    public void reiniciarTodo () {
        vuelosGuardados.clear();
        aeropuertosGuardados = new ArrayList<>(DatosAeropuertos.getAeropuertosInicializados());
    }

}