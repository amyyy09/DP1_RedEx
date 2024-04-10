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
        aeropuertos.add(new Aeropuerto("SKBO", "Bogota", "Colombia", "America del Sur", "bogo", -5, 430));
        aeropuertos.add(new Aeropuerto("SEQM", "Quito", "Ecuador", "America del Sur", "quit", -5, 410));
        return aeropuertos;
    }
}
