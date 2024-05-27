package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import src.entity.PlanDeVueloEntity;
import lombok.NoArgsConstructor;
import java.time.OffsetTime;
import java.time.ZoneOffset;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDeVuelo {
        private int id;
        private String codigoIATAOrigen;
        private String codigoIATADestino;
        private OffsetTime horaSalida;
        private OffsetTime horaLlegada;
        private int capacidad;
        private boolean isSameContinent;

        public static PlanDeVuelo convertirPlanDeVueloFromEntity(PlanDeVueloEntity entity) {
                PlanDeVuelo plan = new PlanDeVuelo();
                plan.setId(entity.getId());
                plan.setCodigoIATAOrigen(entity.getCodigoIATAOrigen());
                plan.setCodigoIATADestino(entity.getCodigoIATADestino());

                if (entity.getHoraSalida() != null && entity.getZonaHorariaSalida() != 0) {
                        OffsetTime salidaOffset = OffsetTime.of(entity.getHoraSalida().toLocalTime(),
                                        ZoneOffset.ofHours(entity.getZonaHorariaSalida()));
                        plan.setHoraSalida(salidaOffset);
                }

                if (entity.getHoraLlegada() != null && entity.getZonaHorariaLlegada() != 0) {
                        OffsetTime llegadaOffset = OffsetTime.of(entity.getHoraLlegada().toLocalTime(),
                                        ZoneOffset.ofHours(entity.getZonaHorariaLlegada()));
                        plan.setHoraLlegada(llegadaOffset);
                }

                plan.setCapacidad(entity.getCapacidad());

                return plan;
        }

}
