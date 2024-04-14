package src.Clases;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DatosAeropuertos {
        public static List<Aeropuerto> obtenerAeropuertos() {
                List<Aeropuerto> aeropuertos = new ArrayList<>();
                aeropuertos.add(new Aeropuerto("SEQM", "Quito", "Ecuador", "America del Sur", "quit", -5));
                aeropuertos.add(new Aeropuerto("SVMI", "Caracas", "Venezuela", "America del Sur", "cara", -4));
                aeropuertos.add(new Aeropuerto("SBBR", "Brasilia", "Brasil", "America del Sur", "bras", -3));
                aeropuertos.add(new Aeropuerto("SPIM", "Lima", "Perú", "America del Sur", "lima", -5));
                aeropuertos.add(new Aeropuerto("SLLP", "La Paz", "Bolivia", "America del Sur", "lapa", -4));
                aeropuertos.add(new Aeropuerto("SCEL", "Santiago de Chile", "Chile", "America del Sur", "sant", -3));
                aeropuertos.add(new Aeropuerto("SABE", "Buenos Aires", "Argentina", "America del Sur", "buen", -3));
                aeropuertos.add(new Aeropuerto("SGAS", "Asunción", "Paraguay", "America del Sur", "asun", -4));
                aeropuertos.add(new Aeropuerto("SUAA", "Montevideo", "Uruguay", "America del Sur", "mont", -3));
                return aeropuertos;
        }

        public static List<Almacen> obtenerAlmacenes() {
                List<Almacen> almacenes = new ArrayList<>();
                almacenes.add(new Almacen("SEQM", 410, 0, new ArrayList<Paquete>()));
                almacenes.add(new Almacen("SVMI", 400, 0, new ArrayList<Paquete>()));
                almacenes.add(new Almacen("SBBR", 480, 0, new ArrayList<Paquete>()));
                almacenes.add(new Almacen("SPIM", 440, 0, new ArrayList<Paquete>()));
                almacenes.add(new Almacen("SLLP", 420, 0, new ArrayList<Paquete>()));
                almacenes.add(new Almacen("SCEL", 460, 0, new ArrayList<Paquete>()));
                almacenes.add(new Almacen("SABE", 460, 0, new ArrayList<Paquete>()));
                almacenes.add(new Almacen("SGAS", 400, 0, new ArrayList<Paquete>()));
                almacenes.add(new Almacen("SUAA", 400, 0, new ArrayList<Paquete>()));

                return almacenes;
        }

}
