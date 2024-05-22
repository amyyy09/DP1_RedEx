package src.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import src.entity.AeropuertoEntity;
import src.entity.PlanDeVueloEntity;
import src.repository.AeropuertoRepository;
import src.repository.PlanDeVueloRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AeropuertoService {
    private static final Logger logger = LoggerFactory.getLogger(PlanDeVueloService.class);

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @PersistenceContext
    EntityManager entityManager;

    public AeropuertoEntity register(AeropuertoEntity aeropuertoDTO) {
        try {
            return aeropuertoRepository.save(aeropuertoDTO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<AeropuertoEntity> getAll() {
        try {
            return aeropuertoRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public AeropuertoEntity getById(Long id) {
        try {
            return aeropuertoRepository.findById(id).get();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public AeropuertoEntity update(AeropuertoEntity aeropuertoDTO) {
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

    @Transactional
    public void saveBatchAeropuertos(List<AeropuertoEntity> aeropuertosEntities) {
        try {
            aeropuertoRepository.saveAll(aeropuertosEntities);
        } catch (Exception e) {
            logger.error("Error saving batch of AeropuertoEntities: " + e.getMessage(), e);
            // You might want to handle or rethrow the exception depending on your
            // requirements
        }
    }

}