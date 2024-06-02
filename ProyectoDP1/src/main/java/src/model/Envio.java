package src.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHoraOrigen;
    private int zonaHorariaGMT;
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private int cantPaquetes;
    @JsonManagedReference
    private List<Paquete> paquetes;
}
