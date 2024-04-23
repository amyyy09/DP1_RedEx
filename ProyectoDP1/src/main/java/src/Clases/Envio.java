package src.Clases;

import java.util.List;
import java.time.LocalDateTime;

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
}
