package src.service;

import java.util.List;

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
import src.dto.PlanDeVueloDTO;
import src.repository.PlanDeVueloRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class PlanDeVueloService {
    private static final Logger logger = LoggerFactory.getLogger(PlanDeVueloService.class);

    @Autowired
    private PlanDeVueloRepository planDeVueloRepository;

    @PersistenceContext
    EntityManager entityManager;

    public PlanDeVueloDTO register (PlanDeVueloDTO planDeVuelo) {
        try {
            return planDeVueloRepository.save(planDeVuelo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<PlanDeVueloDTO> getAll() {
        try {
            return planDeVueloRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}