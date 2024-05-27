package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.OffsetTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDeVuelo {
        private int indexPlan;
        private String codigoIATAOrigen;
        private String codigoIATADestino;
        private OffsetTime horaSalida;
        private OffsetTime horaLlegada;
        private int capacidad;
        private boolean isSameContinent;
}
