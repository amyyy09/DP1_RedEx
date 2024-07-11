package src.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaqueteDiario {
    private String iD;
    private int status;
    private LocalDateTime horaInicio;
    private String aeropuertoOrigen;
    private String aeropuertoDestino;
    private String ruta;
}
