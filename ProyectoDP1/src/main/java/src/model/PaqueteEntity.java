package src.model;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paquetes")
public class PaqueteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identificacion;
    private int status;
    private LocalDateTime horaInicio;
    private String aeropuertoOrigen;
    private String aeropuertoDestino;
    private String ruta;

    @ManyToOne
    @JoinColumn(name = "envio_id")
    private EnvioEntity envio;
}