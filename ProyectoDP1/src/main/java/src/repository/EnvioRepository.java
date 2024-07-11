package src.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import src.model.EnvioEntity;

public interface EnvioRepository extends JpaRepository<EnvioEntity, Long> {
}

