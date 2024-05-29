package src.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import src.model.*;
import src.services.PlanificacionService;
import src.services.VueloServices;
import src.utility.*;
import src.service.AeropuertoService;

@Service
public class ApiServices {

    @Autowired
    private static PlanificacionService planificacionService;

    @Autowired
    private static VueloServices vueloService;

    public static String ejecutarPso(List <Aeropuerto> modAero, List<Vuelo> vuelos) {
        Map<Paquete, RutaTiempoReal> resultado = null;
        String jsonResult = null;
        try {
            List<Aeropuerto> aeropuertos = AeropuertoService.actualizarAeropuertos(modAero); //actualización de aeropuertos
            String archivoRutaPlanes = "ProyectoDP1/src/main/resources/planes_vuelo.v3.txt";
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);//obtencion de planesdevuelos
            List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo,vuelos);

			String archivoRutaEnvios = "ProyectoDP1/src/main/resources/pack_enviado_ZBAA.txt";   
			List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
            envios = envios.subList(0, 1);
           
            List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());
            List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo);			
            Map<String, Almacen> almacenes = aeropuertos.stream()
    			.collect(Collectors.toMap(Aeropuerto::getCodigoIATA, Aeropuerto::getAlmacen));

			for (Envio envio : envios) {
				Aeropuerto aeropuerto = aeropuertos.stream().filter(a -> a.getCodigoIATA().equals(envio.getCodigoIATAOrigen())).findFirst().orElse(null);
				if (aeropuerto != null) {
					Almacen almacen = aeropuerto.getAlmacen();
					for (Paquete paquete : envio.getPaquetes()) {
						almacen.getPaquetes().add(paquete);
						almacen.setCantPaquetes(almacen.getCantPaquetes() + 1);
					}
				}
			}
			System.out.println("Empezando a ejecutar PSO... en el tiempo de ejecución: " + System.currentTimeMillis());
			if (!envios.isEmpty() && !vuelosActuales.isEmpty()) {
				resultado = planificacionService.PSO(envios, paquetes, rutasPred, almacenes, planesDeVuelo, aeropuertos, vuelosActuales, LocalDateTime.now());
			}
            // Convertir el resultado a JSON
            ObjectMapper objectMapper = new ObjectMapper();
            jsonResult = objectMapper.writeValueAsString(resultado);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }
}