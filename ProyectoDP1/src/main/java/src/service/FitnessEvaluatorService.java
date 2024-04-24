package src.service;

import src.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class FitnessEvaluatorService {
    private double penalizacionPorExceso = 10.0; // Penalización por cada unidad que excede la capacidad
    private double valorBaseFitness = 1000.0; // Puntaje base de fitness

    public FitnessEvaluatorService() {
    }

    public FitnessEvaluatorService(double penalizacionPorExceso, double valorBaseFitness) {
        this.penalizacionPorExceso = penalizacionPorExceso;
        this.valorBaseFitness = valorBaseFitness;
    }

    public List<Double> calcularFitnessAgregado(List<Cromosoma> poblacion, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActivos) {
        List<Double> fitnessCromosomas = new ArrayList<>();

        for (Cromosoma cromosoma : poblacion) {
            double penalizacion = 0.0;
            Map<String, Integer> usoCapacidadVuelos = new HashMap<>();
            Map<String, Integer> usoCapacidadAlmacenes = new HashMap<>();

            for (Map.Entry<RutaPredefinida, Paquete> entrada : cromosoma.getGen().entrySet()) {
                RutaPredefinida ruta = entrada.getKey();
                Paquete paquete = entrada.getValue();
                Vuelo _vuelosActivo = VueloService.encontrarVueloActual(vuelosActivos, ruta);

                paquete.setStatus(3);

                // Agregando carga al vuelo
                String claveVuelo = ruta.getCodigoIATAOrigen() + "-" + ruta.getCodigoIATADestino();
                usoCapacidadVuelos.put(claveVuelo, usoCapacidadVuelos.getOrDefault(claveVuelo, 0) + 1);

                // Verificar capacidad de vuelo
                if (usoCapacidadVuelos.get(claveVuelo) > _vuelosActivo.getCapacidad()) {
                    penalizacion += (usoCapacidadVuelos.get(claveVuelo) - _vuelosActivo.getCapacidad())
                            * penalizacionPorExceso;
                }

                // Gestión de capacidades de almacenes
                VueloService.actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getCodigoIATAOrigen(), 1);
                VueloService.actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getCodigoIATADestino(), 1);

                Almacen almacenOrigen = VueloService.encontrarAlmacenActual(aeropuertos, ruta.getCodigoIATAOrigen());
                Almacen almacenDestino = VueloService.encontrarAlmacenActual(aeropuertos, ruta.getCodigoIATADestino());
                penalizacion += almacenOrigen.verificarCapacidadAlmacen() * penalizacionPorExceso;
                penalizacion += almacenDestino.verificarCapacidadAlmacen() * penalizacionPorExceso;
            }

            double fitness = valorBaseFitness - penalizacion;
            fitnessCromosomas.add(fitness);
        }

        return fitnessCromosomas;
    }
}