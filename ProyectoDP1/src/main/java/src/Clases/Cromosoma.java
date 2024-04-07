package src.Clases;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cromosoma {
    private List<Ruta,paquete> rutas;
    //dupla de ruta y paquete
    private int tamano;
}
