package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.entity.Aeropuerto;
import src.repository.AeropuertoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AeropuertoService {

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    public List<Aeropuerto> getAllAeropuertos() {
        return aeropuertoRepository.findAll();
    }

    public Optional<Aeropuerto> getAeropuertoById(Long id) {
        return aeropuertoRepository.findById(id);
    }

    public Aeropuerto saveAeropuerto(Aeropuerto aeropuerto) {
        return aeropuertoRepository.save(aeropuerto);
    }

    public Aeropuerto updateAeropuerto(Long id, Aeropuerto aeropuertoDetails) {
        Aeropuerto aeropuerto = aeropuertoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aeropuerto not found"));
        aeropuerto.setCodigoIATA(aeropuertoDetails.getCodigoIATA());
        aeropuerto.setCiudad(aeropuertoDetails.getCiudad());
        aeropuerto.setPais(aeropuertoDetails.getPais());
        aeropuerto.setContinente(aeropuertoDetails.getContinente());
        aeropuerto.setAlias(aeropuertoDetails.getAlias());
        aeropuerto.setZonaHorariaGMT(aeropuertoDetails.getZonaHorariaGMT());
        aeropuerto.setCantPaquetes(aeropuertoDetails.getCantPaquetes());
        aeropuerto.setCapacidad(aeropuertoDetails.getCapacidad());
        aeropuerto.setLatitud(aeropuertoDetails.getLatitud());
        aeropuerto.setLongitud(aeropuertoDetails.getLongitud());
        aeropuerto.setActivo(aeropuertoDetails.getActivo());
        aeropuerto.setFechaCreacion(aeropuertoDetails.getFechaCreacion());
        aeropuerto.setFechaModificacion(aeropuertoDetails.getFechaModificacion());
        return aeropuertoRepository.save(aeropuerto);
    }

    public void deleteAeropuerto(Long id) {
        Aeropuerto aeropuerto = aeropuertoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aeropuerto not found"));
        aeropuertoRepository.delete(aeropuerto);
    }
}
