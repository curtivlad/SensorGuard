package com.sensorguard.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    private String type; // READING, ALERT, SENSOR_STATUS

    private Object payload;

    private LocalDateTime timestamp;

    public WebSocketMessage(String type, Object payload) {
        this.type = type;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }
}