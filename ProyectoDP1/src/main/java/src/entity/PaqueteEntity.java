package src.entity;

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
@Table(name = "paquete")
@SQLDelete(sql = "UPDATE paquete SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class PaqueteEntity extends BaseEntity {

    @Column(name = "idPaquete")
    private String idPaquete;

    @Column(name = "estado")
    private int estado;// ver el cambio de esto a enum

    @Column(name = "ubicacion")
    private String ubicacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rutapredefinida", referencedColumnName = "id")
    private RutaPredefinidaEntity rutaPredefinida;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_envio", referencedColumnName = "id")
    private EnvioEntity envio;

}
