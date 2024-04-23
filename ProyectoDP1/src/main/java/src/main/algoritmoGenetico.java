package src.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import src.model.*;
import src.controller.*;
import src.utility.*;
import java.util.List;

@SpringBootApplication
public class algoritmoGenetico {

	public static void main(String[] args) {
		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
			String archivoRuta = IOController.chooseFile("Cargar Planes de Vuelo");
			if (archivoRuta != null) {
				List<PlanDeVuelo> planesDeVuelo = IOController.getPlanesDeVuelo(aeropuertos, archivoRuta);
				List<Vuelo> vuelosActuales = IOController.getVuelosActualesTesting(planesDeVuelo);
				// RutaPredefinida.guardarRutasEnCSV(aeropuertos, planesDeVuelo, "rutPred.txt");

				archivoRuta = IOController.chooseFile("Cargar Envios");
				if (archivoRuta != null) {
					List<Envio> envios = IOController.getEnvios(archivoRuta);
					Cromosoma resultado = CromosomaController.ejecutarAlgoritmoGenetico(envios, aeropuertos,
							vuelosActuales);
					if (resultado != null) {
						System.out.println("Resultado del algoritmo genético procesado:");
						IOController.imprimirGenDelCromosoma(resultado);
					} else {
						System.out.println("No se obtuvo un resultado válido del algoritmo genético.");
					}
				} else {
					System.out.println("No se seleccionó ningún archivo de envíos.");
				}
			} else {
				System.out.println("No se seleccionó ningún archivo de planes de vuelo.");
			}
		} catch (Exception e) {
			System.err.println("Se ha producido un error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
