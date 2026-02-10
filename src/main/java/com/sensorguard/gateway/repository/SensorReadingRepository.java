package com.sensorguard.gateway.repository;

import com.sensorguard.gateway.model.Sensor;
import com.sensorguard.gateway.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    List<SensorReading> findBySensorOrderByTimestampDesc(Sensor sensor);

    List<SensorReading> findBySensorAndTimestampBetween(
            Sensor sensor,
            LocalDateTime start,
            LocalDateTime end
    );

    List<SensorReading> findByIsAnomalyTrue();

    @Query("SELECT r FROM SensorReading r WHERE r.sensor.id = ?1 ORDER BY r.timestamp DESC")
    List<SensorReading> findLatestBySensorId(Long sensorId);

    @Query("SELECT r FROM SensorReading r WHERE r.timestamp >= ?1 ORDER BY r.timestamp DESC")
    List<SensorReading> findRecentReadings(LocalDateTime since);
}