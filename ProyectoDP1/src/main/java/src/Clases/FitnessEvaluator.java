package src.Clases;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FitnessEvaluator {
    private double penalizacionPorExceso = 10.0; // Penalización por cada unidad que excede la capacidad
    private double valorBaseFitness = 1000.0; // Puntaje base de fitness

    public FitnessEvaluator() {
    }

    public FitnessEvaluator(double penalizacionPorExceso, double valorBaseFitness) {
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
                Vuelo _vuelosActivo = encontrarVueloActual(vuelosActivos, ruta);

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
                actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getCodigoIATAOrigen(), 1);
                actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getCodigoIATADestino(), 1);

                Almacen almacenOrigen = encontrarAlmacenActual(aeropuertos, ruta.getCodigoIATAOrigen());
                Almacen almacenDestino = encontrarAlmacenActual(aeropuertos, ruta.getCodigoIATADestino());
                penalizacion += almacenOrigen.verificarCapacidadAlmacen() * penalizacionPorExceso;
                penalizacion += almacenDestino.verificarCapacidadAlmacen() * penalizacionPorExceso;
            }

            double fitness = valorBaseFitness - penalizacion;
            fitnessCromosomas.add(fitness);
        }

        return fitnessCromosomas;
    }

    private Vuelo encontrarVueloActual(List<Vuelo> vuelosActivos, RutaPredefinida ruta) {
        OffsetTime horaActual = OffsetTime.now();

        for (Vuelo vuelo : vuelosActivos) {
            if (vuelo.getPlanDeVuelo().getCodigoIATAOrigen().equals(ruta.getCodigoIATAOrigen()) &&
                    vuelo.getPlanDeVuelo().getCodigoIATADestino().equals(ruta.getCodigoIATADestino()) &&
                    vuelo.getPlanDeVuelo().getHoraSalida().isBefore(horaActual) &&
                    vuelo.getPlanDeVuelo().getHoraLlegada().isAfter(horaActual)) {
                return vuelo;
            }
        }
        return null; // Retornar null si no se encuentra ningún vuelo que coincida
    }

    private Almacen encontrarAlmacenActual(List<Aeropuerto> aeropuertos, String codigoIATA) {
        // Buscar un almacén que coincida con el código IATA proporcionado
        for (Aeropuerto aeropuerto : aeropuertos) {
            if (aeropuerto.getCodigoIATA().equals(codigoIATA)) {
                return aeropuerto.getAlmacen();
            }
        }
        return null; // Retornar null si no se encuentra ningún almacén que coincida
    }

    private void actualizarUsoCapacidadAlmacen(Map<String, Integer> usoCapacidad, String codigoIATA, int cantidad) {
        usoCapacidad.put(codigoIATA, usoCapacidad.getOrDefault(codigoIATA, 0) + cantidad);
    }

}