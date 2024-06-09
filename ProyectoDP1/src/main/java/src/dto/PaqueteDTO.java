package src.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


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

public class PaqueteDTO extends BaseEntity{

    @Column(name = "idPaquete")
    private String idPaquete;

    @Column(name = "estado")
    private int estado;//ver el cambio de esto a enum

    @Column(name = "ubicacion")
    private String ubicacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_rutapredefinida", referencedColumnName = "id")
    private RutaPredefinidaDTO rutaPredefinida;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_envio", referencedColumnName = "id")
    private EnvioDTO envio;

    
}
