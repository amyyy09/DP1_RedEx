package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.entity.AeropuertoEntity;

@Repository
public interface AeropuertoRepository extends JpaRepository<AeropuertoEntity, Long> {
}
