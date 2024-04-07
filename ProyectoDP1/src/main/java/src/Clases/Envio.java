package src.Clases;
import java.util.List;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Envio {
    private String idEnvio;
    List <Paquete> paquetes;
   
    private Date fechaHoraOrigen;
    private Date fechaHoraLimite;
    private int cantPaquetes;

    private Almacen almacenOrigen;
    private Almacen almacenDestino;

}
