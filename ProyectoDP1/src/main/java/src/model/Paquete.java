package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Paquete {
    private String idEnvio;
    private int status;
    private Envio envio;
    private String codigoIATADestino;

    public Paquete(String idEnvio, int status) {
        this.idEnvio = idEnvio;
        this.status = status;
    }
}
