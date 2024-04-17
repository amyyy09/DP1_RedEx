package src.Clases;

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

    public List<Double> calcularFitnessAgregado(List<Cromosoma> poblacion, List<Almacen> almacenesActuales, List<Vuelo> vuelosActuales) {
    List<Double> fitnessCromosomas = new ArrayList<>();

    for (Cromosoma cromosoma : poblacion) {
        double penalizacion = 0.0;
        Map<String, Integer> usoCapacidadVuelos = new HashMap<>();
        Map<String, Integer> usoCapacidadAlmacenes = new HashMap<>();

        for (Map.Entry<RutaTiempoReal, Paquete> entrada : cromosoma.getGen().entrySet()) {
            RutaTiempoReal ruta = entrada.getKey();
            Paquete paquete = entrada.getValue();
            Vuelo vueloAnalizado = encontrarVueloActual(vuelosActuales, ruta);
            
            paquete.setStatus(3);

            // Agregando carga al vuelo
            String claveVuelo = ruta.getOrigen().getCodigoIATA() + "-" + ruta.getDestino().getCodigoIATA();
            usoCapacidadVuelos.put(claveVuelo, usoCapacidadVuelos.getOrDefault(claveVuelo, 0) + 1);

            // Verificar capacidad de vuelo
            if (usoCapacidadVuelos.get(claveVuelo) > vueloAnalizado.getCapacidad()) {
                penalizacion += (usoCapacidadVuelos.get(claveVuelo) - vueloAnalizado.getCapacidad()) * penalizacionPorExceso;
            }

            // Gestión de capacidades de almacenes
            actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getOrigen().getCodigoIATA(), 1);
            actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getDestino().getCodigoIATA(), 1);

            Almacen almacenOrigen = encontrarAlmacenActual(almacenesActuales, ruta.getOrigen().getCodigoIATA());
            Almacen almacenDestino = encontrarAlmacenActual(almacenesActuales, ruta.getDestino().getCodigoIATA());
            penalizacion += verificarCapacidadAlmacen(almacenOrigen, usoCapacidadAlmacenes.get(almacenOrigen.getCodigoIATA()));
            penalizacion += verificarCapacidadAlmacen(almacenDestino, usoCapacidadAlmacenes.get(almacenDestino.getCodigoIATA()));
        }

        double fitness = valorBaseFitness - penalizacion;
        fitnessCromosomas.add(fitness);
    }

    return fitnessCromosomas;
}

private Vuelo encontrarVueloActual(List<Vuelo> vuelosActuales, RutaTiempoReal ruta){
    // Buscar un vuelo que coincida con la ruta proporcionada
    for (Vuelo vuelo : vuelosActuales) {
        for (Vuelo vueloEnRuta : ruta.getVuelos()) {
            if (vuelo.getIdVuelo() == vueloEnRuta.getIdVuelo()){
                return vuelo;
            }
        }
    }
    return null; // Retornar null si no se encuentra ningún vuelo que coincida
}

private Almacen encontrarAlmacenActual(List<Almacen> almacenesActuales, String codigoIATA){
    // Buscar un almacén que coincida con el código IATA proporcionado
    for (Almacen almacen : almacenesActuales) {
        if (almacen.getCodigoIATA().equals(codigoIATA)) {
            return almacen;
        }
    }
    return null; // Retornar null si no se encuentra ningún almacén que coincida
}

private void actualizarUsoCapacidadAlmacen(Map<String, Integer> usoCapacidad, String codigoIATA, int cantidad) {
    usoCapacidad.put(codigoIATA, usoCapacidad.getOrDefault(codigoIATA, 0) + cantidad);
}

private double verificarCapacidadAlmacen(Almacen almacen, int cantidadUsada) {
    if (cantidadUsada > almacen.getCapacidad()) {
        return (cantidadUsada - almacen.getCapacidad()) * penalizacionPorExceso;
    }
    return 0.0;
}
}