package src.main;

//dependencias
import org.springframework.boot.autoconfigure.SpringBootApplication;

//utilidades
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

//paquetes
import src.Clases.Paquete;
import src.Clases.PlanDeVuelo;
import src.Clases.RutaTiempoReal;
import src.Clases.Vuelo;
import src.Clases.RutaPredefinida;
import src.Clases.Aeropuerto;
import src.Clases.Utilities;
import src.Clases.Cromosoma;
import src.Clases.DatosAeropuertos;
import src.Clases.Envio;
import src.Clases.FitnessEvaluator;

@SpringBootApplication
public class ProyectoDp1Application_algoritmogenetico {
	private static final FitnessEvaluator evaluator = new FitnessEvaluator();

	public static void main(String[] args) {
		try {
			List<Aeropuerto> aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
			String archivoRuta = Utilities.chooseFile();
			if (archivoRuta != null) {
				List<PlanDeVuelo> planesDeVuelo = Utilities.getPlanesDeVuelo(aeropuertos, archivoRuta);
				List<Vuelo> vuelosActuales = Utilities.getVuelosActualesTesting(planesDeVuelo);
				RutaPredefinida.guardarRutasEnCSV(aeropuertos, planesDeVuelo, "rutPred.txt");

				archivoRuta = Utilities.chooseFile();
				if (archivoRuta != null) {
					List<Envio> envios = Utilities.getEnvios(archivoRuta);
					Cromosoma resultado = ejecutarAlgoritmoGenetico(envios, aeropuertos, vuelosActuales);
					if (resultado != null) {
						System.out.println("Resultado del algoritmo genético procesado:");
						Cromosoma.imprimirGenDelCromosoma(resultado);
					} else {
						System.out.println("No se obtuvo un resultado válido del algoritmo genético.");
					}
				} else {
					System.out.println("No se seleccionó ningún archivo de envíos.");
				}
			} else {
				System.out.println("No se seleccionó ningún archivo de planes de vuelo.");
			}
		} catch (Exception e) {
			System.err.println("Se ha producido un error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static Cromosoma ejecutarAlgoritmoGenetico(List<Envio> envios, List<Aeropuerto> aeropuertos,
			List<Vuelo> vuelosActuales) {
		final double probabilidadSeleccion = 0.7;
		final double probabilidadMutacion = 0.1;
		final double probabilidadCruce = 0.85;
		final int numCromosomas = 100;
		final int tamanoTorneo = 5;
		final int numDescendientes = 50;
		final int numGeneraciones = 20;

		List<RutaPredefinida> rutasPred = RutaPredefinida.obtenerRutasConEscalas(aeropuertos);
		List<Cromosoma> poblacion = Cromosoma.createPopulation(envios, rutasPred, numCromosomas, aeropuertos);
		Random rand = new Random();

		for (int generacion = 0; generacion < numGeneraciones; generacion++) {
			List<Double> fitnessAgregado = evaluator.calcularFitnessAgregado(poblacion, aeropuertos, vuelosActuales);
			if (!fitnessAgregado.isEmpty() && fitnessAgregado.get(0) >= 0) {
				System.out.println("Se ha encontrado una solución satisfactoria en la generación " + generacion);
				return poblacion.get(0);
			}

			List<Cromosoma> matingPool = TournamentSelection(poblacion, probabilidadSeleccion, tamanoTorneo,
					fitnessAgregado);
			List<Cromosoma> descendientes = new ArrayList<>();

			// Generar descendientes
			for (int j = 0; j < numDescendientes; j++) {
				int indexPadre1 = rand.nextInt(matingPool.size());
				int indexPadre2 = rand.nextInt(matingPool.size());
				if (Math.random() < probabilidadCruce) {
					List<Cromosoma> hijos = crossover(matingPool.get(indexPadre1), matingPool.get(indexPadre2));

					// Aplicar mutación con probabilidad probabilidadMutacion
					hijos.forEach(hijo -> {
						if (Math.random() < probabilidadMutacion) {
							mutarHijo(hijo, rutasPred); // Asumiendo que mutarHijos puede ahora manejar un solo hijo
						}
					});

					descendientes.addAll(hijos);
				}
			}

			// Reemplazar la población vieja con los descendientes para la siguiente
			// generación
			poblacion = new ArrayList<>(descendientes);
		}

		System.out
				.println("No se encontró una solución satisfactoria después de " + numGeneraciones + " generaciones.");
		return null; // Devolver el mejor cromosoma encontrado o null si no se encontró solución
	}

	public static void mutarHijo(Cromosoma hijo, List<RutaPredefinida> rutasDisponibles) {
		Random rand = new Random();
		double probabilidadMutacion = 0.1; // Probabilidad de mutación, ajustable según necesidades del algoritmo.

		if (Math.random() < probabilidadMutacion) {
			// Selecciona un gen (ruta) al azar para mutar.
			List<RutaPredefinida> claves = new ArrayList<>(hijo.getGen().keySet());
			RutaPredefinida rutaAMutar = claves.get(rand.nextInt(claves.size()));

			// Selecciona una nueva ruta diferente a la actual.
			RutaPredefinida nuevaRuta;
			do {
				nuevaRuta = rutasDisponibles.get(rand.nextInt(rutasDisponibles.size()));
			} while (nuevaRuta.equals(rutaAMutar));

			// Encuentra el paquete asociado a la ruta que se va a mutar y actualiza la
			// asignación.
			Paquete paqueteAMutar = hijo.getGen().get(rutaAMutar);
			hijo.getGen().remove(rutaAMutar);
			hijo.getGen().put(nuevaRuta, paqueteAMutar);
		}
	}

	private static List<Cromosoma> TournamentSelection(List<Cromosoma> poblacion, double ps, int tamanoTorneo,
			List<Double> fitnessAgregado) {
		List<Cromosoma> matingPool = new ArrayList<>();
		Random rand = new Random();
		for (int i = 0; i < poblacion.size(); i++) {
			List<Cromosoma> torneo = new ArrayList<>();
			for (int j = 0; j < tamanoTorneo; j++) {
				torneo.add(poblacion.get(rand.nextInt(poblacion.size())));
			}
			torneo.sort(Comparator.comparing(c -> fitnessAgregado.get(poblacion.indexOf(c))));
			matingPool.add(torneo.get(torneo.size() - 1)); // Agregar el de mejor fitness
		}
		return matingPool;
	}

	public static List<Cromosoma> crossover(Cromosoma padre1, Cromosoma padre2) {

		Map<RutaPredefinida, Paquete> genPadre1 = new HashMap<>(padre1.getGen());
		Map<RutaPredefinida, Paquete> genPadre2 = new HashMap<>(padre2.getGen());

		List<Map.Entry<RutaPredefinida, Paquete>> listaGenPadre1 = new ArrayList<>(genPadre1.entrySet());
		List<Map.Entry<RutaPredefinida, Paquete>> listaGenPadre2 = new ArrayList<>(genPadre2.entrySet());

		Random random = new Random();
		int puntoCruce = random.nextInt(listaGenPadre1.size());

		for (int i = puntoCruce; i < listaGenPadre1.size(); i++) {
			Map.Entry<RutaPredefinida, Paquete> temp = listaGenPadre1.get(i);
			listaGenPadre1.set(i, listaGenPadre2.get(i));
			listaGenPadre2.set(i, temp);
		}

		Map<RutaPredefinida, Paquete> genHijo1 = new HashMap<>();
		Map<RutaPredefinida, Paquete> genHijo2 = new HashMap<>();
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
