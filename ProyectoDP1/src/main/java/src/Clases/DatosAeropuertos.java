package src.Clases;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DatosAeropuertos {
        public static List<Aeropuerto> getAeropuertosInicializados() {
                List<Aeropuerto> aeropuertos = new ArrayList<>();
                aeropuertos.add(new Aeropuerto("SEQM", "Quito", "Ecuador", "America del Sur", "quit", -5, new Almacen(410, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SVMI", "Caracas", "Venezuela", "America del Sur", "cara", -4, new Almacen(400, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SBBR", "Brasilia", "Brasil", "America del Sur", "bras", -3, new Almacen(480, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SPIM", "Lima", "Perú", "America del Sur", "lima", -5, new Almacen(440, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SLLP", "La Paz", "Bolivia", "America del Sur", "lapa", -4, new Almacen(420, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SCEL", "Santiago de Chile", "Chile", "America del Sur", "sant", -3, new Almacen(460, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SABE", "Buenos Aires", "Argentina", "America del Sur", "buen", -3, new Almacen(460, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SGAS", "Asunción", "Paraguay", "America del Sur", "asun", -4, new Almacen(400, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SUAA", "Montevideo", "Uruguay", "America del Sur", "mont", -3, new Almacen(400, 0, new ArrayList<>())));
                return aeropuertos;
            }

}
