package src.service;

import src.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    public List<Double> calcularFitnessAgregado(List<Cromosoma> poblacion, List<Aeropuerto> aeropuertos) {
        List<Double> fitnessCromosomas = new ArrayList<>();

        for (Cromosoma cromosoma : poblacion) {
            double penalizacion = 0.0;
            Map<String, Integer> usoCapacidadVuelos = new HashMap<>();
            Map<String, Integer> usoCapacidadAlmacenes = new HashMap<>();

            for (Map.Entry<Paquete,RutaPredefinida> entrada : cromosoma.getGen().entrySet()) {
                
                RutaPredefinida ruta = entrada.getValue();
                Paquete paquete = entrada.getKey();
                Vuelo _vuelosActivo = null;//VueloService.encontrarVueloActual(vuelosActivos, ruta);

                String paqueteDestino = paquete.getCodigoIATADestino();
                String rutaDestino = ruta.getCodigoIATADestino();
                paquete.setStatus(3);
                if(paqueteDestino.equals(rutaDestino)){
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
                else{
                    penalizacion+=100000;
                }
            }

            double fitness = valorBaseFitness - penalizacion;
            fitnessCromosomas.add(fitness);
        }

        return fitnessCromosomas;
    }

    public double fitness(Map<Paquete, RutaTiempoReal> poblacion, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActivos) {
        double penalizacion = 0.0;
        Map<String, Integer> usoCapacidadVuelos = new HashMap<>();
        Map<String, Integer> usoCapacidadAlmacenes = new HashMap<>();

        for (Entry<Paquete, RutaTiempoReal> entrada : poblacion.entrySet()) {
            Paquete paquete = entrada.getKey();
            RutaTiempoReal ruta = entrada.getValue();
            Vuelo _vuelosActivo = encontrarVueloActual(vuelosActivos, ruta);

            paquete.setStatus(3);

            if(paquete.getCodigoIATADestino()== ruta.getDestino().getCodigoIATA()){
                
            
                // Agregando carga al vuelo
                String claveVuelo = ruta.getOrigen().getCodigoIATA() + "-" + ruta.getDestino().getCodigoIATA();
                usoCapacidadVuelos.put(claveVuelo, usoCapacidadVuelos.getOrDefault(claveVuelo, 0) + 1);

                // Verificar capacidad de vuelo
                if (usoCapacidadVuelos.get(claveVuelo) > _vuelosActivo.getCapacidad()) {
                    penalizacion += (usoCapacidadVuelos.get(claveVuelo) - _vuelosActivo.getCapacidad())
                            * penalizacionPorExceso;
                }

                // Gestión de capacidades de almacenes
                VueloService.actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getOrigen().getCodigoIATA(), 1);
                VueloService.actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getDestino().getCodigoIATA(), 1);

                Almacen almacenOrigen = encontrarAlmacenActual(aeropuertos, ruta.getOrigen().getCodigoIATA());
                Almacen almacenDestino = encontrarAlmacenActual(aeropuertos, ruta.getDestino().getCodigoIATA());
                penalizacion += almacenOrigen.verificarCapacidadAlmacen() * penalizacionPorExceso;
                penalizacion += almacenDestino.verificarCapacidadAlmacen() * penalizacionPorExceso;
            }
            else{
                penalizacion+=100000;
            }
        }

        double fitnessValue = valorBaseFitness - penalizacion;

        return fitnessValue;
    }

    private Vuelo encontrarVueloActual(List<Vuelo> vuelosActivos, RutaTiempoReal ruta) {
        for (Vuelo vuelo : vuelosActivos) {
            for (Vuelo vueloEnRuta : ruta.getVuelos()) {
                if (vuelo.getIdVuelo() == vueloEnRuta.getIdVuelo()) {
                    return vuelo;
                }
            }
        }
        return null; // Retornar null si no se encuentra ningún vuelo que coincida
    }

    private Almacen encontrarAlmacenActual(List<Aeropuerto> aeropuertos, String codigoIATA) {
        for (Aeropuerto aeropuerto : aeropuertos) {
            if (aeropuerto.getCodigoIATA().equals(codigoIATA)) {
                return aeropuerto.getAlmacen();
            }
        }
        return null; // Retornar null si no se encuentra ningún almacén que coincida
    }

    void ordernarPoblacion(List<Cromosoma> poblacion, List <Double> fitnessAgregado){
        List<Pair> pairs = new ArrayList<>();
        for(int i = 0; i < poblacion.size(); i++){
            pairs.add(new Pair(poblacion.get(i), fitnessAgregado.get(i)));
        }

        Collections.sort(pairs,(pair1,pair2) -> Double.compare(pair2.fitness, pair1.fitness));

        poblacion.clear();
        fitnessAgregado.clear();

        for(Pair pair : pairs){
            poblacion.add(pair.cromosoma);
            fitnessAgregado.add(pair.fitness);
        }
    }

}