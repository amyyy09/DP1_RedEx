package src.Clases;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Almacen {
    private int IDalmacen;
    private int capacidad;
    private int cantPaquetes;
    private Aeropuerto aeropuerto;
    private List<Paquete> paquetes;
}
