package com.sensorguard.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyDetectionRequest {

    private String sensorId;

    private Double value;

    private String sensorType;
}