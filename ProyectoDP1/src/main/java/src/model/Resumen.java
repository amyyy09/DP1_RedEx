package src.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Resumen {
    private int numeroVuelos;
    private int totalPaquetes;
    private String aeropuertoMasFrecuente;
    private int horaConMasVuelos;
    private double promedioPaquetesPorVuelo;
    private double tiempoPromedioVuelo;
}
