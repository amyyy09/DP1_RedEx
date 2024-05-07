package src.utility;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import src.model.Aeropuerto;
import src.model.Envio;
import src.model.PlanDeVuelo;
import src.model.RutaPredefinida;

public class AsignarRutas {
    Random random = new Random();
    public RutaPredefinida buscarRutaRecursiva(Envio envio, List<PlanDeVuelo> vuelosDisponibles, String origenActual, String destinoFinal, LocalTime tiempoMinimo, List<Aeropuerto> aeropuertos) {
        List<PlanDeVuelo> vuelosValidos = new ArrayList<>();
        //el parametro localtime solo se envia la hora y no la fecha
        for (PlanDeVuelo vuelo : vuelosDisponibles) {
            if (vuelo.getCodigoIATAOrigen().equals(origenActual)){
                System.out.println("origen): "+vuelo.getCodigoIATAOrigen());
                System.out.println("vuelo.obtenerHoraLocalSalida(): "+vuelo.obtenerHoraLocalSalida());}
            if (vuelo.getCodigoIATAOrigen().equals(origenActual) && ( vuelo.obtenerHoraLocalSalida().equals(tiempoMinimo) || vuelo.obtenerHoraLocalSalida().isAfter(tiempoMinimo))) {
                vuelosValidos.add(vuelo);
            }
            
        }

        

        for (PlanDeVuelo vuelo : vuelosValidos) {
            if (vuelo.getCodigoIATADestino().equals(destinoFinal)) {
                List<PlanDeVuelo> escalas = new ArrayList<>();
                escalas.add(vuelo);
                return new RutaPredefinida(origenActual, destinoFinal, vuelo.getHoraSalida(), vuelo.getHoraLlegada(), escalas,0 /*calcularDuracion(escalas)*/);
            } else {
                LocalTime nuevaHoraMinima = vuelo.obtenerHoraLocalLlegada(); 
                if (nuevaHoraMinima.isAfter(tiempoMinimo)) {
                    RutaPredefinida rutaConEscalas = buscarRutaRecursiva(envio, vuelosDisponibles, vuelo.getCodigoIATADestino(), destinoFinal, nuevaHoraMinima, aeropuertos);
                    if (rutaConEscalas != null) {
                        List<PlanDeVuelo> escalas = new ArrayList<>();
                        escalas.add(vuelo);
                        escalas.addAll(rutaConEscalas.getEscalas());
                        return new RutaPredefinida(origenActual, destinoFinal, vuelo.getHoraSalida(), rutaConEscalas.getHoraLlegada(), escalas, 0/*calcularDuracion(escalas)*/);
                    }
                }
            }
        }

        return null;
    }

    public int buscarzonahorariaGMT( String codigoIATA, List<Aeropuerto> aeropuertos){
        for (Aeropuerto aeropuerto : aeropuertos) {
            if (aeropuerto.getCodigoIATA().equals(codigoIATA)) {
                return aeropuerto.getZonaHorariaGMT();
            }
        }
        return 0;
    }

   public RutaPredefinida buscarRutaAleatoria(Envio envio, List<PlanDeVuelo> vuelosDisponibles, Random random) {
    List<RutaPredefinida> todasLasRutas = new ArrayList<>();
    OffsetDateTime tiempoMinimo = envio.getFechaHoraLocal();
    OffsetDateTime tiempoMaximo = tiempoMinimo.plusDays(1); // Plazo máximo de un día desde el origen

    for (PlanDeVuelo vuelo : vuelosDisponibles) {
        //quiero crear un zonaoofser de 0
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(0);
        OffsetDateTime horaSalidaVuelo = OffsetDateTime.of(tiempoMinimo.toLocalDate(), vuelo.obtenerHoraLocalSalida(), offset);

        // Ajusta el día de salida si la hora del vuelo ya pasó para ese día
        if (horaSalidaVuelo.isBefore(tiempoMinimo)) {
            horaSalidaVuelo = horaSalidaVuelo.plusDays(1);
        }

        if (vuelo.getCodigoIATAOrigen().equals(envio.getCodigoIATAOrigen()) && horaSalidaVuelo.isAfter(tiempoMinimo) && horaSalidaVuelo.isBefore(tiempoMaximo)) {
            OffsetDateTime horaLlegadaVuelo = OffsetDateTime.of(tiempoMinimo.toLocalDate(), vuelo.obtenerHoraLocalLlegada(), offset);
            if (horaLlegadaVuelo.isBefore(horaSalidaVuelo)) {
                horaLlegadaVuelo = horaLlegadaVuelo.plusDays(1);
            }

            List<PlanDeVuelo> nuevaRutaActual = new ArrayList<>();
            nuevaRutaActual.add(vuelo);
            
            if (vuelo.getCodigoIATADestino().equals(envio.getCodigoIATADestino())) {
                long duracion = ChronoUnit.MINUTES.between(horaSalidaVuelo, horaLlegadaVuelo);
                todasLasRutas.add(new RutaPredefinida(envio.getCodigoIATAOrigen(), envio.getCodigoIATADestino(), horaSalidaVuelo.toOffsetTime(), horaLlegadaVuelo.toOffsetTime(), nuevaRutaActual, duracion));
            } else {
                // Considera rutas con escalas aquí si es necesario, similar al proceso anterior
            }
        }
    }

    // Devuelve una ruta aleatoria si se encontraron rutas válidas
    if (!todasLasRutas.isEmpty()) {
        return todasLasRutas.get(random.nextInt(todasLasRutas.size()));
    } else {
        return null;  // No se encontraron rutas válidas
    }
}
}
