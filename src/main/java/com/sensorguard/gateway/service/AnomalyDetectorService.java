package com.sensorguard.gateway.service;

import com.sensorguard.gateway.dto.AnomalyDetectionRequest;
import com.sensorguard.gateway.dto.AnomalyDetectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyDetectorService {

    private final RestTemplate restTemplate;

    @Value("${anomaly.detector.url}")
    private String anomalyDetectorUrl;

    public AnomalyDetectionResponse detectAnomaly(AnomalyDetectionRequest request) {
        try {
            log.debug("Sending anomaly detection request for sensor: {}", request.getSensorId());

            String url = anomalyDetectorUrl + "/detect";
            AnomalyDetectionResponse response = restTemplate.postForObject(
                    url,
                    request,
                    AnomalyDetectionResponse.class
            );

            log.debug("Anomaly detection response: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Error calling anomaly detector service", e);
            // Fallback: return non-anomaly if service is down
            return new AnomalyDetectionResponse(false, 0.0, "Anomaly detector service unavailable");
        }
    }
}