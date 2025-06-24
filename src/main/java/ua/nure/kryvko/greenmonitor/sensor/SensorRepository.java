package ua.nure.kryvko.greenmonitor.sensor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    List<Sensor> findByGreenhouseId(Integer greenhouseId);
}
