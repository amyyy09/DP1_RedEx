package src.entity;

import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "envio")
@SQLDelete(sql = "UPDATE envio SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EnvioEntity extends BaseEntity {
    @Column(name = "idEnvio")
    private String idEnvio;

    @Column(name = "fechaHoraOrigen")
    private LocalDateTime fechaHoraOrigen;

    @Column(name = "zonaHorariaGMT")
    private int zonaHorariaGMT;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_aeropuertoOrigen", referencedColumnName = "id")
    private AeropuertoEntity aeropuertoOrigen;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_aeropuertoDestino", referencedColumnName = "id")
    private AeropuertoEntity aeropuertoDestino;

    @Column(name = "cantPaquetes")
    private int cantPaquetes;

}
