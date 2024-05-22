package src.service;

import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.lettuce.core.dynamic.annotation.Param;
import src.entity.AeropuertoEntity;
import src.entity.PlanDeVueloEntity;
import src.model.PlanDeVuelo;
import src.repository.PlanDeVueloRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlanDeVueloService {
    private static final Logger logger = LoggerFactory.getLogger(PlanDeVueloService.class);

    @Autowired
    private PlanDeVueloRepository planDeVueloRepository;

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

        List<PlanDeVueloEntity> flights = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        ZoneOffset peruOffset = ZoneOffset.ofHours(-5);

        // Procesa cada línea y crea un objeto PlanDeVueloEntity
        for (String line : flightLines) {
            String[] parts = line.split("-");
            PlanDeVueloEntity flight = new PlanDeVueloEntity();

            flight.setCodigoIATAOrigen(parts[0]);
            flight.setCodigoIATADestino(parts[1]);
            flight.setHoraSalida(OffsetTime.of(LocalTime.parse(parts[2], timeFormatter), peruOffset));
            flight.setHoraLlegada(OffsetTime.of(LocalTime.parse(parts[3], timeFormatter), peruOffset));
            flight.setCapacidad(Integer.parseInt(parts[4]));

            flights.add(flight);
        }

        return flights;
    }
}