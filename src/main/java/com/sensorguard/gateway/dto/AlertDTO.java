package com.sensorguard.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {

    private Long id;

    private String sensorId;

    private String sensorName;

    private Long readingId;

    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    private String message;

    private LocalDateTime createdAt;

    private Boolean acknowledged;

    private LocalDateTime acknowledgedAt;
}