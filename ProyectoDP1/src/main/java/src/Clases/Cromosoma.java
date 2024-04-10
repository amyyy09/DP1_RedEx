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

    public Cromosoma(Map<Ruta, Paquete> gen) {
        this.gen = gen;
        this.tamano = gen.size();
    }
    
    //quiero un contrscturo que me entregue el tamaño del cromosoma
    public int getTamano(){
        return gen.size();
    }
}


