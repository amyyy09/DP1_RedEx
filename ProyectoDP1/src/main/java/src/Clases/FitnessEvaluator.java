package src.Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public List<Double> calcularFitnessAgregado(List<Cromosoma> poblacion,
        List<Almacen> almacenes,
        List<Vuelo> planesDeVuelo) {
    List<Double> fitnessCromosomas = new ArrayList<>();

    for (Cromosoma cromosoma : poblacion) {
        double penalizacion = 0.0;
        Map<String, Integer> usoCapacidadVuelos = new HashMap<>();
        Map<String, Integer> usoCapacidadAlmacenes = new HashMap<>();

        for (Map.Entry<RutaTiempoReal, Paquete> entrada : cromosoma.getGen().entrySet()) {
            RutaTiempoReal ruta = entrada.getKey();
            Paquete paquete = entrada.getValue();
            PlanDeVuelo planDeVuelo = encontrarPlanDeVueloParaRuta(planesDeVuelo, ruta);

            // Agregando carga al vuelo
            String claveVuelo = ruta.getOrigen().getCodigoIATA() + "-" + ruta.getDestino().getCodigoIATA();
            usoCapacidadVuelos.put(claveVuelo, usoCapacidadVuelos.getOrDefault(claveVuelo, 0) + 1);

            // Verificar capacidad de vuelo
            if (usoCapacidadVuelos.get(claveVuelo) > planDeVuelo.getCapacidad()) {
                penalizacion += (usoCapacidadVuelos.get(claveVuelo) - planDeVuelo.getCapacidad()) * penalizacionPorExceso;
            }

            // Gestión de capacidades de almacenes
            actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getOrigen().getCodigoIATA(), 1);
            actualizarUsoCapacidadAlmacen(usoCapacidadAlmacenes, ruta.getDestino().getCodigoIATA(), 1);

            Almacen almacenOrigen = obtenerAlmacenPorCodigoIATA(almacenes, ruta.getOrigen().getCodigoIATA());
            Almacen almacenDestino = obtenerAlmacenPorCodigoIATA(almacenes, ruta.getDestino().getCodigoIATA());
            penalizacion += verificarCapacidadAlmacen(almacenOrigen, usoCapacidadAlmacenes.get(almacenOrigen.getCodigoIATA()));
            penalizacion += verificarCapacidadAlmacen(almacenDestino, usoCapacidadAlmacenes.get(almacenDestino.getCodigoIATA()));
        }

        double fitness = valorBaseFitness - penalizacion;
        fitnessCromosomas.add(fitness);
    }

    return fitnessCromosomas;
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
