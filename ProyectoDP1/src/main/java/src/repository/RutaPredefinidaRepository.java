package src.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import src.dto.RutaPredefinidaDTO;

import java.util.List;


@Repository
@Transactional
public interface RutaPredefinidaRepository extends JpaRepository<RutaPredefinidaDTO, Long>{
    public List<RutaPredefinidaDTO> findAll();
    public RutaPredefinidaDTO findProyectoById(int id);
}
