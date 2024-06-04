package src.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import com.fasterxml.jackson.databind.ObjectMapper;

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


import com.fasterxml.jackson.core.type.TypeReference;


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
