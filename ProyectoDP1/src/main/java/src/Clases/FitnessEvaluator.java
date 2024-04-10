package src.Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FitnessEvaluator {
    // Hiperparámetros
    private double penalizacionPorExceso = 10.0; // Valor predeterminado
    private double valorBaseFitness = 1000.0; // Valor predeterminado

    /**
     * Calcula el fitness para toda una población de cromosomas.
     * 
     * @param poblacion La población de cromosomas a evaluar.
     * @param almacenes Lista de almacenes con sus capacidades y cargas actuales.
     * @param vuelos    Lista de planes de vuelo con sus capacidades.
     * @return Una lista con los valores de fitness de cada cromosoma.
     */

    public FitnessEvaluator() {
    }

    public FitnessEvaluator(double penalizacionPorExceso, double valorBaseFitness) {
        this.penalizacionPorExceso = penalizacionPorExceso;
        this.valorBaseFitness = valorBaseFitness;
    }

    public List<Double> calcularFitnessAgregado(List<Cromosoma> poblacion, List<Almacen> almacenes,
            List<PlanDeVuelo> vuelos) {
        List<Double> fitnessCromo = new ArrayList<>();

        // for (Cromosoma cromosoma : poblacion) {
        // double penalizacion = 0.0;

        // for (Map.Entry<Ruta, Paquete> entrada : cromosoma.getGen().entrySet()) {
        // Ruta ruta = entrada.getKey();
        // Paquete paquete = entrada.getValue();
        // PlanDeVuelo planDeVuelo = encontrarPlanDeVueloParaRuta(vuelos, ruta);

        // // Verificar capacidad de los vuelos
        // // if (planDeVuelo != null && ruta.getCantidadPaquetes() >
        // // planDeVuelo.getCapacidad()) {
        // // penalizacion += (ruta.getCantidadPaquetes() - planDeVuelo.getCapacidad());
        // // }

        // // Verificar capacidad del almacen de origen y destino
        // for (Almacen almacen : almacenes) {
        // // Aquí necesitarías lógica para determinar si este almacén es relevante para
        // la
        // // ruta del paquete
        // // y ajustar la penalización según la capacidad del almacén
        // if (almacenSeRelacionaConRuta(almacen, ruta)) {
        // int capacidadDisponible = almacen.getCapacidad() - almacen.getCantPaquetes();
        // if (capacidadDisponible < 0) {
        // penalizacion += Math.abs(capacidadDisponible);
        // }
        // }
        // }
        // }

        // // Calcula el fitness para este cromosoma (podrías ajustar esta fórmula según
        // // tus necesidades)
        // double fitness = 1000.0 - penalizacion; // Un ejemplo simple
        // fitnessCromo.add(fitness);
        // }

        return fitnessCromo;
    }

    // Método ficticio para encontrar el plan de vuelo para una ruta dada
    private PlanDeVuelo encontrarPlanDeVueloParaRuta(List<PlanDeVuelo> vuelos, Ruta ruta) {
        // Implementación de ejemplo. Necesitarías adaptar esto a tu lógica real.
        // for (PlanDeVuelo vuelo : vuelos) {
        // // if (vuelo.getOrigen().equals(ruta.getOrigen()) &&
        // // vuelo.getDestino().equals(ruta.getDestino())) {
        // // return vuelo;
        // // }
        // }
        return null;
    }

    // Método ficticio para determinar si un almacén se relaciona con la ruta de un
    // paquete
    private boolean almacenSeRelacionaConRuta(Almacen almacen, Ruta ruta) {
        // Implementación de ejemplo. Necesitarías adaptar esto a tu lógica real.
        return true; // Simplificación para el ejemplo
    }
}
