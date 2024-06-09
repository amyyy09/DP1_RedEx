package src.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import src.dto.AeropuertoDTO;

import src.model.Aeropuerto;
import src.repository.AeropuertoRepository;

import src.utility.DatosAeropuertos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class AeropuertoService {
    private static final Logger logger = LoggerFactory.getLogger(PlanDeVueloService.class);

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @PersistenceContext
    EntityManager entityManager;

    private List<Aeropuerto> aeropuertos;

    @Autowired
    public AeropuertoService() {
        this.aeropuertos = DatosAeropuertos.getAeropuertosInicializados(); // Inicializa la lista de aeropuertos
    }

    public int getZonaHorariaGMT(String codigoIATA) {
        return aeropuertos.stream()
                          .filter(aeropuerto -> aeropuerto.getCodigoIATA().equals(codigoIATA))
                          .map(Aeropuerto::getZonaHorariaGMT)
                          .findFirst()
                          .orElse(0);
    }

    public AeropuertoDTO register (AeropuertoDTO aeropuertoDTO) {
        try {
            return aeropuertoRepository.save(aeropuertoDTO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<AeropuertoDTO> getAll() {
        try {
            return aeropuertoRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public AeropuertoDTO getById(Long id) {
        try {
            return aeropuertoRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public AeropuertoDTO update(AeropuertoDTO aeropuertoDTO) {
        try {
            return aeropuertoRepository.save(aeropuertoDTO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public boolean delete(Long id) {
        try {
            aeropuertoRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
    
    public static List<Aeropuerto> actualizarAeropuertos(List<Aeropuerto> modAeroList) {
        // Convertir la lista inicial de aeropuertos a un Map
        Map<String, Aeropuerto> aeropuertoMap = DatosAeropuertos.getAeropuertosInicializados().stream()
            .collect(Collectors.toMap(Aeropuerto::getCodigoIATA, aero -> aero));
    
        if (modAeroList == null) {
            // Si modAeroList es nulo, devolver la lista original de aeropuertos sin cambios
            return new ArrayList<>(aeropuertoMap.values());
        }
    
        // Iterar sobre la lista modificada y actualizar el Map
        for (Aeropuerto modAero : modAeroList) {
            Aeropuerto aero = aeropuertoMap.get(modAero.getCodigoIATA());
            if (aero != null) {
                aero.getAlmacen().setCapacidad(modAero.getAlmacen().getCapacidad());
                aero.getAlmacen().setCantPaquetes(modAero.getAlmacen().getCantPaquetes());
            }
        }
    
        // Convertir el Map de vuelta a una lista
        return new ArrayList<>(aeropuertoMap.values());
    }
}
