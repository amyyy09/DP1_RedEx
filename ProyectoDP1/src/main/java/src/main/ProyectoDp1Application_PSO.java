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

    public static Particula PSO(List<Paquete> paquetes, List<RutaPredefinida> rutasPred, List<Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo) {
        List<Particula> population = new ArrayList<>();
        int numParticles = 50;
        int numIterationsMax=100;

        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(inicializarPosicion(paquetes, rutasPred));
            particle.setVelocidad(inicializarVelocidad(paquetes.size()));
            population.add(particle);
        }
        Map<Paquete, RutaPredefinida> gbest = determineGbest(population);

        for (int j = 0; j < numIterationsMax; j++) {
            for (Particula particle : population) {
                for (Paquete pkg : paquetes) {
                    double r1 = rand.nextDouble(), r2 = rand.nextDouble();
                    double velocity = w * particle.getVelocidad().get(pkg) + c1 * r1 * (particle.getPbest().get(pkg).getId() - particle.getPosicion().get(pkg).getId()) + c2 * r2 * (gbest.get(pkg).getId() - particle.getPosicion().get(pkg).getId());
                    particle.getVelocidad().put(pkg, velocity); 
                    RutaPredefinida newPosition = new RutaPredefinida((int) (particle.getPosicion().get(pkg).getId() + velocity));
                    particle.getPosicion().put(pkg, newPosition);
                }
                particle.setPosicion(verifyLimits(particle.getPosicion(), rutasPred, almacenes, planesDeVuelo));
                if (fitness(particle.posicion) < fitness(particle.pbest)) {
                    particle.pbest = new HashMap<>(particle.posicion);
                }
            }
            Particula currentGbest = determineGbest(population);
            if (fitness(currentGbest) < fitness(gbest)) {
                gbest = new Particula(currentGbest);
            }
        }
        return gbest;
    }

    public static Map<Paquete, RutaPredefinida> inicializarPosicion(List<Paquete> paquetes, List<RutaPredefinida> rutasPred) {
        Map<Paquete, RutaPredefinida> position = new HashMap<>();
        for (Paquete pkg : paquetes) {
            RutaPredefinida randomRoute = routes[rand.nextInt(rutasPred.length)];
            position.put(pkg, randomRoute);
        }
        return position;
    }

    public static Map<Paquete, Double> inicializarVelocidad(int numPaquetes) {
        Map<Paquete, Double> velocity = new HashMap<>();
        for (int i = 0; i < numPaquetes; i++) {
            double randomChange = rand.nextDouble();
            velocity.put(new Package(i), randomChange);
        }
        return velocity;
    }

    public static Map<Paquete, RutaPredefinida> verifyLimits(Map<Paquete, RutaPredefinida> position, List<RutaPredefinida> rutasPred, List<Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo) {
        for (Map.Entry<Paquete, RutaPredefinida> entry : position.entrySet()) {
            if (!validCapacity(entry.getValue(), almacenes, planesDeVuelo)) {
                entry.setValue(assignNewValidRoute());
            }
        }
        return position;
    }

    public static Map<Paquete, RutaPredefinida> determineGbest(List<Particula> population) {
        Map<Paquete, RutaPredefinida> gbest = new HashMap<>(population.get(0).posicion);
        double bestFitness = fitness(gbest);
        for (Particula particle : population) {
            if (fitness(particle.pbest) < bestFitness) {
                gbest = new HashMap<>(particle.pbest);
                bestFitness = fitness(particle.pbest);
            }
        }
        return gbest;
    }

    public static double fitness(Map<Paquete, RutaPredefinida> solution) {
        // Implement your fitness function here...
        return 0.0;
    }

    public static boolean validCapacity(List<RutaPredefinida> rutasPred, List<Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo) {
        // Implement your capacity validation here...
        return true;
    }
}