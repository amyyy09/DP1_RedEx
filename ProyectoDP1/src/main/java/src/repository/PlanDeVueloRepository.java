package src.repository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import src.entity.PlanDeVueloEntity;
import src.entity.RutaPredefinidaEntity;

import java.util.List;

@Repository
@Transactional
public interface PlanDeVueloRepository extends JpaRepository<PlanDeVueloEntity, Long> {
    public List<PlanDeVueloEntity> findAll();

    public PlanDeVueloEntity findProyectoById(int id);
}