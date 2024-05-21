package src.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "aeropuerto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aeropuerto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_iata", nullable = false)
    private String codigoIATA;

    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    @Column(name = "pais", nullable = false)
    private String pais;

    @Column(name = "continente", nullable = false)
    private String continente;

    @Column(name = "alias")
    private String alias;

    @Column(name = "zona_horaria_gmt", nullable = false)
    private int zonaHorariaGMT;

    @Column(name = "cant_paquetes")
    private int cantPaquetes;

    @Column(name = "capacidad")
    private long capacidad;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "activo")
    private int activo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
