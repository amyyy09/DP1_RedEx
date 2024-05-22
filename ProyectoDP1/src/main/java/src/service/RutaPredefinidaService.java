package src.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import src.entity.RutaPredefinidaEntity;
import src.repository.RutaPredefinidaRepository;

@Service
public class RutaPredefinidaService {

    @Autowired
    private RutaPredefinidaRepository rutaPredefinidaRepository;

    public RutaPredefinidaEntity register(RutaPredefinidaEntity ruta) {
        return rutaPredefinidaRepository.save(ruta);
    }

    public List<RutaPredefinidaEntity> getAll() {
        return rutaPredefinidaRepository.findAll();
    }

    public RutaPredefinidaEntity getById(Long id) {
        return rutaPredefinidaRepository.findById(id).orElse(null);
    }

    public RutaPredefinidaEntity update(RutaPredefinidaEntity ruta) {
        return rutaPredefinidaRepository.save(ruta);
    }

    @Transactional
    public boolean delete(Long id) {
        try {
            rutaPredefinidaRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
