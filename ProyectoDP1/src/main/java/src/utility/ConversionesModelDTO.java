package src.utility;

import src.dto.AeropuertoDTO;
import src.model.Aeropuerto;

public class ConversionesModelDTO {
    public static AeropuertoDTO convetirAeropuetoToDTO(Aeropuerto aeropuerto){
        AeropuertoDTO aeropuertoDTO = new AeropuertoDTO();
        aeropuertoDTO.setCodigoIATA(aeropuerto.getCodigoIATA());
        aeropuertoDTO.setCiudad(aeropuerto.getCiudad());
        aeropuertoDTO.setPais(aeropuerto.getPais());
        aeropuertoDTO.setContinente(aeropuerto.getContinente());
        aeropuertoDTO.setAlias(aeropuerto.getAlias());
        aeropuertoDTO.setZonaHorariaGTM(aeropuerto.getZonaHorariaGMT());
        //aeropuertoDTO.setLongitud(aeropuerto.getLongitud());
        //aeropuertoDTO.setLatitud(aeropuerto.getLatitud());
        aeropuertoDTO.setCapacidad(aeropuerto.getAlmacen().getCapacidad());
        aeropuertoDTO.setCantPaquetes(aeropuerto.getAlmacen().getCantPaquetes());
        return aeropuertoDTO;
    }
}
