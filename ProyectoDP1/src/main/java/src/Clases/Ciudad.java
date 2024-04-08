package src.Clases;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ciudad {
    private int idCiudad;
    private String nombre;
    private String codCiudad;
    private ZoneOffset GMT;
    private Pais pais;
    private int GMT;
}
