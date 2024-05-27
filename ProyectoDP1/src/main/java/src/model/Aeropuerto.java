package src.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import src.entity.AeropuertoEntity;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Aeropuerto {
    private String codigoIATA;
    private String ciudad;
    private String pais;
    private String continente;
    private String alias;
    private int zonaHorariaGMT;
    private Almacen almacen;
    private double longitud;
    private double latitud;
    private long capacidad;
    private int cantPaquetes;

    // ejemplo de conversion implicita DTO -> entidad
    public static AeropuertoEntity convetirAeropuetoToDTO(Aeropuerto aeropuerto) {
        AeropuertoEntity aeropuertoDTO = new AeropuertoEntity();
        aeropuertoDTO.setCodigoIATA(aeropuerto.getCodigoIATA());
        aeropuertoDTO.setCiudad(aeropuerto.getCiudad());
        aeropuertoDTO.setPais(aeropuerto.getPais());
        aeropuertoDTO.setContinente(aeropuerto.getContinente());
        aeropuertoDTO.setAlias(aeropuerto.getAlias());
        aeropuertoDTO.setZonaHorariaGTM(aeropuerto.getZonaHorariaGMT());
        aeropuertoDTO.setLongitud(aeropuerto.getLongitud());
        aeropuertoDTO.setLatitud(aeropuerto.getLatitud());
        aeropuertoDTO.setCapacidad(aeropuerto.getAlmacen().getCapacidad());
        aeropuertoDTO.setCantPaquetes(aeropuerto.getAlmacen().getCantPaquetes());
        return aeropuertoDTO;
    }

    public static Aeropuerto convertirAeropuetoFromEntity(AeropuertoEntity entity) {
        Aeropuerto aeropuerto = new Aeropuerto();
        aeropuerto.setCodigoIATA(entity.getCodigoIATA());
        aeropuerto.setCiudad(entity.getCiudad());
        aeropuerto.setPais(entity.getPais());
        aeropuerto.setContinente(entity.getContinente());
        aeropuerto.setAlias(entity.getAlias());
        aeropuerto.setZonaHorariaGMT(entity.getZonaHorariaGTM());
        aeropuerto.setLongitud(entity.getLongitud());
        aeropuerto.setLatitud(entity.getLatitud());
        Almacen almacen = new Almacen();
        almacen.setCantPaquetes(0);
        almacen.setCapacidad(entity.getCapacidad());
        almacen.setPaquetes(null);
        aeropuerto.setAlmacen(almacen);
        return aeropuerto;
    }

}
