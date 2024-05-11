package src.dto;

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
@Table(name = "planDeVuelo")
@SQLDelete(sql = "UPDATE planDeVuelo SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class PlanDeVueloDTO extends BaseEntity{

    @Column(name = "idPlanDeVuelo")
    private String idPlanDeVuelo;

    @Column(name = "idAeropuertoOrigen")
    private String idAeropuertoOrigen;

    @Column(name = "idAeropuertoDestino")
    private String idAeropuertoDestino;

    @Column(name = "fechaHoraSalida")
    private OffsetTime horaSalidadLocal; 

    @Column(name = "fechaHoraLlegada")
    private OffsetTime horaLlegadaLocal;

    @Column(name = "capacidad")
    private int capacidad;
    
    @Column(name = "transcontinental")
    private boolean transcontinental;   
}
