package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import src.dto.AeropuertoDTO;

@Repository
@Transactional
public interface AeropuertoRepository extends JpaRepository<AeropuertoDTO, Long>{
    
}
