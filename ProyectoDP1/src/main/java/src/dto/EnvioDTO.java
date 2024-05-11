package src.dto;

import java.time.LocalDateTime;

public class EnvioDTO {
    private String idEnvio;
    private LocalDateTime fechaHoraOrigen;
    private int zonaHorariaGMT;
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private int cantPaquetes;

}
