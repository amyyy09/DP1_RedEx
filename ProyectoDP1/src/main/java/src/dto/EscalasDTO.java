package src.dto;

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
@Table(name = "escalas")
@SQLDelete(sql = "UPDATE escalas SET activo = 0 WHERE id = ?")
@Where(clause = "activo = 1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EscalasDTO extends BaseEntity{
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rutaPredefinida", referencedColumnName = "id")
    private RutaPredefinidaDTO rutaPredefinida;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_PlanDeVuelo", referencedColumnName = "id")
    private PlanDeVueloDTO planDeVuelo;
}
