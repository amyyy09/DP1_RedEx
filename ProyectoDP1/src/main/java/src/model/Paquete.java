package src.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Paquete {
    private String iD;
    private int status;
    private LocalDateTime horaInicio;
    private String aeropuertoOrigen;
    private String aeropuertoDestino;
}
