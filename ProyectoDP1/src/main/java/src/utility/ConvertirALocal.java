package src.utility;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ConvertirALocal {
    

    public static LocalTime convertirALocal(LocalDateTime fechaHora, int zonaHorariaGMT) {
    // Crear una ZonedDateTime usando la fechaHora con un ZoneOffset basado en zonaHorariaGMT
        ZonedDateTime zdt = fechaHora.atZone(ZoneOffset.ofHours(zonaHorariaGMT));
        // Convertir la ZonedDateTime a UTC (Coordinated Universal Time)
        ZonedDateTime zdtUtc = zdt.withZoneSameInstant(ZoneOffset.UTC);
        // Retornar solo la parte de tiempo de la ZonedDateTime en UTC
        return zdtUtc.toLocalTime();
    }
}
