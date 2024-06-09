package src.services;

import src.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class FitnessEvaluatorService {
    private double penalizacionPorExceso = 50.0; // Penalización por cada unidad que excede la capacidad
    // private double valorBaseFitness = 0; // Puntaje base de fitness

    public FitnessEvaluatorService() {
    }

    public FitnessEvaluatorService(double penalizacionPorExceso, double valorBaseFitness) {
        this.penalizacionPorExceso = penalizacionPorExceso;
        // this.valorBaseFitness = valorBaseFitness;
    }

    public Double fitness(Map<Paquete, RutaTiempoReal> particula, Map<String, Almacen> almacenes,
        List<Vuelo> vuelosActivos, boolean gbest) {

        double penalizacion = 0.0;
        Map<String, Integer> usoCapacidadVuelos = new HashMap<>();
        Map<String, TreeMap<LocalDateTime, Integer>> usoCapacidadAlmacenes = new HashMap<>();
        Double fitnessValue = 0.0;
        int size = particula.size();

        for (Entry<Paquete, RutaTiempoReal> entrada : particula.entrySet()) {
            Paquete paquete = entrada.getKey();
            if (entrada.getValue() == null) continue;
            RutaTiempoReal ruta = entrada.getValue();
            List<Vuelo> vuelos = ruta.getVuelos();
            int horaOrigen = paquete.getEnvio().getFechaHoraOrigen().getHour();

            if (gbest) {
                int horaMinOrigen = horaOrigen * 100 + paquete.getEnvio().getFechaHoraOrigen().getMinute();
                int horaLlegada = ruta.getHoraLlegada().getHour() * 100 + ruta.getHoraLlegada().getMinute();
                if (horaMinOrigen < horaLlegada) horaLlegada += 2400;
            }

            int horaSalida = ruta.getRutaPredefinida().getHoraSalida().getHour();
            if (horaSalida < horaOrigen) horaSalida += 24;
            int hoursDifference = Math.abs(horaSalida - horaOrigen);
            fitnessValue += (24.0 - hoursDifference) / (2400 * size);

            for (Vuelo vuelo : vuelos) {
                usoCapacidadVuelos.merge(vuelo.getIdVuelo(), 1, Integer::sum);
                String codigoIATAOrigen = vuelo.getPlanDeVuelo().getCodigoIATAOrigen();
                String codigoIATADestino = vuelo.getPlanDeVuelo().getCodigoIATADestino();

                TreeMap<LocalDateTime, Integer> usoCapacidadAlmacenesOrigen = usoCapacidadAlmacenes
                        .computeIfAbsent(codigoIATAOrigen, k -> new TreeMap<>());
                TreeMap<LocalDateTime, Integer> usoCapacidadAlmacenesDestino = usoCapacidadAlmacenes
                        .computeIfAbsent(codigoIATADestino, k -> new TreeMap<>());

                actualizarCapacidadAlmacen(vuelo.getHoraSalida(), usoCapacidadAlmacenesOrigen, almacenes.get(codigoIATAOrigen), -1);
                actualizarCapacidadAlmacen(vuelo.getHoraLlegada(), usoCapacidadAlmacenesDestino, almacenes.get(codigoIATADestino), 1);
            }
        }

        penalizacion += calcularPenalizacionVuelos(usoCapacidadVuelos, vuelosActivos);
        penalizacion += calcularPenalizacionAlmacenes(usoCapacidadAlmacenes, almacenes);

        fitnessValue -= penalizacion;
        return fitnessValue;
    }

    private void actualizarCapacidadAlmacen(LocalDateTime hora, TreeMap<LocalDateTime, Integer> capacidadAlmacen, Almacen almacen, int ajuste) {
        int ocupacion = capacidadAlmacen.isEmpty() ? almacen.getCantPaquetes() :
                capacidadAlmacen.floorEntry(hora) != null ? capacidadAlmacen.floorEntry(hora).getValue() : almacen.getCantPaquetes();
        capacidadAlmacen.put(hora, ocupacion + ajuste);

        for (LocalDateTime key : capacidadAlmacen.tailMap(hora, false).keySet()) {
            capacidadAlmacen.put(key, capacidadAlmacen.get(key) + ajuste);
        }
    }

    private double calcularPenalizacionVuelos(Map<String, Integer> usoCapacidadVuelos, List<Vuelo> vuelosActivos) {
        double penalizacion = 0.0;
        for (Map.Entry<String, Integer> entry : usoCapacidadVuelos.entrySet()) {
            String idVuelo = entry.getKey();
            int paquetesEnVuelo = entry.getValue();
            Vuelo vuelo = vuelosActivos.stream().filter(v -> v.getIdVuelo().equals(idVuelo)).findFirst().orElse(null);
            if (vuelo != null && paquetesEnVuelo > vuelo.getCapacidad()) {
                penalizacion += (paquetesEnVuelo - vuelo.getCapacidad()) * penalizacionPorExceso;
            }
        }
        return penalizacion;
    }

    private double calcularPenalizacionAlmacenes(Map<String, TreeMap<LocalDateTime, Integer>> usoCapacidadAlmacenes, Map<String, Almacen> almacenes) {
        double penalizacion = 0.0;

        for (Map.Entry<String, TreeMap<LocalDateTime, Integer>> entry : usoCapacidadAlmacenes.entrySet()) {
            String codigoIATA = entry.getKey();
            TreeMap<LocalDateTime, Integer> capacidadAlmacen = entry.getValue();
            Almacen almacen = almacenes.get(codigoIATA);
            int capacidad = almacen.getCapacidad();

            for (int ocupacion : capacidadAlmacen.values()) {
                if (ocupacion > capacidad) {
                    penalizacion += (ocupacion - capacidad) * penalizacionPorExceso;
                    // contNoAtendidos += ocupacion - capacidad;
                }
            }
        }

        return penalizacion;
    }


}