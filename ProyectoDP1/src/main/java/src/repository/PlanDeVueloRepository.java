package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import src.dto.PlanDeVueloDTO;

import java.util.List;

@Repository
@Transactional
public interface PlanDeVueloRepository extends JpaRepository<PlanDeVueloDTO, Long>{
    public List<PlanDeVueloDTO> findAll();
    public PlanDeVueloDTO findProyectoById(int id);
}
