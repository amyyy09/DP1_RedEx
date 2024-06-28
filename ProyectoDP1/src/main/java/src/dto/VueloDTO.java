package src.dto;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import src.model.Paquete;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VueloDTO{
    private int indexPlan;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLLegada;
    private String aeropuertoSalida;
    private String aeropuertoDestino;
}
