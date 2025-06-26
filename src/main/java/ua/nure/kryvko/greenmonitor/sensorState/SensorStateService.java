package ua.nure.kryvko.greenmonitor.sensorState;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.kryvko.greenmonitor.greenhouse.Greenhouse;
import ua.nure.kryvko.greenmonitor.greenhouse.GreenhouseRepository;
import ua.nure.kryvko.greenmonitor.notification.Notification;
import ua.nure.kryvko.greenmonitor.notification.NotificationRepository;
import ua.nure.kryvko.greenmonitor.notification.NotificationUrgency;
import ua.nure.kryvko.greenmonitor.plant.Plant;
import ua.nure.kryvko.greenmonitor.sensor.Sensor;
import ua.nure.kryvko.greenmonitor.sensor.SensorRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//TODO: write BOTH MQTT and REST implementations, one - for sensors, the other one - for users
@Service
public class SensorStateService {
    @Autowired
    private SensorStateRepository sensorStateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private GreenhouseRepository greenhouseRepository;

    @Transactional
    public SensorState saveSensorState(SensorState sensorState) {
        Sensor sensor = sensorRepository.findById(sensorState.getSensor().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found"));

        sensorState.setSensor(sensor);
        Greenhouse greenhouse = sensor.getGreenhouse();
        Plant plant = greenhouse.getPlant();

        StringBuilder messageBuilder = new StringBuilder();
        NotificationUrgency urgency = null;

        Float temp = sensorState.getTemperature();
        Float humidity = sensorState.getHumidity();
        Float moisture = sensorState.getMoisture();

        if (temp != null && !plant.isNormalTemperature(temp)) {
            messageBuilder.append(String.format("Temperature %.1f°C is out of range (%.1f°C - %.1f°C). ",
                    temp, plant.getMinTemperature(), plant.getMaxTemperature()));
            urgency = NotificationUrgency.WARNING;
        }

        if (humidity != null && !plant.isNormalHumidity(humidity)) {
            messageBuilder.append(String.format("Humidity %.1f%% is out of range (%.1f%% - %.1f%%). ",
                    humidity, plant.getMinHumidity(), plant.getMaxHumidity()));
            urgency = NotificationUrgency.WARNING;
        }

        if (moisture != null && !plant.isNormalMoisture(moisture)) {
            messageBuilder.append(String.format("Soil moisture %.1f is out of range (%.1f - %.1f). ",
                    moisture, plant.getMinMoisture(), plant.getMaxMoisture()));
            urgency = NotificationUrgency.WARNING;
        }

        SensorState savedState = sensorStateRepository.save(sensorState);

        if (!messageBuilder.isEmpty()) {
            Notification notification = new Notification();
            notification.setGreenhouse(greenhouse);
            notification.setUser(greenhouse.getUser());
            notification.setMessage(messageBuilder.toString().trim());
            notification.setTimestamp(new Date());
            notification.setNotificationUrgency(urgency);

            notificationRepository.save(notification);
        }

        return savedState;
    }

    public Optional<SensorState> getSensorStateById(Integer id) {
        return sensorStateRepository.findById(id);
    }

    public List<SensorState> getSensorStateByGreenhouseId(Integer id) {
        Greenhouse greenhouse = greenhouseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Greenhouse not found."));
        List<Sensor> sensors = greenhouse.getSensors();
        List<SensorState> sensorStates = new ArrayList<>();
        for (Sensor sensor : sensors) {
            sensorStates.addAll(sensorStateRepository.findBySensor(sensor));
        }
        return sensorStates;
    }

    public List<SensorState> getSensorStateBySensorId(Integer id) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found."));
        return sensorStateRepository.findBySensor(sensor);
    }

    @Transactional
    public SensorState updateSensorState(SensorState sensorState) {
        if (sensorState.getId() == null || !sensorStateRepository.existsById(sensorState.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SensorState ID not found for update.");
        }
        return sensorStateRepository.save(sensorState);
    }

    @Transactional
    public void deleteSensorStateById(Integer id) {
        if (!sensorStateRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SensorState ID not found for update.");
        }

        sensorStateRepository.deleteById(id);
    }

    public List<SensorState> getSensorStatesBetween(Integer id, LocalDateTime start, LocalDateTime end) {
        if (!sensorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor ID not found.");
        }

        return sensorStateRepository.findBySensorIdAndTimestampBetween(id, start, end);
    }
}
