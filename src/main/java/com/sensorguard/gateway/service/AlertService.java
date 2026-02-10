package com.sensorguard.gateway.service;

import com.sensorguard.gateway.dto.AlertDTO;
import com.sensorguard.gateway.model.Alert;
import com.sensorguard.gateway.model.Sensor;
import com.sensorguard.gateway.model.SensorReading;
import com.sensorguard.gateway.repository.AlertRepository;
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
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public Alert createAlert(Sensor sensor, SensorReading reading, String severity, String message) {
        Alert alert = new Alert();
        alert.setSensor(sensor);
        alert.setReading(reading);
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setAcknowledged(false);

        Alert saved = alertRepository.save(alert);
        log.warn("Alert created: {} - Sensor: {} - Message: {}", severity, sensor.getSensorId(), message);

        return saved;
    }

    public List<AlertDTO> getAllAlerts() {
        return alertRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AlertDTO> getUnacknowledgedAlerts() {
        return alertRepository.findByAcknowledgedFalseOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AlertDTO> getRecentAlerts(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return alertRepository.findByCreatedAtAfterOrderByCreatedAtDesc(since).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlertDTO acknowledgeAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));

        alert.setAcknowledged(true);
        alert.setAcknowledgedAt(LocalDateTime.now());

        Alert updated = alertRepository.save(alert);
        log.info("Alert acknowledged: {}", alertId);

        return toDTO(updated);
    }

    public long getUnacknowledgedCount() {
        return alertRepository.countUnacknowledged();
    }

    private AlertDTO toDTO(Alert alert) {
        return new AlertDTO(
                alert.getId(),
                alert.getSensor().getSensorId(),
                alert.getSensor().getName(),
                alert.getReading() != null ? alert.getReading().getId() : null,
                alert.getSeverity(),
                alert.getMessage(),
                alert.getCreatedAt(),
                alert.getAcknowledged(),
                alert.getAcknowledgedAt()
        );
    }
}