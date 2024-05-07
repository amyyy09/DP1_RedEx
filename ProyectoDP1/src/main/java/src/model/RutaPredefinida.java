package src.model;

import java.time.LocalDateTime;
import java.time.*;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RutaPredefinida {
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private OffsetTime horaSalida;
    private OffsetTime horaLlegada;
    private List<PlanDeVuelo> escalas;
    private long duracion;

    public RutaTiempoReal convertirAPredefinidaEnTiempoReal(List<Aeropuerto> aeropuertos, List<Vuelo> vuelosActuales) {
        RutaTiempoReal rutaTiempoReal = new RutaTiempoReal();
        
        // Suponemos que tienes una forma de obtener los objetos Aeropuerto basados en
        // el código IATA
        Aeropuerto origen = aeropuertos.stream()
                .filter(a -> a.getCodigoIATA().equals(this.getCodigoIATAOrigen()))
                .findFirst()
                .orElse(null);
        Aeropuerto destino = aeropuertos.stream()
                .filter(a -> a.getCodigoIATA().equals(this.getCodigoIATADestino()))
                .findFirst()
                .orElse(null);

        // Asumimos que cada aeropuerto tiene un almacén asociado y podemos obtenerlo
        // directamente
        Almacen almacenOrigen = (origen != null) ? origen.getAlmacen() : null;
        LocalDateTime horaInicio = LocalDateTime.of(LocalDate.now(), horaSalida.toLocalTime());
        LocalDateTime horaFin = LocalDateTime.of(LocalDate.now(), horaSalida.toLocalTime());

        List<Vuelo> vuelos = new ArrayList<>(); // Esto debería ser poblado según lógica específica
        this.escalas.forEach((PlanDeVuelo element) -> {
            Vuelo vuelo1;
            vuelo1 = vuelosActuales.stream()
                .filter(a -> a.getPlanDeVuelo().equals(element))
                .findFirst()
                .orElse(null);
            vuelos.add(vuelo1);
        });
        rutaTiempoReal.setIdRuta(1); // Generar un ID aleatorio o de alguna otra forma
        rutaTiempoReal.setOrigen(origen);
        rutaTiempoReal.setDestino(destino);
        rutaTiempoReal.setXAlmacen(almacenOrigen);
        rutaTiempoReal.setHoraInicio(horaInicio);
        rutaTiempoReal.setHoraLlegada(horaFin);
        rutaTiempoReal.setVuelos(vuelos);
        rutaTiempoReal.setRutaPredefinida(this);
        rutaTiempoReal.setStatus(0); // Status inicial, suponemos '0' para no activa

        return rutaTiempoReal;
    }

}