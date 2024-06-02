package src.dto;

import java.time.LocalDateTime;

import java.time.OffsetDateTime;

import java.time.LocalDateTime;
import java.time.OffsetTime;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "vuelo")
@SQLDelete(sql = "UPDATE vuelo SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class VueloDTO extends BaseEntity{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="id_planDeVuelo", referencedColumnName = "id")
    private PlanDeVueloDTO planDeVuelo;

    @Column(name = "cantPaquetes")
    private int cantPaquetes;

    @Column(name = "capacidad")
    private int capacidad;

    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @Column(name = "activo2")
    private boolean activo2;
}
