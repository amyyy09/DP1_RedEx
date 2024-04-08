package src.Clases;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cromosoma {
    Map <Ruta,Paquete> gen;
    private int tamano;
}
