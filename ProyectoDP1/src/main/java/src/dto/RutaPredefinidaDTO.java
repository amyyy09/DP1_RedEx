package src.dto;

import java.time.OffsetTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rutaPredefinida")
@SQLDelete(sql = "UPDATE rutaPredefinida SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter


public class RutaPredefinidaDTO extends BaseEntity{
    @Column(name = "codigoIATAOrigen")
    private String codigoIATAOrigen;

    @Column(name = "codigoIATADestino")
    private String codigoIATADestino;

    @Column(name = "horaSalida")
    private OffsetTime horaSalida;

    @Column(name = "horaLlegada")
    private OffsetTime horaLlegada;

    @Column(name = "duracion")
    private int duracion;

    @Column(name = "isSameContinente")
    private boolean isSameContinente;  
    
}
