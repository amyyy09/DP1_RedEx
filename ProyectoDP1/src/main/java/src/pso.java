package src;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import src.entity.*;
import src.model.*;
import src.service.*;
import src.utility.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "src")
public class pso {

	public static void main(String[] args) {
		// PlanificacionService planificacionService = new PlanificacionService();
		VueloService vueloService = new VueloService();
		
		// AeropuertoService aeropuertoService = new AeropuertoService();

		ApplicationContext context = SpringApplication.run(RedexApplication.class, args);
        PlanificacionService planificacionService = new PlanificacionService();
        AeropuertoService aeropuertoService = context.getBean(AeropuertoService.class);
		EnvioService envioService = context.getBean(EnvioService.class);
		

		
		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
			//aeropuertoService.saveAllAeropuertos(aeropuertos);
			
			
			System.out.println("Almacenando envios en la base de datos... en el tiempo de ejecución: " + System.currentTimeMillis());
			String archivoRutaEnvios = "ProyectoDP1/src/main/resources/combined.txt";
            String archivoRutaPlanes = "ProyectoDP1/src/main/resources/planes_Vuelo.v3.txt";
			List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
			//envioService.guardarEnvios(envios);
			System.out.println("Envios almacenados en la base de datos. en el tiempo de ejecución: " + System.currentTimeMillis());
            System.out.println("Cantidad de envios: " + envios.size());
            //envios = envios.subList(0, 50);
			 
			List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);
			List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);
            List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream).collect(Collectors.toList());
            System.out.println("Paquetes: " + paquetes.size());
            System.out.println("Empezando a generar rutas predefinidas... en el tiempo de ejecución: " + System.currentTimeMillis());
            List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo);			
            System.out.println("Rutas predefinidas generadas." + System.currentTimeMillis());
            System.out.println("Cantidad de rutas predefinidas: " + rutasPred.size());
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
