package src.Clases;
import java.util.List;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pais {
    private int idPais;
    private String nombre;
    private String codigoPostal;
    private List<Ciudad> ciudades;
    private Continente continente;
}
