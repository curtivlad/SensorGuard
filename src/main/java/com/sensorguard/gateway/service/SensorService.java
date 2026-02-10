package com.sensorguard.gateway.service;

import com.sensorguard.gateway.dto.SensorDTO;
import com.sensorguard.gateway.model.Sensor;
import com.sensorguard.gateway.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {

    private final SensorRepository sensorRepository;

    @Transactional
    public SensorDTO createSensor(SensorDTO sensorDTO) {
        if (sensorRepository.existsBySensorId(sensorDTO.getSensorId())) {
            throw new RuntimeException("Sensor with ID " + sensorDTO.getSensorId() + " already exists");
        }

        Sensor sensor = new Sensor();
        sensor.setSensorId(sensorDTO.getSensorId());
        sensor.setName(sensorDTO.getName());
        sensor.setType(sensorDTO.getType());
        sensor.setLocation(sensorDTO.getLocation());
        sensor.setUnit(sensorDTO.getUnit());
        sensor.setActive(sensorDTO.getActive() != null ? sensorDTO.getActive() : true);
        sensor.setCreatedAt(LocalDateTime.now());

        Sensor saved = sensorRepository.save(sensor);
        log.info("Created sensor: {}", saved.getSensorId());

        return toDTO(saved);
    }

    public List<SensorDTO> getAllSensors() {
        return sensorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SensorDTO> getActiveSensors() {
        return sensorRepository.findByActive(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SensorDTO getSensorBySensorId(String sensorId) {
        Sensor sensor = sensorRepository.findBySensorId(sensorId)
                .orElseThrow(() -> new RuntimeException("Sensor not found: " + sensorId));
        return toDTO(sensor);
    }

    public Sensor findBySensorId(String sensorId) {
        return sensorRepository.findBySensorId(sensorId)
                .orElseThrow(() -> new RuntimeException("Sensor not found: " + sensorId));
    }

    @Transactional
    public SensorDTO updateSensor(String sensorId, SensorDTO sensorDTO) {
        Sensor sensor = findBySensorId(sensorId);

        sensor.setName(sensorDTO.getName());
        sensor.setType(sensorDTO.getType());
        sensor.setLocation(sensorDTO.getLocation());
        sensor.setUnit(sensorDTO.getUnit());
        sensor.setActive(sensorDTO.getActive());

        Sensor updated = sensorRepository.save(sensor);
        log.info("Updated sensor: {}", updated.getSensorId());

        return toDTO(updated);
    }

    @Transactional
    public void deleteSensor(String sensorId) {
        Sensor sensor = findBySensorId(sensorId);
        sensorRepository.delete(sensor);
        log.info("Deleted sensor: {}", sensorId);
    }

    private SensorDTO toDTO(Sensor sensor) {
        return new SensorDTO(
                sensor.getId(),
                sensor.getSensorId(),
                sensor.getName(),
                sensor.getType(),
                sensor.getLocation(),
                sensor.getUnit(),
                sensor.getActive()
        );
    }
}