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
public class Escala {

    private RutaComun ruta;
    private List<PlanDeVuelo> planes;

    
}
