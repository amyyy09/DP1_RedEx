package src.Clases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FitnessEvaluator {
    private double penalizacionPorExceso = 10.0; // Penalización por cada unidad que excede la capacidad
    private double valorBaseFitness = 1000.0; // Puntaje base de fitness

    public FitnessEvaluator() {
    }

    public FitnessEvaluator(double penalizacionPorExceso, double valorBaseFitness) {
        this.penalizacionPorExceso = penalizacionPorExceso;
        this.valorBaseFitness = valorBaseFitness;
    }

    public List<Double> calcularFitnessAgregado(List<Cromosoma> poblacion,
            List<Almacen> almacenes,
            List<Vuelo> planesDeVuelo) {
        List<Double> fitnessCromosomas = new ArrayList<>();

        for (Cromosoma cromosoma : poblacion) {
            double penalizacion = 0.0;

            for (Map.Entry<RutaTiempoReal, Paquete> entrada : cromosoma.getGen().entrySet()) {
                RutaTiempoReal ruta = entrada.getKey();
                Paquete paquete = entrada.getValue();
                PlanDeVuelo planDeVuelo = encontrarPlanDeVueloParaRuta(planesDeVuelo, ruta);

                if (planDeVuelo != null) {
                    int exceso = 1 - planDeVuelo.getCapacidad();
                    if (exceso > 0) {
                        penalizacion += exceso * penalizacionPorExceso;
                    }
                }

                Almacen almacenOrigen = obtenerAlmacenPorCodigoIATA(almacenes,
                        ruta.getOrigen().getCodigoIATA());
                Almacen almacenDestino = obtenerAlmacenPorCodigoIATA(almacenes,
                        ruta.getDestino().getCodigoIATA());

                if (almacenOrigen != null && almacenDestino != null) {
                    penalizacion += calcularPenalizacionAlmacen(almacenOrigen, 1);
                    penalizacion += calcularPenalizacionAlmacen(almacenDestino, 1);
                }
            }

            double fitness = valorBaseFitness - penalizacion;
            fitnessCromosomas.add(fitness);
        }

        return fitnessCromosomas;
    }

    private PlanDeVuelo encontrarPlanDeVueloParaRuta(List<Vuelo> planesDeVuelo, RutaTiempoReal RutaComun) {
        for (Vuelo vuelo : planesDeVuelo) {
            if (vuelo.getPlanDeVuelo().getCodigoIATAOrigen().equals(RutaComun.getOrigen().getCodigoIATA()) &&
                    vuelo.getPlanDeVuelo().getCodigoIATADestino().equals(RutaComun.getDestino().getCodigoIATA())) {
                return vuelo.getPlanDeVuelo();
            }
        }
        return null;
    }

    private Almacen obtenerAlmacenPorCodigoIATA(List<Almacen> almacenes, String codigoIATA) {
        for (Almacen almacen : almacenes) {
            if (almacen.getCodigoIATA().equals(codigoIATA)) {
                return almacen;
            }
        }
        return null;
    }

    private double calcularPenalizacionAlmacen(Almacen almacen, int cantidad) {
        int capacidadDisponible = almacen.getCapacidad() - almacen.getCantPaquetes();
        if (capacidadDisponible < cantidad) {
            return Math.abs(capacidadDisponible - cantidad) * penalizacionPorExceso;
        }
        return 0.0;
    }
}
