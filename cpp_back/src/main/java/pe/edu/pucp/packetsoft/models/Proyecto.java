package pe.edu.pucp.packetsoft.models;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

//import com.amazonaws.services.kendra.model.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto")
@SQLDelete(sql = "UPDATE proyecto SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Proyecto extends BaseEntity{
 
   

    @Column(name="codigo")
    private String codigo;

    @Column(name="nombre")
    private String nombre;

    @Column(name="fechaInicio")
    private Date fechaInicio;

    @Column(name="fechaFin")
    private Date fechaFin;

    @Column(name ="usuarioCreacion")
    private String usuarioCreacion;

    @Column(name="usuarioActualizacion")
    private String usuarioActualizacion;


}
