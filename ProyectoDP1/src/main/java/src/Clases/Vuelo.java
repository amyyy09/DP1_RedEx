package src.Clases;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vuelo {
    private int cantPaquetes;
    private double porcSaturacion;

    private Ruta ruta;
    private PlanDeVuelo planDeVuelo;
}
