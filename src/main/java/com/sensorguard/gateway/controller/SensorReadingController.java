package com.sensorguard.gateway.controller;

import com.sensorguard.gateway.dto.SensorReadingDTO;
import com.sensorguard.gateway.service.SensorReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sensor Readings", description = "Sensor data ingestion and retrieval endpoints")
public class SensorReadingController {

    private final SensorReadingService readingService;

    @PostMapping("/ingest")
    @Operation(summary = "Ingest a new sensor reading")
    public ResponseEntity<SensorReadingDTO> ingestReading(@Valid @RequestBody SensorReadingDTO readingDTO) {
        log.info("Ingesting reading for sensor: {}", readingDTO.getSensorId());
        SensorReadingDTO ingested = readingService.ingestReading(readingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingested);
    }

    @GetMapping
    @Operation(summary = "Get all sensor readings")
    public ResponseEntity<List<SensorReadingDTO>> getAllReadings() {
        List<SensorReadingDTO> readings = readingService.getAllReadings();
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/sensor/{sensorId}")
    @Operation(summary = "Get readings by sensor ID")
    public ResponseEntity<List<SensorReadingDTO>> getReadingsBySensor(@PathVariable String sensorId) {
        List<SensorReadingDTO> readings = readingService.getReadingsBySensor(sensorId);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent readings (last N hours)")
    public ResponseEntity<List<SensorReadingDTO>> getRecentReadings(
            @RequestParam(defaultValue = "24") int hours) {
        List<SensorReadingDTO> readings = readingService.getRecentReadings(hours);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/anomalies")
    @Operation(summary = "Get all anomalous readings")
    public ResponseEntity<List<SensorReadingDTO>> getAnomalousReadings() {
        List<SensorReadingDTO> readings = readingService.getAnomalousReadings();
        return ResponseEntity.ok(readings);
    }
}