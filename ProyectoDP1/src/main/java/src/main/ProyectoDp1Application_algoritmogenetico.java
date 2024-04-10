package src.main;

//dependencias
import org.springframework.boot.SpringApplication;
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
import src.Clases.Ruta;
import src.Clases.RutaComun;
import src.Clases.Aeropuerto;
import src.Clases.Almacen;
import src.Clases.Cromosoma;
import src.Clases.Vuelo;
import src.Clases.Aeropuerto;

@SpringBootApplication
public class ProyectoDp1Application_algoritmogenetico {
	public static void main(String[] args) {
		try {
			List<Aeropuerto> aeropuertos = Aeropuerto.leerAeropuertos();
			List<PlanDeVuelo> planes = PlanDeVuelo.leerPlanesDeVuelo(aeropuertos);
			List<RutaComun> rutas = RutaComun.generarRutas(aeropuertos, planes);
			System.out.println("Rutas generadas " + rutas.size());
		} catch (Exception e) {
			System.err.println("Se ha producido un error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public Cromosoma ejecutarAlgoritmo(List<Paquete> paquetes, List<Ruta> rutas,
			List<Almacen> almacenes, List<PlanDeVuelo> vuelos,
			double Ps, double Pm, double Pc, int numCromosomas,
			int numTorneo, int numDescendientes, int numGeneraciones) {

		List<Cromosoma> poblacion = new ArrayList<>();
		for (int i = 0; i < numGeneraciones; i++) {
			// Determinar el fitness de la población/generación actual
			List<Double> fitnessAgregado = calcularFitnessAgregado(poblacion, almacenes, vuelos);
			if (!fitnessAgregado.isEmpty() && fitnessAgregado.get(0) >= 0) {
				System.out.println("Se ha encontrado una solución satisfactoria");
				return poblacion.get(0);
			}

			// Formación del mating pool (padres candidatos)
			List<Cromosoma> matingPool = TournnamentSeleccion(poblacion, Ps, numTorneo, fitnessAgregado);

			List<Cromosoma> descendientes = new ArrayList<>();

			// Iteraciones para generación de descendientes
			for (int j = 0; j < numDescendientes / 2; j++) {
				// Selección aleatoria de pareja de padres del mating pool
				Random rand = new Random();
				int padre1 = rand.nextInt(matingPool.size());
				int padre2 = rand.nextInt(matingPool.size());

				// Cruzamiento bajo probabilidad Pc
				if (Math.random() < Pc) {
					List<Cromosoma> hijos = crossover(matingPool.get(padre1), matingPool.get(padre2));

					// Mutación bajo probabilidad Pm
					if (Math.random() < Pm) {
						mutarHijos(hijos, rutas);
					}
					descendientes.addAll(hijos);
				}
			}

			// Aquí podrías actualizar la población con los descendientes generados
			// Considera criterios de selección o reemplazo para la próxima generación
		}

		// En caso de no encontrar una solución satisfactoria, decide qué hacer
		System.out.println("No se encontró una solución satisfactoria.");
		return null; // O cualquier otra acción como devolver el mejor encontrado
	}

	// Considera implementar aquí los métodos calcularFitnessAgregado,
	// tournamentSelection, crossover, mutarHijos, etc.
	public void mutarHijos(List<Cromosoma> hijos, List<Ruta> rutasDisponibles) {
		Random rand = new Random();
		for (Cromosoma hijo : hijos) {
			// Convertir las claves del mapa (Ruta) a una lista para poder seleccionar una
			// al azar
			List<Ruta> rutasActuales = new ArrayList<>(hijo.getGen().keySet());
			int indexRutaAMutar = rand.nextInt(rutasActuales.size());
			Ruta rutaAMutar = rutasActuales.get(indexRutaAMutar);

			// Seleccionar una nueva ruta que sea diferente a la actual
			Ruta nuevaRuta;
			do {
				nuevaRuta = rutasDisponibles.get(rand.nextInt(rutasDisponibles.size()));
			} while (nuevaRuta.equals(rutaAMutar));

			// Obtener el paquete asociado a la ruta que vamos a mutar
			Paquete paqueteAMutar = hijo.getGen().get(rutaAMutar);

			// Remover la asignación de ruta-paquete antigua y añadir la nueva
			hijo.getGen().remove(rutaAMutar);
			hijo.getGen().put(nuevaRuta, paqueteAMutar);
		}
	}

	List<Double> calcularFitnessAgregado(List<Cromosoma> poblacion, List<Almacen> almacenes,
			List<PlanDeVuelo> vuelos) {
		List<Double> fitnesssCromo = new ArrayList<Double>();
		return fitnesssCromo;
	}

	public List<Cromosoma> TournnamentSeleccion(List<Cromosoma> poblacion, double Ps, int NumTorneo,
			List<Double> fitnessAgregado) {
		List<Cromosoma> mattingPool = new ArrayList<Cromosoma>();
		int cantidadSeleccion = (int) (poblacion.size() * NumTorneo / 100);
		while (cantidadSeleccion != 0) {

			List<Cromosoma> torneo = new ArrayList<Cromosoma>();
			List<Double> fitnessTorneo = new ArrayList<Double>();
			for (int i = 0; i < poblacion.size(); i++) {
				Random rand = new Random();
				int j = rand.nextInt(poblacion.size());
				double n = Math.random();// numeros aleatorios entre 0 y 1
				if (Ps < n) {
					torneo.add(poblacion.get(j));
					fitnessTorneo.add(fitnessAgregado.get(j));
				}
			}
			int max = 0;
			for (int i = 0; i < torneo.size(); i++) {
				if (fitnessTorneo.get(i) > fitnessTorneo.get(max)) {
					max = i;
				}
			}
			mattingPool.add(torneo.get(max));
			cantidadSeleccion--;
		}
		return mattingPool;
	}

	public List<Cromosoma> crossover(Cromosoma padre1, Cromosoma padre2) {

		Map<Ruta, Paquete> genPadre1 = new HashMap<>(padre1.getGen());
		Map<Ruta, Paquete> genPadre2 = new HashMap<>(padre2.getGen());

		List<Map.Entry<Ruta, Paquete>> listaGenPadre1 = new ArrayList<>(genPadre1.entrySet());
		List<Map.Entry<Ruta, Paquete>> listaGenPadre2 = new ArrayList<>(genPadre2.entrySet());

		Random random = new Random();
		int puntoCruce = random.nextInt(listaGenPadre1.size());

		for (int i = puntoCruce; i < listaGenPadre1.size(); i++) {
			Map.Entry<Ruta, Paquete> temp = listaGenPadre1.get(i);
			listaGenPadre1.set(i, listaGenPadre2.get(i));
			listaGenPadre2.set(i, temp);
		}

		Map<Ruta, Paquete> genHijo1 = new HashMap<>();
		Map<Ruta, Paquete> genHijo2 = new HashMap<>();
		for (int i = 0; i < listaGenPadre1.size(); i++) {
			genHijo1.put(listaGenPadre1.get(i).getKey(), listaGenPadre1.get(i).getValue());
			genHijo2.put(listaGenPadre2.get(i).getKey(), listaGenPadre2.get(i).getValue());
		}

		Cromosoma hijo1 = new Cromosoma(genHijo1);
		Cromosoma hijo2 = new Cromosoma(genHijo2);
		List<Cromosoma> hijos = new ArrayList<>();
		hijos.add(hijo1);
		hijos.add(hijo2);

		return hijos;
	}

}
