package src.main;

//dependencias
import org.springframework.boot.autoconfigure.SpringBootApplication;

//utilidades
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

//paquetes
import src.Clases.Paquete;
import src.Clases.PlanDeVuelo;
import src.Clases.RutaTiempoReal;
import src.Clases.RutaPredefinida;
import src.Clases.Aeropuerto;
import src.Clases.Almacen;
import src.Clases.DatosAeropuertos;
import src.Clases.Envio;
import src.Clases.Particula;
import src.Clases.FitnessEvaluator;

@SpringBootApplication
public class ProyectoDp1Application_PSO {
    private static Random rand = new Random();

    public static void main(String[] args) {
        
    }

    public static Map<Paquete, RutaPredefinida> PSO(List<Paquete> paquetes, List<RutaPredefinida> rutasPred, List<Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo) {
        List<Particula> population = new ArrayList<>();
        int numParticles = 50;
        int numIterationsMax=100;
        double w = 0.1, c1 = 0.1, c2 = 0.1;

        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(inicializarPosicion(paquetes, rutasPred));
            particle.setVelocidad(inicializarVelocidad(paquetes.size()));
            particle.setPbest(particle.getPosicion());
            particle.setFbest(fitness(particle.getPbest()));
            population.add(particle);
        }
        Map<Paquete, RutaPredefinida> gbest = determineGbest(population);

        for (int j = 0; j < numIterationsMax; j++) {
            for (Particula particle : population) {
                for (int k=0; k<paquetes.size(); k++) {
                    double r1 = rand.nextDouble(), r2 = rand.nextDouble();
                    double velocity = w * particle.getVelocidad().get(k) + 

                        c1 * r1 * (rutasPred.indexOf(particle.getPbest().get(paquetes.get(k))) - rutasPred.indexOf(particle.getPosicion().get(paquetes.get(k)))) +

                        c2 * r2 * (rutasPred.indexOf(gbest.get(paquetes.get(k))) - rutasPred.indexOf(particle.getPosicion().get(paquetes.get(k))));

                    int velint = verifyLimits(velocity, rutasPred);

                    particle.getVelocidad().set(k,(double) velint);

                    RutaPredefinida newPosition = rutasPred.get(rutasPred.indexOf(particle.getPosicion().get(paquetes.get(k))) + velint);

                    particle.getPosicion().put(paquetes.get(k), newPosition);
                }

                double fit = fitness(particle.getPosicion());

                if (fit < particle.getFbest()) {
                    particle.setPbest(particle.getPosicion());
                    particle.setFbest(fit);
                }
            }
            Map<Paquete, RutaPredefinida> currentGbest = determineGbest(population);
            if (fitness(currentGbest) < fitness(gbest)) {
                gbest = currentGbest;
            }
        }
        return gbest;
    }

    public static Map<Paquete, RutaPredefinida> inicializarPosicion(List<Paquete> paquetes, List<RutaPredefinida> rutasPred) {
        Map<Paquete, RutaPredefinida> position = new HashMap<>();
        for (Paquete pkg : paquetes) {
            RutaPredefinida randomRoute = rutasPred.get(rand.nextInt(rutasPred.size()));
            position.put(pkg, randomRoute);
        }
        return position;
    }

    public static List<Double> inicializarVelocidad(int numPaquetes) {
        List<Double> velocity = new ArrayList<>();
        for (int i = 0; i < numPaquetes; i++) {
            double randomChange = rand.nextDouble();
            velocity.add(randomChange);
        }
        return velocity;
    }

    public static int verifyLimits(double velocity, List<RutaPredefinida> rutasPred){

        int val = (int) Math.floor(velocity);
        
        if (val < 0){
            val = rutasPred.size() + val;
        }
        if (val >= rutasPred.size()){
            val = rutasPred.size() - val;
        }

        return val;
    }

    public static Map<Paquete, RutaPredefinida> determineGbest(List<Particula> population) {
        Map<Paquete, RutaPredefinida> gbest = new HashMap<>(population.get(0).getPosicion());
        double bestFitness = fitness(gbest);
        for (Particula particle : population) {
            if (particle.getFbest() < bestFitness) {
                gbest = new HashMap<>(particle.getPbest());
                bestFitness = particle.getFbest();
            }
        }
        return gbest;
    }

    public static double fitness(Map<Paquete, RutaPredefinida> solution) {
        // Implement your fitness function here...
        return 0.0;
    }
}