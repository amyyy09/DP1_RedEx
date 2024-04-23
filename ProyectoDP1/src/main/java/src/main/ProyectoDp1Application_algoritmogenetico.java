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
					System.out.println("Resultado del algoritmo genético procesado.");
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
		double ps = 0.7; // Probabilidad de selección
		double pm = 0.1; // Probabilidad de mutación
		double pc = 0.85; // Probabilidad de cruzamiento
		int numCromosomas = 100; // Número de cromosomas en la población
		int numTorneo = 5; // Tamaño del torneo
		int numDescendientes = 50; // Número de descendientes a generar
		int numGeneraciones = 20; // Número de generaciones

		List<RutaPredefinida> rutasPred = RutaPredefinida.obtenerRutasConEscalas(aeropuertos);
		List<Cromosoma> poblacion = Cromosoma.createPopulation(envios, rutasPred, numCromosomas, aeropuertos);
		// CARGA POBLACION???

		for (int i = 0; i < numGeneraciones; i++) {
			List<Double> fitnessagregado = evaluator.calcularFitnessAgregado(poblacion, aeropuertos, vuelosActuales);
			if (!fitnessagregado.isEmpty() && fitnessagregado.get(0) >= 0) {
				System.out.println("se ha encontrado una solución satisfactoria");
				return poblacion.get(0);
			}

			List<Cromosoma> matingpool = TournnamentSeleccion(poblacion, ps, numTorneo, fitnessagregado);
			List<Cromosoma> descendientes = new ArrayList<Cromosoma>();

			// iteraciones para generación de descendientes
			for (int j = 0; j < numDescendientes / 2; j++) {
				// selección aleatoria de pareja de padres del mating pool
				Random rand = new Random();
				int padre1 = rand.nextInt(matingpool.size());
				int padre2 = rand.nextInt(matingpool.size());

				// cruzamiento bajo probabilidad pc
				if (Math.random() < pc) {
					List<Cromosoma> hijos = crossover(matingpool.get(padre1), matingpool.get(padre2));

					// mutación bajo probabilidad pm
					if (Math.random() < pm) {
						// mutarhijos(hijos, rutas);
					}
					descendientes.addAll(hijos);
				}
			}

		}

		System.out.println("no se encontró una solución satisfactoria.");
		return null; // o cualquier otra acción como devolver el mejor encontrado
	}

	public void mutarHijos(List<Cromosoma> hijos, List<RutaTiempoReal> rutasDisponibles) {
		Random rand = new Random();
		for (Cromosoma hijo : hijos) {
			// Convertir las claves del mapa (Ruta) a una lista para poder seleccionar una
			// al azar
			List<RutaTiempoReal> rutasActuales = new ArrayList<>(hijo.getGen().keySet());
			int indexRutaAMutar = rand.nextInt(rutasActuales.size());
			RutaTiempoReal rutaAMutar = rutasActuales.get(indexRutaAMutar);

			// Seleccionar una nueva ruta que sea diferente a la actual
			RutaTiempoReal nuevaRuta;
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

	public static List<Cromosoma> TournnamentSeleccion(List<Cromosoma> poblacion, double Ps, int NumTorneo,
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

	public static List<Cromosoma> crossover(Cromosoma padre1, Cromosoma padre2) {

		Map<RutaTiempoReal, Paquete> genPadre1 = new HashMap<>(padre1.getGen());
		Map<RutaTiempoReal, Paquete> genPadre2 = new HashMap<>(padre2.getGen());

		List<Map.Entry<RutaTiempoReal, Paquete>> listaGenPadre1 = new ArrayList<>(genPadre1.entrySet());
		List<Map.Entry<RutaTiempoReal, Paquete>> listaGenPadre2 = new ArrayList<>(genPadre2.entrySet());

		Random random = new Random();
		int puntoCruce = random.nextInt(listaGenPadre1.size());

		for (int i = puntoCruce; i < listaGenPadre1.size(); i++) {
			Map.Entry<RutaTiempoReal, Paquete> temp = listaGenPadre1.get(i);
			listaGenPadre1.set(i, listaGenPadre2.get(i));
			listaGenPadre2.set(i, temp);
		}

		Map<RutaTiempoReal, Paquete> genHijo1 = new HashMap<>();
		Map<RutaTiempoReal, Paquete> genHijo2 = new HashMap<>();
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
