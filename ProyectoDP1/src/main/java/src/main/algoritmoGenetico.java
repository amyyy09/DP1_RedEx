package src.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import src.model.*;
import src.service.PlanificacionService;
import src.utility.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class algoritmoGenetico {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(algoritmoGenetico.class, args);
		PlanificacionService planificacionService = context.getBean(PlanificacionService.class);

		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
			List<Envio> envios = obtenerEnvios(planificacionService);
			List<Vuelo> vuelosActuales = obtenerVuelos(planificacionService);

			if (!envios.isEmpty() && !vuelosActuales.isEmpty()) {
				Cromosoma resultado = planificacionService.ejecutarAlgoritmoGenetico(envios, aeropuertos,
						vuelosActuales);
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

	private static List<Envio> obtenerEnvios(PlanificacionService planificacionService) {
		// Suponiendo que hay un método para obtener envíos
		return new ArrayList<>();
	}

	private static List<Vuelo> obtenerVuelos(PlanificacionService planificacionService) {
		// Suponiendo que hay un método para obtener vuelos actuales
		return new ArrayList<>();
	}
}
