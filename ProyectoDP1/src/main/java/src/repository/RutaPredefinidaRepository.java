package src.repository;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import src.entity.RutaPredefinidaEntity;

import java.util.List;

@Repository
@Transactional
public interface RutaPredefinidaRepository extends JpaRepository<RutaPredefinidaEntity, Long> {
}
