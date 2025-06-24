package ua.nure.kryvko.greenmonitor.sensorState;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.kryvko.greenmonitor.greenhouse.Greenhouse;
import ua.nure.kryvko.greenmonitor.greenhouse.GreenhouseRepository;
import ua.nure.kryvko.greenmonitor.sensor.Sensor;
import ua.nure.kryvko.greenmonitor.sensor.SensorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO: write BOTH MQTT and REST implementations, one - for sensors, the other one - for users
@Service
public class SensorStateService {
    @Autowired
    private SensorStateRepository sensorStateRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private GreenhouseRepository greenhouseRepository;

    @Transactional
    public SensorState saveSensorState(SensorState sensorState) {
        Sensor owner = sensorRepository.findById(sensorState.getSensor().getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found"));
        sensorState.setSensor(owner);

        Greenhouse greenhouse = owner.getGreenhouse();
        return sensorStateRepository.save(sensorState);
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
}
