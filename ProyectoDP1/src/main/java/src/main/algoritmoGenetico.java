package src.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import src.model.*;
import src.service.*;
import src.utility.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class algoritmoGenetico {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(algoritmoGenetico.class, args);
		PlanificacionService planificacionService = context.getBean(PlanificacionService.class);
		VueloService vueloService = context.getBean(VueloService.class);

		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
			List<Envio> envios = vueloService.getEnvios("Buscar Envios");
			List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, "Buscar Planes de Vuelo");
			List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);

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
}
