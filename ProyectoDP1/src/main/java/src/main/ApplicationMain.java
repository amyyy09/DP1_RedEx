package src.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import src.model.*;
import src.service.*;
import src.utility.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class ApplicationMain {

    public static void main(String[] args) {
        PlanificacionService planificacionService = new PlanificacionService();
        VueloService vueloService = new VueloService();

        // Lógica para algoritmoGenetico
        System.out.println("Iniciando algoritmo genético...");
        ejecutarAlgoritmoGenetico(planificacionService, vueloService);

        // Lógica para PSO
        System.out.println("Iniciando PSO...");
        ejecutarPSO(planificacionService, vueloService);
    }

    private static void ejecutarAlgoritmoGenetico(PlanificacionService planificacionService,
            VueloService vueloService) {
        String archivoRutaEnvios = FileUtils.chooseFile("Buscar Envíos");
        String archivoRutaPlanes = FileUtils.chooseFile("Buscar Planes de Vuelo");

        try {
            List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
            List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);
            List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);

            if (!envios.isEmpty() && !vuelosActuales.isEmpty()) {
                Cromosoma resultado = planificacionService.ejecutarAlgoritmoGenetico(envios, aeropuertos,
                        vuelosActuales, planesDeVuelo);
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

    private static void ejecutarPSO(PlanificacionService planificacionService, VueloService vueloService) {
        String archivoRutaEnvios = "ProyectoDP1/src/main/resources/pack_enviado_ZBAA.txt";
        String archivoRutaPlanes = "ProyectoDP1/src/main/resources/planes_vuelo.v3.txt";

        try {
            List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
            List<Envio> envios = vueloService.getEnvios(archivoRutaEnvios);
            envios = envios.subList(0, 1);
            List<PlanDeVuelo> planesDeVuelo = vueloService.getPlanesDeVuelo(aeropuertos, archivoRutaPlanes);
            List<Vuelo> vuelosActuales = vueloService.getVuelosActuales(planesDeVuelo);
            List<Paquete> paquetes = envios.stream().map(Envio::getPaquetes).flatMap(List::stream)
                    .collect(Collectors.toList());

            System.out.println("Paquetes: " + paquetes.size());
            List<RutaPredefinida> rutasPred = planificacionService.generarRutas(aeropuertos, planesDeVuelo);
            List<Almacen> almacenes = aeropuertos.stream().map(Aeropuerto::getAlmacen).collect(Collectors.toList());

            if (!envios.isEmpty() && !vuelosActuales.isEmpty()) {
                Map<Paquete, RutaTiempoReal> resultado = planificacionService.PSO(paquetes, rutasPred, almacenes,
                        planesDeVuelo, aeropuertos, vuelosActuales);
                if (resultado != null) {
                    System.out.println("Resultado del PSO procesado.");
                } else {
                    System.out.println("No se obtuvo un resultado válido del PSO.");
                }
            }
        } catch (Exception e) {
            System.err.println("Se ha producido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
