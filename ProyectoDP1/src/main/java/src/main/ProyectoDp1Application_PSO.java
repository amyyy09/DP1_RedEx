package src.main;

//dependencias
import org.springframework.boot.autoconfigure.SpringBootApplication;

//utilidades
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;

//paquetes
import src.Clases.*;

@SpringBootApplication
public class ProyectoDp1Application_PSO {
    private static Random rand = new Random();

    public static void main(String[] args) {

    }

    public static Map<Paquete, RutaTiempoReal> PSO(List<Paquete> paquetes, List<RutaPredefinida> rutasPred,
            List<Almacen> almacenes, List<PlanDeVuelo> planesDeVuelo, List<Aeropuerto> aeropuertos,
            List<Vuelo> vuelosActuales) {
        List<Particula> population = new ArrayList<>();
        int numParticles = 50;
        int numIterationsMax = 100;
        double w = 0.1, c1 = 0.1, c2 = 0.1;

        FitnessEvaluator fitnessEvaluator = new FitnessEvaluator();

        for (int i = 0; i < numParticles; i++) {
            Particula particle = new Particula();
            particle.setPosicion(Particula.inicializarPosicion(paquetes, rutasPred, aeropuertos));
            particle.setVelocidad(Particula.inicializarVelocidad(paquetes.size()));
            particle.setPbest(particle.getPosicion());
            particle.setFbest(fitnessEvaluator.fitness(particle.getPbest(), aeropuertos, vuelosActuales));
            population.add(particle);
        }
        Map<Paquete, RutaTiempoReal> gbest = Particula.determineGbest(population, aeropuertos, vuelosActuales);

        for (int j = 0; j < numIterationsMax; j++) {
            for (Particula particle : population) {
                for (int k = 0; k < paquetes.size(); k++) {
                    double r1 = rand.nextDouble(), r2 = rand.nextDouble();
                    double velocity = w * particle.getVelocidad().get(k) +

                            c1 * r1 * (rutasPred.indexOf(particle.getPbest().get(paquetes.get(k)).getRutaPredefinida())
                                    - rutasPred
                                            .indexOf(particle.getPosicion().get(paquetes.get(k)).getRutaPredefinida()))
                            +

                            c2 * r2 * (rutasPred.indexOf(gbest.get(paquetes.get(k)).getRutaPredefinida()) - rutasPred
                                    .indexOf(particle.getPosicion().get(paquetes.get(k)).getRutaPredefinida()));

                    int velint = Particula.verifyLimits(velocity, rutasPred);

                    particle.getVelocidad().set(k, (double) velint);

                    RutaPredefinida newPosition = rutasPred
                            .get(rutasPred.indexOf(particle.getPosicion().get(paquetes.get(k)).getRutaPredefinida())
                                    + velint);

                    RutaTiempoReal newRTR = newPosition.convertirAPredefinidaEnTiempoReal(aeropuertos);

                    particle.getPosicion().put(paquetes.get(k), newRTR);
                }

                double fit = fitnessEvaluator.fitness(particle.getPosicion(), aeropuertos, vuelosActuales);

                if (fit < particle.getFbest()) {
                    particle.setPbest(particle.getPosicion());
                    particle.setFbest(fit);
                }
            }
            Map<Paquete, RutaTiempoReal> currentGbest = Particula.determineGbest(population, aeropuertos,
                    vuelosActuales);
            if (fitnessEvaluator.fitness(currentGbest, aeropuertos, vuelosActuales) < fitnessEvaluator.fitness(gbest,
                    aeropuertos, vuelosActuales)) {
                gbest = currentGbest;
            }
        }
        return gbest;
    }
}