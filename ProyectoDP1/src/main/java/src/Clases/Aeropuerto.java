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

public class Aeropuerto {
    private int idAeropuerto;
    private String nombre;
    private String codAeropuerto;

    private Almacen almacen;
    

}
