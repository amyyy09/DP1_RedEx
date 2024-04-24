package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vuelo {
    private int IdVuelo;
    private int cantPaquetes;
    private int capacidad;
    private int status;
    private PlanDeVuelo planDeVuelo;
}