package src.service;

import src.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class FitnessEvaluatorService {
    private double penalizacionPorExceso = 10.0; // Penalización por cada unidad que excede la capacidad
    private double valorBaseFitness = 0; // Puntaje base de fitness

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

    public double fitness(Map<Paquete, RutaTiempoReal> particula, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActivos) { //RUTA -> FITNESSVALUE
        double penalizacion = 0.0;
        Map<Integer, Integer> usoCapacidadVuelos = new HashMap<>();
        Map<String, TreeMap<LocalDateTime, Integer>> usoCapacidadAlmacenes = new HashMap<>();

        for (Entry<Paquete, RutaTiempoReal> entrada : particula.entrySet()) {
            // Paquete paquete = entrada.getKey();
            RutaTiempoReal ruta = entrada.getValue();
            List<Vuelo> vuelos = ruta.getVuelos();
            //Vuelo _vuelosActivo = encontrarVueloActual(vuelosActivos, ruta);
            //paquete.setStatus(3);

            for(int i=0; i<vuelos.size();i++){
                // agregamos un paquete a la capacidad del vuelo en usoCapacidadVuelos
                usoCapacidadVuelos.put(vuelos.get(i).getIdVuelo(), usoCapacidadVuelos.getOrDefault(vuelos.get(i).getIdVuelo(), 0) + 1);
                String codigoIATAOrigen = vuelos.get(i).getPlanDeVuelo().getCodigoIATAOrigen();
                String codigoIATADestino = vuelos.get(i).getPlanDeVuelo().getCodigoIATADestino();
                // si no existe la clave en usoCapacidadAlmacenes la creamos
                TreeMap<LocalDateTime, Integer> usoCapacidadAlmacenesOrigen = usoCapacidadAlmacenes
                    .computeIfAbsent(codigoIATAOrigen, k -> new TreeMap<>());
                
                Almacen almacenOrigen = aeropuertos.stream().filter(a -> a.getCodigoIATA().equals(codigoIATAOrigen)).findFirst().get().getAlmacen();
                // int capacidadOrigen = almacenOrigen.getCapacidad();
                int ocupacionOrigen;
                // verificar que en el map de usoCapacidadAlmacenesOrigen no exista fechas anteriores a la fecha actual, es un treemap ordenado por fecha
                if(usoCapacidadAlmacenesOrigen.size() > 0){
                    LocalDateTime fechaAnterior = usoCapacidadAlmacenesOrigen.lowerKey(vuelos.get(i).getHoraSalida());
                    if(fechaAnterior != null){
                        ocupacionOrigen = usoCapacidadAlmacenesOrigen.get(fechaAnterior);
                    }
                    else{
                        ocupacionOrigen = almacenOrigen.getCantPaquetes();
                    }
                }
                else{
                    ocupacionOrigen = almacenOrigen.getCantPaquetes();
                }

                usoCapacidadAlmacenesOrigen.put(vuelos.get(i).getHoraSalida(), ocupacionOrigen-1);

                NavigableMap<LocalDateTime, Integer> tailMap = usoCapacidadAlmacenesOrigen.tailMap(vuelos.get(i).getHoraSalida(), false);

                for (Map.Entry<LocalDateTime, Integer> entry : tailMap.entrySet()) {
                    usoCapacidadAlmacenesOrigen.put(entry.getKey(), entry.getValue() - 1);
                }

                Almacen almacenDestino = aeropuertos.stream().filter(a -> a.getCodigoIATA().equals(codigoIATADestino)).findFirst().get().getAlmacen();
                // int capacidadDestino = almacenDestino.getCapacidad();
                int ocupacionDestino;
                // si no existe la clave en usoCapacidadAlmacenes la creamos
                TreeMap<LocalDateTime, Integer> usoCapacidadAlmacenesDestino = usoCapacidadAlmacenes
                    .computeIfAbsent(codigoIATADestino, k -> new TreeMap<>());
                // verificar que en el map de usoCapacidadAlmacenesDestino no exista fechas anteriores a la fecha actual, es un treemap ordenado por fecha
                if(usoCapacidadAlmacenesDestino.size() > 0){
                    LocalDateTime fechaAnterior = usoCapacidadAlmacenesDestino.lowerKey(vuelos.get(i).getHoraLlegada());
                    if(fechaAnterior != null){
                        ocupacionDestino = usoCapacidadAlmacenesDestino.get(fechaAnterior);
                    }
                    else{
                        ocupacionDestino = almacenDestino.getCantPaquetes();
                    }
                }
                else{
                    ocupacionDestino = almacenDestino.getCantPaquetes();
                }

                usoCapacidadAlmacenesDestino.put(vuelos.get(i).getHoraLlegada(), ocupacionDestino+1);

                NavigableMap<LocalDateTime, Integer> tailMapDestino = usoCapacidadAlmacenesDestino.tailMap(vuelos.get(i).getHoraLlegada(), false);

                for (Map.Entry<LocalDateTime, Integer> entry : tailMapDestino.entrySet()) {
                    usoCapacidadAlmacenesDestino.put(entry.getKey(), entry.getValue() + 1);
                }
            }

        }


        // Verificar capacidad de vuelo
        for (Map.Entry<Integer, Integer> entry : usoCapacidadVuelos.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            // si la cantidad de paquetes en el vuelo es mayor a la capacidad del vuelo
            Vuelo vuelo = vuelosActivos.stream().filter(v -> v.getIdVuelo() == key).findFirst().orElse(null);
            if (value > vuelo.getCapacidad()) {
                penalizacion += (value - vuelo.getCapacidad()) * penalizacionPorExceso;
            }
        }

        // Verificar capacidad de almacenes
        for (Map.Entry<String, TreeMap<LocalDateTime, Integer>> entry : usoCapacidadAlmacenes.entrySet()) {
            String key = entry.getKey();
            TreeMap<LocalDateTime, Integer> value = entry.getValue();
            Almacen almacen = aeropuertos.stream().filter(a -> a.getCodigoIATA().equals(key)).findFirst().get().getAlmacen();
            for (Map.Entry<LocalDateTime, Integer> entry2 : value.entrySet()) {
                Integer value2 = entry2.getValue();
                if (value2 > almacen.getCapacidad()) {
                    penalizacion += (value2 - almacen.getCapacidad()) * penalizacionPorExceso;
                }
            }
        }

        double fitnessValue = valorBaseFitness - penalizacion;

        return fitnessValue;
    }

    // private Vuelo encontrarVueloActual(List<Vuelo> vuelosActivos, RutaTiempoReal ruta) {
    //     for (Vuelo vuelo : vuelosActivos) {
    //         for (Vuelo vueloEnRuta : ruta.getVuelos()) {
    //             if (vuelo.getIdVuelo() == vueloEnRuta.getIdVuelo()) {
    //                 return vuelo;
    //             }
    //         }
    //     }
    //     return null; // Retornar null si no se encuentra ningún vuelo que coincida
    // }

    // private Almacen encontrarAlmacenActual(List<Aeropuerto> aeropuertos, String codigoIATA) {
    //     for (Aeropuerto aeropuerto : aeropuertos) {
    //         if (aeropuerto.getCodigoIATA().equals(codigoIATA)) {
    //             return aeropuerto.getAlmacen();
    //         }
    //     }
    //     return null; // Retornar null si no se encuentra ningún almacén que coincida
    // }

}