package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDeVuelo {
        // private int idPlanVuelo;
        private String codigoIATAOrigen;
        private String codigoIATADestino;
        private OffsetTime horaSalida;
        private OffsetTime horaLlegada;
        private int capacidad;
        private boolean isSameContinent;

        // Método para obtener la hora local de salida
        public LocalTime obtenerHoraLocalSalida() {
                return convertirALocal(horaSalida);
        }

        // Método para obtener la hora local de llegada
        public LocalTime obtenerHoraLocalLlegada() {
                return convertirALocal(horaLlegada);
        }

        // Método para convertir OffsetTime a hora local
        private LocalTime convertirALocal(OffsetTime horaOffset) {
                return horaOffset.withOffsetSameInstant(ZoneOffset.UTC).toLocalTime();
        }
}
