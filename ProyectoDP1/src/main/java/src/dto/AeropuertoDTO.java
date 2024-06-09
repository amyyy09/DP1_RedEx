package src.dto;

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
@Table(name = "aeropuerto")
@SQLDelete(sql = "UPDATE aeropuerto SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class AeropuertoDTO extends BaseEntity {
    @Column(name = "codigoIATA")
    private String codigoIATA;
    
    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "pais")
    private String pais;

    @Column(name = "continente")
    private String continente;

    @Column(name = "alias")
    private String alias;

    @Column(name = "zonaHorariaGTM")
    private int zonaHorariaGTM;

    @Column(name = "longitud")
    private String longitud;

    @Column(name = "latitud")
    private String latitud; 

    @Column(name = "capacidad")
    private long capacidad;

    @Column(name = "cantPaquetes")
    private int cantPaquetes;
    
}
