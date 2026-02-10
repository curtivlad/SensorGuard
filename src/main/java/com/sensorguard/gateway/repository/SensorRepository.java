package com.sensorguard.gateway.repository;

import com.sensorguard.gateway.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findBySensorId(String sensorId);

    List<Sensor> findByActive(Boolean active);

    List<Sensor> findByType(String type);

    boolean existsBySensorId(String sensorId);
}