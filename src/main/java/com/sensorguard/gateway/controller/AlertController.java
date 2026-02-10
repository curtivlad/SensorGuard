package com.sensorguard.gateway.controller;

import com.sensorguard.gateway.dto.AlertDTO;
import com.sensorguard.gateway.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Alerts", description = "Alert management endpoints")
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    @Operation(summary = "Get all alerts")
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        List<AlertDTO> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/unacknowledged")
    @Operation(summary = "Get unacknowledged alerts")
    public ResponseEntity<List<AlertDTO>> getUnacknowledgedAlerts() {
        List<AlertDTO> alerts = alertService.getUnacknowledgedAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent alerts (last N hours)")
    public ResponseEntity<List<AlertDTO>> getRecentAlerts(
            @RequestParam(defaultValue = "24") int hours) {
        List<AlertDTO> alerts = alertService.getRecentAlerts(hours);
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{alertId}/acknowledge")
    @Operation(summary = "Acknowledge an alert")
    public ResponseEntity<AlertDTO> acknowledgeAlert(@PathVariable Long alertId) {
        log.info("Acknowledging alert: {}", alertId);
        AlertDTO alert = alertService.acknowledgeAlert(alertId);
        return ResponseEntity.ok(alert);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get alert statistics")
    public ResponseEntity<Map<String, Object>> getAlertStats() {
        long unacknowledgedCount = alertService.getUnacknowledgedCount();
        return ResponseEntity.ok(Map.of(
                "unacknowledgedCount", unacknowledgedCount,
                "totalCount", alertService.getAllAlerts().size()
        ));
    }
}