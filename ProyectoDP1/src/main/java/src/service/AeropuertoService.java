package src.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import src.dto.AeropuertoDTO;
import src.dto.PlanDeVueloDTO;
import src.model.Aeropuerto;
import src.repository.AeropuertoRepository;
import src.repository.PlanDeVueloRepository;
import src.utility.ConversionesModelDTO;
import src.utility.DatosAeropuertos;

import java.util.concurrent.CopyOnWriteArrayList;

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
    
    @PostConstruct
    public void init() {
        aeropuertos = new CopyOnWriteArrayList<>();
        try {
            aeropuertos = DatosAeropuertos.getAeropuertosInicializados();
            System.out.println("Se cargaron los aeropuertos");
        } catch (Exception e) {
            // Maneja la excepción de acuerdo a tus necesidades
            System.err.println("Error al cargar los aeropuertos: " + e.getMessage());
        }
        
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
            return aeropuertoRepository.findById(id).get();
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

    // Model a DTO
    public void saveAeropuerto(Aeropuerto aeropuerto){
        AeropuertoDTO aeropuertoDTO = ConversionesModelDTO.convetirAeropuetoToDTO(aeropuerto);
        aeropuertoRepository.save(aeropuertoDTO);
    }

    public void saveAllAeropuertos(List<Aeropuerto> aeropuertos){
        List<AeropuertoDTO> aeropuertoDTOs= aeropuertos.stream().map(ConversionesModelDTO :: convetirAeropuetoToDTO).collect(Collectors.toList());
        aeropuertoRepository.saveAll(aeropuertoDTOs);
    }
    
}
