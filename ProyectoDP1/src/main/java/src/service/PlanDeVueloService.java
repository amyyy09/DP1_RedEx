package src.service;

import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import src.entity.AeropuertoEntity;
import src.entity.PlanDeVueloEntity;
import src.model.Aeropuerto;
import src.model.PlanDeVuelo;
import src.repository.AeropuertoRepository;
import src.repository.PlanDeVueloRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlanDeVueloService {
    private static final Logger logger = LoggerFactory.getLogger(PlanDeVueloService.class);

    @Autowired
    private PlanDeVueloRepository planDeVueloRepository;
    @Autowired
    private AeropuertoRepository aeropuertoRepository; // Aquí agregamos la anotación @Autowired
    @PersistenceContext
    EntityManager entityManager;

    public PlanDeVueloEntity register(PlanDeVueloEntity planDeVuelo) {
        try {
            return planDeVueloRepository.save(planDeVuelo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<PlanDeVueloEntity> getAll() {
        try {
            return planDeVueloRepository.findAll();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public PlanDeVueloEntity convertToEntity(PlanDeVuelo planDeVuelo) {
        if (planDeVuelo == null)
            return null;

        PlanDeVueloEntity entity = new PlanDeVueloEntity();
        entity.setCodigoIATAOrigen(planDeVuelo.getCodigoIATAOrigen());
        entity.setCodigoIATADestino(planDeVuelo.getCodigoIATADestino());
        entity.setHoraSalida(planDeVuelo.getHoraSalida());
        entity.setHoraLlegada(planDeVuelo.getHoraLlegada());
        entity.setCapacidad(planDeVuelo.getCapacidad());
        return entity;
    }

    @Transactional
    public void saveBatchPlanesVuelo(List<PlanDeVueloEntity> aeropuertosEntities) {
        try {
            planDeVueloRepository.saveAll(aeropuertosEntities);
        } catch (Exception e) {
            logger.error("Error saving batch of AeropuertoEntities: " + e.getMessage(), e);
        }
    }

    public List<PlanDeVueloEntity> readValue(String jsonData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonData);
        String flightDataString = rootNode.get("data").asText();

        // Divide la cadena de datos en líneas individuales
        String[] flightLines = flightDataString.split("\n");

        List<PlanDeVueloEntity> planes = new ArrayList<>();

        List<AeropuertoEntity> aeropuertosEntities = aeropuertoRepository.findAll();
        List<Aeropuerto> aeropuertos = aeropuertosEntities.stream().map(Aeropuerto::convertirAeropuetoFromEntity)
                .collect(Collectors.toList());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        ZoneOffset noOffset = ZoneOffset.ofHours(0); // offset de referencia

        // Procesa cada línea y crea un objeto PlanDeVueloEntity
        for (String line : flightLines) {
            String[] parts = line.split("-");
            PlanDeVueloEntity plan = new PlanDeVueloEntity();

            plan.setCodigoIATAOrigen(parts[0]);
            plan.setCodigoIATADestino(parts[1]);

            int zonaHorariaSalida = getOffsetForAirport(parts[0], aeropuertos);
            int zonaHorariaLlegada = getOffsetForAirport(parts[1], aeropuertos);

            plan.setHoraSalida(OffsetTime.of(LocalTime.parse(parts[2], timeFormatter), noOffset));
            plan.setHoraLlegada(OffsetTime.of(LocalTime.parse(parts[3], timeFormatter), noOffset));

            plan.setZonaHorariaSalida(zonaHorariaSalida);
            plan.setZonaHorariaLlegada(zonaHorariaLlegada);

            plan.setCapacidad(Integer.parseInt(parts[4]));

            planes.add(plan);
        }

        return planes;
    }

    private int getOffsetForAirport(String codigoIATA, List<Aeropuerto> aeropuertos) {
        return aeropuertos.stream()
                .filter(a -> a.getCodigoIATA().equals(codigoIATA))
                .findFirst()
                .map(a -> a.getZonaHorariaGMT()) // Assuming getZonaHorariaGMT() returns int; adjust if needed
                .orElse(0); // Default to UTC if not found
    }
}