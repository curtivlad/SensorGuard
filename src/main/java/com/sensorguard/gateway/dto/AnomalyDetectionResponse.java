package com.sensorguard.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyDetectionResponse {

    private Boolean isAnomaly;

    private Double anomalyScore;

    private String message;
}