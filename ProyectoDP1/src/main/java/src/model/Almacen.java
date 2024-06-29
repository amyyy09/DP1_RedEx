package src.model;

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
    private int capacidad;
    private int cantPaquetes;
    private List<Paquete> paquetes;

    public double verificarCapacidadAlmacen() {
        if (cantPaquetes > capacidad) {
            return (cantPaquetes - capacidad);
        }
        return 0.0;
    }

    public void actualizarCantPaquetes() {
        if (paquetes != null) {
            this.cantPaquetes = paquetes.size();
        } else {
            this.cantPaquetes = 0;
        }
    }
}
