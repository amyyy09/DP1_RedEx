package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Aeropuerto {
    private String codigoIATA;
    private String ciudad;
    private String pais;
    private String continente;
    private String alias;
    private int zonaHorariaGMT;
    private Almacen almacen;
}