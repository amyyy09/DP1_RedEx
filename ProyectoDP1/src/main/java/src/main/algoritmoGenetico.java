package src.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import src.model.*;
import src.service.*;
import src.utility.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class algoritmoGenetico {

	public static void main(String[] args) {
		PlanificacionService planificacionService = new PlanificacionService();
		VueloService vueloService = new VueloService();

		String archivoRutaEnvios = "ProyectoDP1/src/main/resources/message.txt";//FileUtils.chooseFile("Buscar Envíos");
		String archivoRutaPlanes = "ProyectoDP1/src/main/resources/planes_vuelo.v3.txt";//FileUtils.chooseFile("Buscar Planes de Vuelo");

		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();//Realizar lectura de datos
			List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
			//envios = envios.subList(0, 50);
			List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);// en planes de vuelo se tiene la hora y su GMT
			List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());
            System.out.println("Paquetes: " + paquetes.size());

            //System.out.println("Empezando a generar rutas predefinidas... en el tiempo de ejecución: " + System.currentTimeMillis());
			List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo);
			//System.out.println("Rutas predefinidas generadas." + System.currentTimeMillis());
			//List<Almacen> almacenes = aeropuertos.stream().map(Aeropuerto::getAlmacen).collect(Collectors.toList());
			List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);
			
			//System.out.println("Pasos previos al PSO en el tiempo de ejecución: " + System.currentTimeMillis());
            Map<String, Almacen> almacenes = aeropuertos.stream()
    			.collect(Collectors.toMap(Aeropuerto::getCodigoIATA, Aeropuerto::getAlmacen));
			// 	for (Envio envio : envios) {
			// 		// add number of packages to the warehouse according to the codigosIATA origin 
			// 		// of the packages in the envio
			// 		Aeropuerto aeropuerto = aeropuertos.stream().filter(a -> a.getCodigoIATA().equals(envio.getCodigoIATAOrigen())).findFirst().orElse(null);
			// 		if (aeropuerto != null) {
			// 			Almacen almacen = aeropuerto.getAlmacen();
			// 			for (Paquete paquete : envio.getPaquetes()) {
			// 				almacen.getPaquetes().add(paquete);
			// 				almacen.setCantPaquetes(almacen.getCantPaquetes() + 1);
			// 			}
			// 		}
			// 	}
			for (int i = 0; i < 50; i++) {
				System.err.println("\n-------------------------------------------------");
				System.err.println("\nprueba numero: " + (i+1));
				System.out.println("Empezando a ejecutar genetico.. en el tiempo de ejecución: " + System.currentTimeMillis());
				if (!envios.isEmpty()) {
					System.err.println("Inicio de la ejecución del algoritmo genético.");
					Cromosoma resultado = planificacionService.ejecutarAlgoritmoGenetico(envios, aeropuertos, planesDeVuelo,rutasPred,almacenes,vuelosActuales);
					System.out.println("FInalizando ejecutar genetico.. en el tiempo de ejecución: " + System.currentTimeMillis());
					if (resultado != null) {
						System.out.println("Resultado del genetico procesado en el tiempo de ejecución: " + System.currentTimeMillis());
					} else {
						System.out.println("No se obtuvo un resultado válido del algoritmo genético.");
					}
				}
			}


			
		} catch (Exception e) {
			System.err.println("Se ha producido un error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
