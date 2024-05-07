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

    public static Map<Paquete, RutaTiempoReal> inicializarPosicion(List<Envio> envios,List<RutaPredefinida> rutasPred, 
            List<Aeropuerto> aeropuertos, List<Vuelo> vuelosActivos) {
        Map<Paquete, RutaTiempoReal> position = new HashMap<>();
        for (Envio env : envios) {
            String codigoIATAOrigen = env.getCodigoIATAOrigen();
            String codigoIATADestino = env.getCodigoIATADestino();
            int horaSalida = env.getFechaHoraOrigen().getHour()*100 + env.getFechaHoraOrigen().getMinute();
            int horaLlegada = env.getFechaHoraOrigen().getHour()*100 + env.getFechaHoraOrigen().getMinute();
    
            List<RutaPredefinida> filteredRutasPred = rutasPred.stream()
                .filter(ruta -> ruta.getCodigoIATAOrigen().equals(codigoIATAOrigen) 
                    && ruta.getCodigoIATADestino().equals(codigoIATADestino)
                    && ruta.getHoraLlegada().getHour()*100 + ruta.getHoraLlegada().getMinute() >= horaLlegada
                    && (ruta.getHoraSalida().getHour()*100 + ruta.getHoraSalida().getMinute() <= horaSalida
                    || (ruta.getHoraSalida().getHour()*100 + ruta.getHoraSalida().getMinute() >= horaSalida
                    && ruta.getDuracion() < 1))).toList();

            if (filteredRutasPred.isEmpty()) {
                System.err.println("ayuda, revisar el filtrado de rutas predefinidas");
            } 
            for (Paquete pkg : env.getPaquetes()) {
                int index = new Random().nextInt(filteredRutasPred.size());
                RutaPredefinida randomRoute = filteredRutasPred.get(index);
                RutaTiempoReal randTiempoReal = randomRoute.convertirAPredefinidaEnTiempoReal(aeropuertos, vuelosActivos);
                position.put(pkg, randTiempoReal);
            }
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
            val = (val % rutasPred.size() + rutasPred.size()) % rutasPred.size();
        }
        if (val >= rutasPred.size()) {
            val = val % rutasPred.size();
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
