package com.sensorguard.gateway.controller;

import com.sensorguard.gateway.dto.SensorDTO;
import com.sensorguard.gateway.service.SensorService;
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
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sensors", description = "Sensor management endpoints")
public class SensorController {

    private final SensorService sensorService;

    @PostMapping
    @Operation(summary = "Create a new sensor")
    public ResponseEntity<SensorDTO> createSensor(@Valid @RequestBody SensorDTO sensorDTO) {
        log.info("Creating new sensor: {}", sensorDTO.getSensorId());
        SensorDTO created = sensorService.createSensor(sensorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Get all sensors")
    public ResponseEntity<List<SensorDTO>> getAllSensors() {
        List<SensorDTO> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active sensors")
    public ResponseEntity<List<SensorDTO>> getActiveSensors() {
        List<SensorDTO> sensors = sensorService.getActiveSensors();
        return ResponseEntity.ok(sensors);
    }

    @GetMapping("/{sensorId}")
    @Operation(summary = "Get sensor by ID")
    public ResponseEntity<SensorDTO> getSensor(@PathVariable String sensorId) {
        SensorDTO sensor = sensorService.getSensorBySensorId(sensorId);
        return ResponseEntity.ok(sensor);
    }

    @PutMapping("/{sensorId}")
    @Operation(summary = "Update sensor")
    public ResponseEntity<SensorDTO> updateSensor(
            @PathVariable String sensorId,
            @Valid @RequestBody SensorDTO sensorDTO) {
        log.info("Updating sensor: {}", sensorId);
        SensorDTO updated = sensorService.updateSensor(sensorId, sensorDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{sensorId}")
    @Operation(summary = "Delete sensor")
    public ResponseEntity<Void> deleteSensor(@PathVariable String sensorId) {
        log.info("Deleting sensor: {}", sensorId);
        sensorService.deleteSensor(sensorId);
        return ResponseEntity.noContent().build();
    }
}