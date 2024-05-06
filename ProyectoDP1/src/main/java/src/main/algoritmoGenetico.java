package src.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import src.model.*;
import src.service.*;
import src.utility.*;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class algoritmoGenetico {

	public static void main(String[] args) {
		PlanificacionService planificacionService = new PlanificacionService();
		VueloService vueloService = new VueloService();

		String archivoRutaEnvios = FileUtils.chooseFile("Buscar Envíos");
		String archivoRutaPlanes = FileUtils.chooseFile("Buscar Planes de Vuelo");

		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();//Realizar lectura de datos
			List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
			List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);// en planes de vuelo se tiene la hora y su GMT
			List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo);
			List<Almacen> almacenes = aeropuertos.stream().map(Aeropuerto::getAlmacen).collect(Collectors.toList());
			List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);
			if (!envios.isEmpty()) {
				System.err.println("Inicio de la ejecución del algoritmo genético.");
				Cromosoma resultado = planificacionService.ejecutarAlgoritmoGenetico(envios, aeropuertos, planesDeVuelo,rutasPred,almacenes,vuelosActuales);
				if (resultado != null) {
					System.out.println("Resultado del algoritmo genético procesado.");
				} else {
					System.out.println("No se obtuvo un resultado válido del algoritmo genético.");
				}
			}


			
		} catch (Exception e) {
			System.err.println("Se ha producido un error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
