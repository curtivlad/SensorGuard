package com.sensorguard.gateway.repository;

import com.sensorguard.gateway.model.Alert;
import com.sensorguard.gateway.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findBySensorOrderByCreatedAtDesc(Sensor sensor);

    List<Alert> findByAcknowledgedFalseOrderByCreatedAtDesc();

    List<Alert> findBySeverityOrderByCreatedAtDesc(String severity);

    List<Alert> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);

    @Query("SELECT a FROM Alert a WHERE a.acknowledged = false AND a.severity = ?1 ORDER BY a.createdAt DESC")
    List<Alert> findUnacknowledgedBySeverity(String severity);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.acknowledged = false")
    long countUnacknowledged();
}