package com.sensorguard.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReadingDTO {

    private Long id;

    @NotBlank(message = "Sensor ID is required")
    private String sensorId;

    @NotNull(message = "Value is required")
    private Double value;

    private LocalDateTime timestamp;

    private Boolean isAnomaly;

    private Double anomalyScore;

    private String sensorName;

    private String sensorType;
}