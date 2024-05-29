package src.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import src.Application;
import src.dto.AeropuertoDTO;
import src.model.*;
import src.service.AeropuertoService;
import src.service.EnvioService;
import src.service.RutaPredefinidaService;
import src.services.*;
import src.utility.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "src")
public class pso {

	public static void main(String[] args) {
		PlanificacionService planificacionService = new PlanificacionService();
		VueloServices vueloService = new VueloServices();
		//RutaPredefinidaService rutaPredefinidaService = new RutaPredefinidaService();
		
		// AeropuertoService aeropuertoService = new AeropuertoService();

		ApplicationContext context = SpringApplication.run(Application.class, args);

        AeropuertoService aeropuertoService = context.getBean(AeropuertoService.class);
		EnvioService envioService = context.getBean(EnvioService.class);
		RutaPredefinidaService rutaPredefinidaService = context.getBean(RutaPredefinidaService.class);

		
		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
			aeropuertoService.saveAllAeropuertos(aeropuertos);
			
			
			System.out.println("Almacenando envios en la base de datos... en el tiempo de ejecución: " + System.currentTimeMillis());
			String archivoRutaEnvios = "ProyectoDP1/src/main/resources/pack_enviado_ZBAA.txt";
			
			String archivoRutaPlanes = "ProyectoDP1/src/main/resources/planes_vuelo.v3.txt";
			List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
			//envioService.guardarEnvios(envios);
			System.out.println("Envios almacenados en la base de datos. en el tiempo de ejecución: " + System.currentTimeMillis());
            //envios = envios.subList(0, 50);
			 
			List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);
			List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);
            List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());
            System.out.println("Paquetes: " + paquetes.size());
            

			// System.out.println("Empezando a generar rutas predefinidas... en el tiempo de ejecución: " + System.currentTimeMillis());
            // List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo);			
            // System.out.println("Rutas predefinidas generadas." + System.currentTimeMillis());
			
			List<String> destinosEspecificos = Arrays.asList("SKBO","SEQM","SVMI","SBBR","SPIM","SLLP","SCEL","SABE","SGAS","SUAA","LATI", "EDDI","LOWW","EBCI","UMMS","LBSF","LKPR","LDZA","EKCH","EHAM","VIDP","RKSI", "VTBS","OMDB","ZBAA","RJTT","WMKK","WSSS","WIII","RPLL"); //para nuestro experimento tenemos solo un aeropuerto destino WMKK
			for (String destino : destinosEspecificos) {
				List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo, destino);
				System.out.println("Rutas predefinidas para el destino " + destino );
				vueloService.guardarRutasEnCSV(rutasPred, "ProyectoDP1/src/main/resources/rutas_predefinidas_" + destino + ".csv");
			}
			
			//System.out.println("Rutas predefinidas almacenadas en el archivo rutas_predefinidas.csv." );
			
			// List<RutaPredefinida> rutasPred3 = rutaPredefinidaService.getRutasPredefinidas();

			// System.out.println("leyendo el archivo rutaspred... en el tiempo de ejecución: " + System.currentTimeMillis());
            // List<RutaPredefinida> rutasPred2 = rutaPredefinidaService.cargarRutas("ProyectoDP1/src/main/resources/rutas_predefinidas.csv");		
            // System.out.println("lecturas de rutas pred en." + System.currentTimeMillis());

			System.out.println("cierre");
			
			
			/* 
			System.out.println("Pasos previos al PSO en el tiempo de ejecución: " + System.currentTimeMillis());
            Map<String, Almacen> almacenes = aeropuertos.stream()
    			.collect(Collectors.toMap(Aeropuerto::getCodigoIATA, Aeropuerto::getAlmacen));

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
			for (int i = 0; i < 25; i++) {
				System.out.println("Empezando a ejecutar PSO... en el tiempo de ejecución: " + System.currentTimeMillis());
				if (!envios.isEmpty() && !vuelosActuales.isEmpty()) {
					Map<Paquete, RutaTiempoReal> resultado = planificacionService.PSO(envios, paquetes, rutasPred, almacenes, planesDeVuelo, aeropuertos, vuelosActuales);
					if (resultado != null) {
						System.out.println("Resultado del pso procesado en el tiempo de ejecución: " + System.currentTimeMillis());
					} else {
						System.out.println("No se obtuvo un resultado válido del pso.");
					}
				}
			}
			*/
		} catch (Exception e) {
			System.err.println("Se ha producido un error: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
}
