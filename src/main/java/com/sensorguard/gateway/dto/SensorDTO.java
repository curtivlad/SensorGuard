package com.sensorguard.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDTO {

    private Long id;

    @NotBlank(message = "Sensor ID is required")
    private String sensorId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Type is required")
    private String type; // TEMPERATURE, VIBRATION, PRESSURE, HUMIDITY

    private String location;

    private String unit;

    @NotNull(message = "Active status is required")
    private Boolean active;
}