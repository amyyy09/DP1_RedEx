package src.utility;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import src.dto.AeropuertoDTO;
import src.dto.EnvioDTO;
import src.dto.PaqueteDTO;
import src.model.Aeropuerto;
import src.model.Envio;
import src.model.Paquete;
import src.repository.EnvioRepository;
import src.repository.PaqueteRepository;


@Component
public class ConversionesModelDTO {

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private EnvioRepository envioRepository;
    
    public static AeropuertoDTO convetirAeropuetoToDTO(Aeropuerto aeropuerto){
        AeropuertoDTO aeropuertoDTO = new AeropuertoDTO();
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

    // public List<EnvioDTO> convertirEnviosToDTO(List<Envio> envios){
    //     List<EnvioDTO> enviosDTO = new ArrayList<>();
    //     for (Envio envio : envios) {
    //         EnvioDTO envioDTO = new EnvioDTO();
    //         envioDTO.setIdEnvio(envio.getIdEnvio());
    //         envioDTO.setFechaHoraOrigen(envio.getFechaHoraOrigen());
    //         envioDTO.setZonaHorariaGMT(envio.getZonaHorariaGMT());
    //         envioDTO.setCodigoIATAOrigen(envio.getCodigoIATAOrigen());
    //         envioDTO.setCodigoIATADestino(envio.getCodigoIATADestino());
    //         envioDTO.setCantPaquetes(envio.getCantPaquetes());
    //         envioRepository.save(envioDTO);

    //         for (Paquete paquete : envio.getPaquetes()) {
    //             PaqueteDTO paqueteDTO = new PaqueteDTO();
    //             paqueteDTO.setIdPaquete(paquete.getIdEnvio());
    //             paqueteDTO.setEstado(paquete.getStatus());
    //             paqueteDTO.setEnvio(envioDTO);
    //             paqueteRepository.save(paqueteDTO);
    //         }
    //         enviosDTO.add(envioDTO);
    //     }
    //     return enviosDTO;
    // }

    public List<EnvioDTO> convertirEnviosToDTO(List<Envio> envios) {
        List<EnvioDTO> enviosDTO = new ArrayList<>();
        for (Envio envio : envios) {
            EnvioDTO envioDTO = new EnvioDTO();
            envioDTO.setIdEnvio(envio.getIdEnvio());
            envioDTO.setFechaHoraOrigen(envio.getFechaHoraOrigen());
            envioDTO.setZonaHorariaGMT(envio.getZonaHorariaGMT());
            envioDTO.setCodigoIATAOrigen(envio.getCodigoIATAOrigen());
            envioDTO.setCodigoIATADestino(envio.getCodigoIATADestino());
            envioDTO.setCantPaquetes(envio.getCantPaquetes());
            List<PaqueteDTO> paquetesDTO = new ArrayList<>();

            for (Paquete paquete : envio.getPaquetes()) {
                PaqueteDTO paqueteDTO = new PaqueteDTO();
                paqueteDTO.setIdPaquete(paquete.getIdEnvio());
                paqueteDTO.setEstado(paquete.getStatus());
                paqueteDTO.setEnvio(envioDTO);
                paquetesDTO.add(paqueteDTO);
            }
            envioDTO.setPaquetes(paquetesDTO);
            enviosDTO.add(envioDTO);
        }
        return enviosDTO;
    }
}
