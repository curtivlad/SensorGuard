package com.sensorguard.gateway.service;

import com.sensorguard.gateway.dto.AnomalyDetectionRequest;
import com.sensorguard.gateway.dto.AnomalyDetectionResponse;
import com.sensorguard.gateway.dto.SensorReadingDTO;
import com.sensorguard.gateway.model.Alert;
import com.sensorguard.gateway.model.Sensor;
import com.sensorguard.gateway.model.SensorReading;
import com.sensorguard.gateway.repository.SensorReadingRepository;
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
public class SensorReadingService {

    private final SensorReadingRepository readingRepository;
    private final SensorService sensorService;
    private final AnomalyDetectorService anomalyDetectorService;
    private final AlertService alertService;
    private final WebSocketService webSocketService;

    @Transactional
    public SensorReadingDTO ingestReading(SensorReadingDTO readingDTO) {
        // Find sensor
        Sensor sensor = sensorService.findBySensorId(readingDTO.getSensorId());

        // Create reading entity
        SensorReading reading = new SensorReading();
        reading.setSensor(sensor);
        reading.setValue(readingDTO.getValue());
        reading.setTimestamp(readingDTO.getTimestamp() != null ?
                readingDTO.getTimestamp() : LocalDateTime.now());

        // Check for anomaly using Python microservice
        AnomalyDetectionRequest anomalyRequest = new AnomalyDetectionRequest(
                sensor.getSensorId(),
                readingDTO.getValue(),
                sensor.getType()
        );

        AnomalyDetectionResponse anomalyResponse = anomalyDetectorService.detectAnomaly(anomalyRequest);

        reading.setIsAnomaly(anomalyResponse.getIsAnomaly());
        reading.setAnomalyScore(anomalyResponse.getAnomalyScore());

        // Save reading
        SensorReading saved = readingRepository.save(reading);
        log.info("Ingested reading for sensor: {} - Value: {} - Anomaly: {}",
                sensor.getSensorId(), saved.getValue(), saved.getIsAnomaly());

        // Create alert if anomaly detected
        if (saved.getIsAnomaly()) {
            String severity = determineSeverity(anomalyResponse.getAnomalyScore());
            String message = String.format(
                    "Anomaly detected for sensor %s (%s). Value: %.2f, Score: %.2f",
                    sensor.getName(),
                    sensor.getType(),
                    saved.getValue(),
                    saved.getAnomalyScore()
            );

            Alert alert = alertService.createAlert(sensor, saved, severity, message);

            // Send alert via WebSocket
            webSocketService.sendAlert(alert);
        }

        // Send reading via WebSocket
        SensorReadingDTO responseDTO = toDTO(saved);
        webSocketService.sendReading(responseDTO);

        return responseDTO;
    }

    public List<SensorReadingDTO> getAllReadings() {
        return readingRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SensorReadingDTO> getReadingsBySensor(String sensorId) {
        Sensor sensor = sensorService.findBySensorId(sensorId);
        return readingRepository.findBySensorOrderByTimestampDesc(sensor).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SensorReadingDTO> getRecentReadings(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return readingRepository.findRecentReadings(since).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SensorReadingDTO> getAnomalousReadings() {
        return readingRepository.findByIsAnomalyTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private String determineSeverity(Double anomalyScore) {
        if (anomalyScore >= 0.9) return "CRITICAL";
        if (anomalyScore >= 0.7) return "HIGH";
        if (anomalyScore >= 0.5) return "MEDIUM";
        return "LOW";
    }

    private SensorReadingDTO toDTO(SensorReading reading) {
        return new SensorReadingDTO(
                reading.getId(),
                reading.getSensor().getSensorId(),
                reading.getValue(),
                reading.getTimestamp(),
                reading.getIsAnomaly(),
                reading.getAnomalyScore(),
                reading.getSensor().getName(),
                reading.getSensor().getType()
        );
    }
}