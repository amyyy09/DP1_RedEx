package src.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import src.dto.EnvioDTO;

@Repository
@Transactional
public interface EnvioRepository extends JpaRepository<EnvioDTO, Long>{
    
}
