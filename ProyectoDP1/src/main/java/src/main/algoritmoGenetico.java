package src.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import src.model.*;
import src.service.*;
import src.utility.*;

import java.util.List;

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
			
			if (!envios.isEmpty()) {
				Cromosoma resultado = planificacionService.ejecutarAlgoritmoGenetico(envios, aeropuertos, planesDeVuelo);
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
