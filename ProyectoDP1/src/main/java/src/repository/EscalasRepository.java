package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import src.dto.EscalasDTO;

@Repository
@Transactional

public interface EscalasRepository extends JpaRepository<EscalasDTO, Long>{
    
}
