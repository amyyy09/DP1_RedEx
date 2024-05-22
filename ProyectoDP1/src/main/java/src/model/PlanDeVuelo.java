package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import src.entity.PlanDeVueloEntity;
import lombok.NoArgsConstructor;
import java.time.OffsetTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDeVuelo {
        private String codigoIATAOrigen;
        private String codigoIATADestino;
        private OffsetTime horaSalida;
        private OffsetTime horaLlegada;
        private int capacidad;
        private boolean isSameContinent;

        public static PlanDeVuelo convertirPlanDeVueloFromEntity(PlanDeVueloEntity entity) {
                PlanDeVuelo plan = new PlanDeVuelo();
                plan.setCodigoIATAOrigen(entity.getCodigoIATAOrigen());
                plan.setCodigoIATADestino(entity.getCodigoIATADestino());
                plan.setHoraSalida(entity.getHoraSalida());
                plan.setHoraLlegada(entity.getHoraLlegada());
                plan.setCapacidad(entity.getCapacidad());
                // Assume other fields and initialization
                return plan;
        }
}
