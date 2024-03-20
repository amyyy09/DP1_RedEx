package pe.edu.pucp.packetsoft.models;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME", nullable = false)
    protected Date fecha_creacion = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME", nullable = false)
    protected Date fecha_modificacion = new Date();

    @Column(name = "activo")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int activo = 1;

    @PreUpdate
    private void onUpdate(){
        fecha_modificacion = addHoursToJavaUtilDate(new Date(), -5);
    }

    @PrePersist
    private void onCreate(){
        fecha_creacion=fecha_modificacion=addHoursToJavaUtilDate(new Date(), -5);
    }

    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

}
