package src.model;

import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import src.service.FitnessEvaluatorService;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Particula {
    private Map<Paquete, RutaTiempoReal> posicion;
    private List<Double> velocidad;
    private Map<Paquete, RutaTiempoReal> pbest;
    private double fbest;

    public static Map<Paquete, RutaTiempoReal> inicializarPosicion(List<Paquete> paquetes,
            List<RutaPredefinida> rutasPred, List<Aeropuerto> aeropuertos) {
        Map<Paquete, RutaTiempoReal> position = new HashMap<>();
        for (Paquete pkg : paquetes) {
            RutaPredefinida randomRoute = rutasPred.get(new Random().nextInt(rutasPred.size()));
            RutaTiempoReal randTiempoReal = randomRoute.convertirAPredefinidaEnTiempoReal(aeropuertos);
            position.put(pkg, randTiempoReal);
        }
        return position;
    }

    public static List<Double> inicializarVelocidad(int numPaquetes) {
        List<Double> velocity = new ArrayList<>();
        for (int i = 0; i < numPaquetes; i++) {
            double randomChange = new Random().nextDouble();
            velocity.add(randomChange);
        }
        return velocity;
    }

    public static int verifyLimits(double velocity, List<RutaPredefinida> rutasPred) {

        int val = (int) Math.floor(velocity);

        if (val < 0) {
            val = rutasPred.size() + val;
        }
        if (val >= rutasPred.size()) {
            val = rutasPred.size() - val;
        }

        return val;
    }

    public static Map<Paquete, RutaTiempoReal> determineGbest(List<Particula> population, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelos) {
        Map<Paquete, RutaTiempoReal> gbest = new HashMap<Paquete, RutaTiempoReal>(population.get(0).getPosicion());
        FitnessEvaluatorService fitnessEvaluator = new FitnessEvaluatorService();
        double bestFitness = fitnessEvaluator.fitness(gbest, aeropuertos, vuelos);
        for (Particula particle : population) {
            if (particle.getFbest() < bestFitness) {
                gbest = new HashMap<Paquete, RutaTiempoReal>(particle.getPbest());
                bestFitness = particle.getFbest();
            }
        }
        return gbest;
    }
}
