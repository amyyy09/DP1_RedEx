package src.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "envios")
public class EnvioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idEnvio;
    
    @Column(name = "fecha_hora_origen")
    private LocalDateTime fechaHoraOrigen;
    
    private int zonaHorariaGMT;
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private int cantPaquetes;

    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL)
    private List<PaqueteEntity> paquetes;

    // Getters y Setters
    // ...
}