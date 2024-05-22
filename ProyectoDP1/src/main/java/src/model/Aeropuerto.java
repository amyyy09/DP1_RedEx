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
}
