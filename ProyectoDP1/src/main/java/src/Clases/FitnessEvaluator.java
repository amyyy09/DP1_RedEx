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

        for (cromosoma cromosoma : poblacion) {
        double penalizacion = 0.0;

        for (map.entry<ruta, paquete> entrada : cromosoma.getgen().entryset()) {
        ruta ruta = entrada.getkey();
        paquete paquete = entrada.getvalue();
        plandevuelo plandevuelo = encontrarplandevuelopararuta(vuelos, ruta);

        verificar capacidad de los vuelos
        if (plandevuelo != null && ruta.getcantidadpaquetes() >
        plandevuelo.getcapacidad()) {
        penalizacion += (ruta.getcantidadpaquetes() -
        plandevuelo.getcapacidad());
        }

        verificar capacidad del almacen de origen y destino
        for (almacen almacen : almacenes) {
        aquí necesitarías lógica para determinar si este almacén es relevante
        para
        la
        ruta del paquete
        y ajustar la penalización según la capacidad del almacén
        if (almacenserelacionaconruta(almacen, ruta)) {
        int capacidaddisponible = almacen.getcapacidad() -
        almacen.getcantpaquetes();
        if (capacidaddisponible < 0) {
        penalizacion += math.abs(capacidaddisponible);
        }
        }
        }
        }

        calcula el fitness para este cromosoma (podrías ajustar esta fórmula
        según
        tus necesidades)
        double fitness = 1000.0 - penalizacion; // un ejemplo simple
        fitnesscromo.add(fitness);
        }

        return fitnessCromo;
    }

    // Método ficticio para encontrar el plan de vuelo para una ruta dada
    private PlanDeVuelo encontrarPlanDeVueloParaRuta(List<PlanDeVuelo> vuelos, Ruta ruta) {
        // Implementación de ejemplo. Necesitarías adaptar esto a tu lógica real.
        // for (plandevuelo vuelo : vuelos) {
        // // if (vuelo.getorigen().equals(ruta.getorigen()) &&
        // // vuelo.getdestino().equals(ruta.getdestino())) {
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
