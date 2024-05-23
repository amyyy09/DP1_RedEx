package src.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.entity.PaqueteEntity;

@Repository
public interface PaqueteRepository extends JpaRepository<PaqueteEntity, Long> { 
    
}
