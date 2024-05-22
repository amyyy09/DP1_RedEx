package src.model;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import src.entity.AeropuertoEntity;
import src.entity.EnvioEntity;
import src.entity.PaqueteEntity;
import src.service.AeropuertoService;
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

    public static EnvioEntity convertirAeropuetoToDTO(Envio envio) {
        EnvioEntity envioEntity = new EnvioEntity();
        envioEntity.setIdEnvio(envio.getIdEnvio());
        envioEntity.setFechaHoraOrigen(envio.getFechaHoraOrigen());
        envioEntity.setZonaHorariaGMT(envio.getZonaHorariaGMT());

        // Assume AeropuertoEntity has a static method to fetch by IATA code

        envioEntity.getAeropuertoOrigen().setCodigoIATA(envio.getCodigoIATAOrigen());
        envioEntity.getAeropuertoDestino().setCodigoIATA(envio.getCodigoIATADestino());

        envioEntity.setCantPaquetes(envio.getCantPaquetes());

        // Convert Paquetes list

        return envioEntity;
    }
}
