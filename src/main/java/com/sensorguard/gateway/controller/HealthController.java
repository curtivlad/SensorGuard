package com.sensorguard.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check and monitoring endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Basic health check")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "sensorguard-gateway");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0.0");

        return ResponseEntity.ok(health);
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe")
    public ResponseEntity<Map<String, String>> ready() {
        return ResponseEntity.ok(Map.of(
                "status", "READY",
                "message", "Application is ready to serve requests"
        ));
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness probe")
    public ResponseEntity<Map<String, String>> live() {
        return ResponseEntity.ok(Map.of(
                "status", "ALIVE",
                "message", "Application is alive"
        ));
    }
}