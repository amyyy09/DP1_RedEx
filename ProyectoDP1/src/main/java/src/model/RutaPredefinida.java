package src.model;

import java.time.*;
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
    private boolean sameContinent;

    public RutaTiempoReal convertirAPredefinidaEnTiempoReal(List<Aeropuerto> aeropuertos, List<Vuelo> vuelosActuales, 
            LocalDateTime fechaHora) {
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
        // la fecha de inicio y fin saldrá dependerá de 
        // - la fecha que se ingresó en el método
        // - this.duracion (y hora de salida para ver si se pasa de la media noche)

        LocalDate fechaInicio = fechaHora.toLocalDate();
        if(this.horaSalida.isBefore(fechaHora.toLocalTime().atOffset(ZoneOffset.UTC))) {
            fechaInicio = fechaInicio.plusDays(1);
        }
        LocalDate fechaFin = fechaInicio.plusDays(this.duracion);
        LocalDateTime horaInicio = LocalDateTime.of(fechaInicio, horaSalida.toLocalTime());
        LocalDateTime horaFin = LocalDateTime.of(fechaFin, horaLlegada.toLocalTime());

        List<Vuelo> vuelos = new ArrayList<>(); // Esto debería ser poblado según lógica específica

        /* 
         * Lógica:
         * 1. Iterar sobre las escalas de la ruta predefinida
         * 2. Buscar si existe un vuelo de la lista vuelosActuales que coincide con indexPlan (con indexPlan en vuelosActuales) y fechaInicio (con horaInicio en vuelosActuales, se debe sacar solo la fecha de este último)
         * 3. Si coincide, agregarlo a la lista de vuelos
         * 4. Si no coincide, se añade el vuelo respectivo a la lista de vuelos actuales y se agrega a la lista de vuelos 
         */

        int duracion = 0;
        // servirá de referencia para la fecha de llegada previa
        LocalDateTime fechaInicioVuelo = horaInicio;

        for (PlanDeVuelo element : this.escalas) {
            Vuelo vuelo1 = null;

            if(fechaInicioVuelo.toLocalTime().isBefore(element.getHoraSalida().toLocalTime())) {
                fechaInicioVuelo = LocalDateTime.of(fechaInicioVuelo.toLocalDate().plusDays(1), element.getHoraSalida().toLocalTime());
            }
            else{
                fechaInicioVuelo = LocalDateTime.of(fechaInicioVuelo.toLocalDate(), element.getHoraSalida().toLocalTime());
            }

            for (Vuelo a : vuelosActuales) {
                if (a.getIndexPlan() == element.getIndexPlan() && a.getHoraSalida().toLocalDate().equals(fechaInicioVuelo.toLocalDate())) {
                    vuelo1 = a;
                    break;
                }
            }

            duracion = element.getHoraLlegada().isBefore(element.getHoraSalida()) ? 1 : 0;

            LocalDateTime fechaLlegada = LocalDateTime.of(fechaInicioVuelo.toLocalDate().plusDays(duracion), element.getHoraLlegada().toLocalTime());

            if(vuelo1 == null) {
                vuelo1 = new Vuelo();
                vuelo1.setIdVuelo(element.getIndexPlan() + "-" + fechaInicioVuelo.toLocalDate());
                vuelo1.setIndexPlan(element.getIndexPlan());
                vuelo1.setHoraSalida(fechaInicioVuelo);
                vuelo1.setHoraLlegada(fechaLlegada);
                vuelo1.setPlanDeVuelo(element);
                vuelo1.setStatus(0); // Status inicial, suponemos '0' para no activo
                vuelo1.setCapacidad(element.getCapacidad());
                vuelosActuales.add(vuelo1);
            }
            fechaInicioVuelo = fechaLlegada;
            vuelos.add(vuelo1);
        }

        // this.escalas.forEach((PlanDeVuelo element) -> {
        //     Vuelo vuelo1;
        //     vuelo1 = vuelosActuales.stream()
        //         .filter(a -> a.getPlanDeVuelo().equals(element))
        //         .findFirst()
        //         .orElse(null);
        //     vuelos.add(vuelo1);
        // });
        
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