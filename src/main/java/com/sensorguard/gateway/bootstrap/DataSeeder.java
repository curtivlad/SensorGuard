package com.sensorguard.gateway.bootstrap;

import com.sensorguard.gateway.model.Sensor;
import com.sensorguard.gateway.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final SensorRepository sensorRepository;

    @Override
    public void run(String... args) throws Exception {
        if (sensorRepository.count() == 0) {
            log.info("Seeding database with initial sensors...");

            // Temperature Sensor 1
            Sensor tempSensor1 = new Sensor();
            tempSensor1.setSensorId("TEMP-001");
            tempSensor1.setName("Server Room Temperature");
            tempSensor1.setType("TEMPERATURE");
            tempSensor1.setLocation("Data Center - Floor 1");
            tempSensor1.setUnit("°C");
            tempSensor1.setActive(true);
            tempSensor1.setCreatedAt(LocalDateTime.now());
            sensorRepository.save(tempSensor1);

            // Vibration Sensor
            Sensor vibrationSensor = new Sensor();
            vibrationSensor.setSensorId("VIB-001");
            vibrationSensor.setName("Motor Vibration Monitor");
            vibrationSensor.setType("VIBRATION");
            vibrationSensor.setLocation("Production Line A");
            vibrationSensor.setUnit("Hz");
            vibrationSensor.setActive(true);
            vibrationSensor.setCreatedAt(LocalDateTime.now());
            sensorRepository.save(vibrationSensor);

            // Pressure Sensor
            Sensor pressureSensor = new Sensor();
            pressureSensor.setSensorId("PRES-001");
            pressureSensor.setName("Hydraulic Pressure Sensor");
            pressureSensor.setType("PRESSURE");
            pressureSensor.setLocation("Main Pump Room");
            pressureSensor.setUnit("bar");
            pressureSensor.setActive(true);
            pressureSensor.setCreatedAt(LocalDateTime.now());
            sensorRepository.save(pressureSensor);

            // Humidity Sensor
            Sensor humiditySensor = new Sensor();
            humiditySensor.setSensorId("HUM-001");
            humiditySensor.setName("Warehouse Humidity");
            humiditySensor.setType("HUMIDITY");
            humiditySensor.setLocation("Warehouse Section B");
            humiditySensor.setUnit("%");
            humiditySensor.setActive(true);
            humiditySensor.setCreatedAt(LocalDateTime.now());
            sensorRepository.save(humiditySensor);

            log.info("✅ Database seeded with {} sensors", sensorRepository.count());
        } else {
            log.info("Database already contains {} sensors, skipping seed", sensorRepository.count());
        }
    }
}