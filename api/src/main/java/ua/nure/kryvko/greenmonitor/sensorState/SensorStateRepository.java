package ua.nure.kryvko.greenmonitor.sensorState;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.kryvko.greenmonitor.sensor.Sensor;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorStateRepository extends JpaRepository<SensorState, Integer> {
    List<SensorState> findBySensor (Sensor sensor);
    List<SensorState> findBySensorIdAndTimestampBetween(Integer sensorId, LocalDateTime start, LocalDateTime end);
}
