package com.sensorguard.gateway.service;

import com.sensorguard.gateway.dto.AlertDTO;
import com.sensorguard.gateway.dto.SensorReadingDTO;
import com.sensorguard.gateway.dto.WebSocketMessage;
import com.sensorguard.gateway.model.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendReading(SensorReadingDTO reading) {
        WebSocketMessage message = new WebSocketMessage("READING", reading);
        messagingTemplate.convertAndSend("/topic/readings", message);
        log.debug("Sent reading via WebSocket: {}", reading.getSensorId());
    }

    public void sendAlert(Alert alert) {
        AlertDTO alertDTO = new AlertDTO(
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

        WebSocketMessage message = new WebSocketMessage("ALERT", alertDTO);
        messagingTemplate.convertAndSend("/topic/alerts", message);
        log.info("Sent alert via WebSocket: {} - {}", alert.getSeverity(), alert.getSensor().getSensorId());
    }

    public void sendSensorStatus(String sensorId, String status) {
        WebSocketMessage message = new WebSocketMessage(
                "SENSOR_STATUS",
                new SensorStatus(sensorId, status)
        );
        messagingTemplate.convertAndSend("/topic/sensor-status", message);
        log.debug("Sent sensor status via WebSocket: {} - {}", sensorId, status);
    }

    // Inner class for sensor status
    public record SensorStatus(String sensorId, String status) {}
}