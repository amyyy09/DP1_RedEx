package src.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import src.model.*;
import src.service.*;
import src.utility.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class pso {

	public static void main(String[] args) {
		PlanificacionService planificacionService = new PlanificacionService();
		VueloService vueloService = new VueloService();

		String archivoRutaEnvios = "ProyectoDP1/src/main/resources/pack_enviado_ZBAA.txt";
		String archivoRutaPlanes = "ProyectoDP1/src/main/resources/planes_vuelo.v3.txt";

		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
			List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
            envios = envios.subList(0, 4);
			List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);
			List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);
            List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());
            System.out.println("Paquetes: " + paquetes.size());
            System.out.println("Empezando a generar rutas predefinidas... en el tiempo de ejecución: " + System.currentTimeMillis());
            List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo);
            List<Almacen> almacenes = aeropuertos.stream().map(Aeropuerto::getAlmacen).collect(Collectors.toList());

			for (Envio envio : envios) {
				// add number of packages to the warehouse according to the codigosIATA origin 
				// of the packages in the envio
				Aeropuerto aeropuerto = aeropuertos.stream().filter(a -> a.getCodigoIATA().equals(envio.getCodigoIATAOrigen())).findFirst().orElse(null);
				if (aeropuerto != null) {
					Almacen almacen = aeropuerto.getAlmacen();
					for (Paquete paquete : envio.getPaquetes()) {
						almacen.getPaquetes().add(paquete);
						almacen.setCantPaquetes(almacen.getCantPaquetes() + 1);
					}
				}
			}
			
            System.out.println("Rutas predefinidas generadas." + System.currentTimeMillis());
            System.out.println("Empezando a ejecutar PSO... en el tiempo de ejecución: " );
			if (!envios.isEmpty() && !vuelosActuales.isEmpty()) {
				Map<Paquete, RutaTiempoReal> resultado = planificacionService.PSO(envios, paquetes, rutasPred, almacenes, planesDeVuelo, aeropuertos, vuelosActuales);
				if (resultado != null) {
					System.out.println("Resultado del pso procesado en el tiempo de ejecución: " + System.currentTimeMillis());
				} else {
					System.out.println("No se obtuvo un resultado válido del pso.");
				}
			}
		} catch (Exception e) {
			System.err.println("Se ha producido un error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
