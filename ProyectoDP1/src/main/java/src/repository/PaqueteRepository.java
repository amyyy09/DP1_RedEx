package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import src.dto.PaqueteDTO;

@Repository
@Transactional
public interface PaqueteRepository extends JpaRepository<PaqueteDTO, Long> {
    
}
