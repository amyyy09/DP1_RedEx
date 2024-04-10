package src.main;

//dependencias
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//utilidades
import java.io.IOException;
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


	public static void main(String[] args) throws IOException{
		System.out.println("hola mundo");
		System.err.println("hola mundo");
		List<Aeropuerto> aeropuertos = Aeropuerto.leerAeropuertos(); //listo
		List<PlanDeVuelo> planes = PlanDeVuelo.leerPlanesDeVuelo(aeropuertos); // listo
		List<RutaComun> rutas = RutaComun.generarRutas(aeropuertos, planes); 

		System.out.println("Rutas generadas " +  rutas.size());

		// for (RutaComun ruta : rutas) {
		// 	System.out.println(ruta.getAeropuertoOrigen().getCodAeropuerto() + " " + ruta.getAeropuertoDestino().getCodAeropuerto() + " " + ruta.getNdays());
		// 	for (PlanDeVuelo plan : ruta.getEscala().getPlanes()) {
		// 		System.out.println(plan.getIdPlan() + " " + plan.getHoraSalida() + " "  + plan.getAeropuertoOrigen().getNombre() + " " + plan.getHoraLlegada() + " " + plan.getAeropuertoDestino().getNombre());
		// 	}
		// }
		
	}
	//list<ruta> no es list es un jagger array
	// public Cromosoma EjecutarAlgoritmo(List<Paquete> paquetes, List<Ruta> rutas, List <Almacen> alamcenes, List <PlanDeVuelo> vuelos,double Ps, double Pm, double Pc,int NumCromosomas, int NumTorneo, int NumDescendientes, int NumGeneraciones){
	// 	List <Cromosoma> poblacion = new ArrayList<Cromosoma>();
	// 	List<Paquete> paquetesCopia = new ArrayList<Paquete>();
	
	public Cromosoma EjecutarAlgoritmo(List<Paquete> paquetes, List<Ruta> rutas, List <Almacen> alamcenes, List <PlanDeVuelo> vuelos,double Ps, double Pm, double Pc,int NumCromosomas, int NumTorneo, int NumDescendientes, int NumGeneraciones){
		List <Cromosoma> poblacion = new ArrayList<Cromosoma>();
		List<Paquete> paquetesCopia = new ArrayList<Paquete>();
		
		
	// 	//generar poblacion inicial
	// 	for(int i = 0; i < NumCromosomas; i++){
	// 		paquetesCopia.addAll(paquetes);
	// 		Cromosoma cromosoma = new Cromosoma();
			
			
			while (!paquetesCopia.isEmpty()){
				Random rand = new Random();
				int j = rand.nextInt(paquetesCopia.size());
				Paquete paquete = paquetesCopia.get(j);
				//PARA CADA PAQUETE ASIGANRLE UNA RUTA CON LA FUNCION ASIGNARRUTAPAQUETE
				//--------------------------------------funcion asignarRutaPaqeuete(paquete, Cromosoma, Rutas)---------------------------
				//busca la fila de su ruta, escoger al azar,  ver hora llegada, si la hora llegada es mayor al limite no la toma, si no busco aleatoriamente otra vez hasta el index anterior 
				//LUEGO AGREGAR EL GEN AL CROMOSOMA i
				paquetesCopia.remove(j);
			}
			
			poblacion.add(cromosoma);
		}

		//iteraciones evolutivas
		for(int i = 0; i < NumGeneraciones; i++){
			//determinar el fitness de la poblacion/generacion actual
			List<Double> fitnessAgregado= calcularFitnessAgregado(poblacion,alamcenes,vuelos);
			//preguntar a Amy -> que es el fitness agregado
			//ir por cada gen del cromosoma 
			//-------------------FUNCION ORDENAR CROMOSOMAS POR FITNESS---------------------------
			

	// 		while (!paquetesCopia.isEmpty()){
	// 			Random rand = new Random();
	// 			int j = rand.nextInt(paquetesCopia.size());
	// 			Paquete paquete = paquetesCopia.get(j);
	// 			//cromosoma.addPaquete(paquete);
	// 			//funcion asignarRutaPaqeuete(paquete, Cromosoma, Rutas)
	// 			//preguntar a Amy ->busca la fila de su ruta, escoger al azar,  ver hora llegada, si la hora llegada es mayor al limite no la toma, si no busco aleatoriamente otra vez hasta el index anterior 
				
	// 			paquetesCopia.remove(j);
	// 		}
			
	// 		poblacion.add(cromosoma);
	// 	}

	// 	//iteraciones evolutivas
	// 	for(int i = 0; i < NumGeneraciones; i++){
	// 		//determinar el fitness de la poblacion/generacion actual
	// 		List<Double> fitnessAgregado= calcularFitnessAgregado(poblacion,alamcenes,vuelos);
	// 		//preguntar a Amy -> que es el fitness agregado
	// 		//ir por cada gen del cromosoma
	// 		//sobre la cant de paquetes 

	// 		//order la poblacion por fitness tanto el lista de fitness como la poblacion
			
	// 		if (fitnessAgregado.get(0)>=0){
	// 			//se ha encontrado una solucion satisfacotria
	// 			System.out.println("Se ha encontrado una solucion satisfactoria");
	// 			return poblacion.get(0);
	// 		}
			if (fitnessAgregado.get(0)>=0){
				//revisar si el cromorosoma es valido -> crear funcion en relacion del almacen

				//se ha encontrado una solucion satisfacotria
				System.out.println("Se ha encontrado una solucion satisfactoria");
				return poblacion.get(0);
			}
			
	// 		//formacion del matting pool (padres condidatos)
	// 		List<Cromosoma> mattingPool = TournnamentSeleccion(poblacion,Ps,NumTorneo,fitnessAgregado);
	// 		//NumTorneo es el porcentaje de la poblacion que se va a seleccionar

	// 		List<Cromosoma> descendientes = new ArrayList<Cromosoma>();
			
			//iteraciones para generacion de descenetientes
			for(int j = 0; j < NumDescendientes/2; j++){
				//seleccion aleatoria de pareda de padres del matting pool
				Random rand = new Random();
				int padre1 = rand.nextInt(mattingPool.size());
				int padre2 = rand.nextInt(mattingPool.size());

				//cruzamiento bajo probabilidad Pc
				double n=Math.random();//numeros aleatorios entre 0 y 1

				if (Pc<n){
					List<Cromosoma> hijos = crossover(mattingPool.get(padre1),mattingPool.get(padre2));//tiene que devolver dos hijos.

					//mutacion bajo probabilidad Pm
					double m=Math.random();//numeros aleatorios entre 0 y 1	
					if (Pm<m){

						//crea una copia de hijos
						List<Cromosoma> hijosCopia = new ArrayList<Cromosoma>();
						hijosCopia.addAll(hijos);
						//borrra lo que hay en hijos
						hijos.clear();

						int indexPadreM= rand.nextInt(hijosCopia.size());
						Cromosoma padreM = hijosCopia.get(indexPadreM);
						
						//---------------------------FUNCION asignarRutaPaqeuete AL PADREM---------------------

						//crossover escoge alatoriamente un padre 1 o 2 y padreM. Tner 2 hijos
						int azar = rand.nextInt(2);
						if(azar==0){
							hijos=crossover(padreM,hijosCopia.get(0));
						}else{
							hijos=crossover(padreM,hijosCopia.get(1));
						}
					}
					descendientes.addAll(hijos);
				}
			}
	// 		//iteraciones para generacion de descenetientes
	// 		for(int j = 0; j < NumDescendientes/2; j++){
	// 			//seleccion aleatoria de pareda de padres del matting pool
	// 			Random rand = new Random();
	// 			int padre1 = rand.nextInt(mattingPool.size());
	// 			int padre2 = rand.nextInt(mattingPool.size());

	// 			//cruzamiento bajo probabilidad Pc
	// 			double n=Math.random();//numeros aleatorios entre 0 y 1

	// 			if (Pc<n){
	// 				List<Cromosoma> hijos = crossover(mattingPool.get(padre1),mattingPool.get(padre2));//tiene que devolver dos hijos.

	// 				//mutacion bajo probabilidad Pm
	// 				double m=Math.random();//numeros aleatorios entre 0 y 1	
	// 				if (Pm<m){
	// 					int padreM= rand.nextInt(hijos.size());
	// 					//preguntar a Amy -> 
	// 					//que hace el swapmutation: lo mismo de asignarRutaPaqeuete 
						
	// 					//crossover escoge alatoriamente un padre 1 o 2 y padreM. Tner 2 hijos
						
	// 				}
	// 				descendientes.addAll(hijos);
	// 			}
	// 		}

	// 		//determinacion de fitness de nueva poblacion total
	// 		List<Double> fitnessAgregadoDescendientes= calcularFitnessAgregado(descendientes,alamcenes,vuelos);
	// 		poblacion.addAll(descendientes);
	// 		fitnessAgregado.addAll(fitnessAgregadoDescendientes);
			//ordernar para determinar mejores cromosomas
			//----------------------FUNCION ORDERNAR CROMOSOMAS POR FITNESS----------------------------------------
	// 		//ordernar para determinar mejores cromosomas

	// 		//determinacion de futura generacion
	// 		poblacion = poblacion.subList(0,NumCromosomas);
	// 	}
	// }

	// haz la funcion TournnamentSeleccion que tiene estos parametros oblacion,Ps,NumTorneo,fitnessAgregado) donde Ps es la probabilidad de seleccion para el torneo, NumTorneo es el porcentaje de la poblacion que se va a seleccionar
	// y fitnessAgregado es el fitness de cada cromosoma de la poblacion

	public List <Cromosoma> TournnamentSeleccion(List<Cromosoma> poblacion, double Ps, int NumTorneo, List<Double> fitnessAgregado){
		List <Cromosoma> mattingPool = new ArrayList<Cromosoma>();
		int cantidadSeleccion = (int) (poblacion.size()*NumTorneo/100);
		while(cantidadSeleccion!=0){

			List <Cromosoma> torneo = new ArrayList<Cromosoma>();
			List <Double> fitnessTorneo = new ArrayList<Double>();
			for(int i=0; i<poblacion.size(); i++){
				Random rand = new Random();
				int j = rand.nextInt(poblacion.size());
				double n=Math.random();//numeros aleatorios entre 0 y 1
				if (Ps<n){
					torneo.add(poblacion.get(j));
					fitnessTorneo.add(fitnessAgregado.get(j));
				}
			}
			int max =0;
			for (int i=0; i<torneo.size(); i++){
				if(fitnessTorneo.get(i)>fitnessTorneo.get(max)){
					max = i;
				}
			}
			mattingPool.add(torneo.get(max));
			cantidadSeleccion--;
		}
		return mattingPool;
	}

	// haz la funcion crossover que tiene estos parametros (Cromosoma padre1, Cromosoma padre2) y devuelve una dos cromosomas hijos dentro de una lista
	public List<Cromosoma> crossover(Cromosoma padre1, Cromosoma padre2){


		// Crear copias de los mapas para no modificar los originales
        Map<Ruta, Paquete> genPadre1 = new HashMap<>(padre1.getGen());
        Map<Ruta, Paquete> genPadre2 = new HashMap<>(padre2.getGen());

        // Convertir las entradas de los mapas en listas para poder acceder por índice
        List<Map.Entry<Ruta, Paquete>> listaGenPadre1 = new ArrayList<>(genPadre1.entrySet());
        List<Map.Entry<Ruta, Paquete>> listaGenPadre2 = new ArrayList<>(genPadre2.entrySet());

        // Elegir un punto de cruce aleatorio
        Random random = new Random();
        int puntoCruce = random.nextInt(listaGenPadre1.size());

        // Intercambiar las partes después del punto de cruce
        for (int i = puntoCruce; i < listaGenPadre1.size(); i++) {
            Map.Entry<Ruta, Paquete> temp = listaGenPadre1.get(i);
            listaGenPadre1.set(i, listaGenPadre2.get(i));
            listaGenPadre2.set(i, temp);
        }

        // Convertir las listas modificadas de nuevo en mapas
        Map<Ruta, Paquete> genHijo1 = new HashMap<>();
        Map<Ruta, Paquete> genHijo2 = new HashMap<>();
        for (int i = 0; i < listaGenPadre1.size(); i++) {
            genHijo1.put(listaGenPadre1.get(i).getKey(), listaGenPadre1.get(i).getValue());
            genHijo2.put(listaGenPadre2.get(i).getKey(), listaGenPadre2.get(i).getValue());
        }

        // Crear y devolver los nuevos cromosomas hijos
        Cromosoma hijo1 = new Cromosoma(genHijo1);
        Cromosoma hijo2 = new Cromosoma(genHijo2);
        List<Cromosoma> hijos = new ArrayList<>();
        hijos.add(hijo1);
        hijos.add(hijo2);

        return hijos;
	}

}
