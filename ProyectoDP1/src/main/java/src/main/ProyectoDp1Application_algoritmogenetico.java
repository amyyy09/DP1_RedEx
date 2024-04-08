package src.main;

//dependencias
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//utilidades
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//paquetes
import src.Clases.Paquete;
import src.Clases.PlanDeVuelo;
import src.Clases.Ruta;
import src.Clases.Almacen;
import src.Clases.Cromosoma;
import src.Clases.Vuelo;

@SpringBootApplication
public class ProyectoDp1Application_algoritmogenetico {


	public static void main(String[] args) throws IOException{
		System.out.println("hola mundo");
		System.err.println("hola mundo");
		List<Paquete> paquetes = new ArrayList<Paquete>();

		

		
	}
	
	public Cromosoma EjecutarAlgoritmo(List<Paquete> paquetes, List<Ruta> rutas, List <Almacen> alamcenes, List <PlanDeVuelo> vuelos,double Ps, double Pm, double Pc,int NumCromosomas, int NumTorneo, int NumDescendientes, int NumGeneraciones){
		List <Cromosoma> poblacion = new ArrayList<Cromosoma>();
		List<Paquete> paquetesCopia = new ArrayList<Paquete>();
		
		
		//generar poblacion inicial
		for(int i = 0; i < NumCromosomas; i++){
			paquetesCopia.addAll(paquetes);
			Cromosoma cromosoma = new Cromosoma();
			
			
			while (!paquetesCopia.isEmpty()){
				Random rand = new Random();
				int j = rand.nextInt(paquetesCopia.size());
				Paquete paquete = paquetesCopia.get(j);
				//cromosoma.addPaquete(paquete);
				//funcion asignarRutaPaqeuete(paquete, Cromosoma, Rutas)
				//preguntar a Amy ->busca la fila de su ruta, escoger al azar,  ver hora llegada, si la hora llegada es mayor al limite no la toma, si no busco aleatoriamente otra vez hasta el index anterior 
				
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
			//sobre la cant de paquetes 

			//order la poblacion por fitness tanto el lista de fitness como la poblacion
			
			if (fitnessAgregado.get(0)>=0){
				//revisar si el cromorosoma es valido -> crear funcion en relacion del almacen

				//se ha encontrado una solucion satisfacotria
				System.out.println("Se ha encontrado una solucion satisfactoria");
				return poblacion.get(0);
			}
			
			//formacion del matting pool (padres condidatos)
			List<Cromosoma> mattingPool = TournnamentSeleccion(poblacion,Ps,NumTorneo,fitnessAgregado);
			//NumTorneo es el porcentaje de la poblacion que se va a seleccionar

			List<Cromosoma> descendientes = new ArrayList<Cromosoma>();
			
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
						int padreM= rand.nextInt(hijos.size());
						//preguntar a Amy -> 
						//que hace el swapmutation: lo mismo de asignarRutaPaqeuete 
						
						//crossover escoge alatoriamente un padre 1 o 2 y padreM. Tner 2 hijos
						
					}
					descendientes.addAll(hijos);
				}
			}

			//determinacion de fitness de nueva poblacion total
			List<Double> fitnessAgregadoDescendientes= calcularFitnessAgregado(descendientes,alamcenes,vuelos);
			poblacion.addAll(descendientes);
			fitnessAgregado.addAll(fitnessAgregadoDescendientes);

			//ordernar para determinar mejores cromosomas


			//determinacion de futura generacion
			poblacion = poblacion.subList(0,NumCromosomas);
		}
	}

}
