package src.entity;

import java.time.OffsetTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "planDeVuelo")
@SQLDelete(sql = "UPDATE planDeVuelo SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class PlanDeVueloEntity extends BaseEntity {

    @Column(name = "codigoIATAOrigen")
    private String codigoIATAOrigen;

    @Column(name = "codigoIATADestino")
    private String codigoIATADestino;

    @Column(name = "fechaHoraSalida")
    private OffsetTime horaSalida;

    @Column(name = "fechaHoraLlegada")
    private OffsetTime horaLlegada;

    @Column(name = "zonaHorariaSalida")
    private int zonaHorariaSalida;

    @Column(name = "zonaHorariaLlegada")
    private int zonaHorariaLlegada;

    @Column(name = "capacidad")
    private int capacidad;
}