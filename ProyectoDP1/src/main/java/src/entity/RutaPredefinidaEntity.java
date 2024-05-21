package src.entity;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

public class RutaPredefinidaEntity extends BaseEntity {
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
