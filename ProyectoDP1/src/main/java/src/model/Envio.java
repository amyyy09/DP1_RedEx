package src.model;
import src.utility.ConvertirALocal;

import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Envio {
    private String idEnvio;
    private LocalDateTime fechaHoraOrigen;
    private int zonaHorariaGMT;
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private int cantPaquetes;
    private List<Paquete> paquetes;

    public LocalTime getHoraUTC(){
        return ConvertirALocal.convertirALocal(this.fechaHoraOrigen, this.zonaHorariaGMT);
    }
    public OffsetDateTime getFechaHoraLocal() {
        // Obtiene la zona horaria como ZoneOffset
        ZoneOffset zona = ZoneOffset.ofTotalSeconds(this.zonaHorariaGMT * 3600);
        // Ajusta la fecha y hora local del envío a la zona horaria especificada
        return OffsetDateTime.of(this.fechaHoraOrigen, zona);
    }
    public LocalDateTime getFechaHoraLocalDateTime() {
        // Obtiene la zona horaria como ZoneOffset
        ZoneOffset zona = ZoneOffset.ofTotalSeconds(this.zonaHorariaGMT * 3600);
        // Ajusta la fecha y hora local del envío a la zona horaria especificada
        return LocalDateTime.of(this.fechaHoraOrigen.toLocalDate(), this.fechaHoraOrigen.toLocalTime()).atOffset(zona).toLocalDateTime();
    }
}
