package ua.nure.kryvko.greenmonitor.greenhouse;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.kryvko.greenmonitor.plant.Plant;
import ua.nure.kryvko.greenmonitor.plant.PlantRepository;
import ua.nure.kryvko.greenmonitor.sensor.Sensor;
import ua.nure.kryvko.greenmonitor.sensor.SensorRepository;
import ua.nure.kryvko.greenmonitor.sensorState.SensorState;
import ua.nure.kryvko.greenmonitor.sensorState.SensorStateRepository;
import ua.nure.kryvko.greenmonitor.user.User;
import ua.nure.kryvko.greenmonitor.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GreenhouseService {
    @Autowired
    private GreenhouseRepository greenhouseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private SensorStateRepository sensorStateRepository;

    @Autowired
    private SensorRepository sensorRepository;

    public double calculateGDD(int greenhouseId, double baseTemperature, LocalDate from, LocalDate to) {
        List<Sensor> sensors = sensorRepository.findByGreenhouseId(greenhouseId);
        if (sensors.isEmpty()) return 0;

        List<Float> dailyGDDs = new ArrayList<>();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            List<Float> dayTemps = new ArrayList<>();
            for (Sensor sensor : sensors) {
                List<SensorState> states = sensorStateRepository.findBySensorIdAndTimestampBetween(sensor.getId(), start, end);
                for (SensorState s : states) {
                    dayTemps.add(s.getTemperature());
                }
            }

            if (!dayTemps.isEmpty()) {
                double tMin = Collections.min(dayTemps);
                double tMax = Collections.max(dayTemps);
                double gdd = ((tMax + tMin) / 2) - baseTemperature;
                dailyGDDs.add(Math.max(0, (float) gdd)); // GDD can't be negative
            }
        }

        return dailyGDDs.stream().mapToDouble(Float::doubleValue).sum();
    }

    public double calculateDewPoint(int greenhouseId, LocalDate date) {
        List<Sensor> sensors = sensorRepository.findByGreenhouseId(greenhouseId);

        if (sensors.isEmpty()) return 0;

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Float> tempValues = new ArrayList<>();
        List<Float> humidityValues = new ArrayList<>();

        for (Sensor sensor : sensors) {
            List<SensorState> states = sensorStateRepository.findBySensorIdAndTimestampBetween(sensor.getId(), start, end);
            for (SensorState s : states) tempValues.add(s.getTemperature());
        }

        for (Sensor sensor : sensors) {
            List<SensorState> states = sensorStateRepository.findBySensorIdAndTimestampBetween(sensor.getId(), start, end);
            for (SensorState s : states) humidityValues.add(s.getHumidity());
        }

        if (tempValues.isEmpty() || humidityValues.isEmpty()) return 0;

        double avgTemp = tempValues.stream().mapToDouble(Float::doubleValue).average().orElse(0);
        double avgRH = humidityValues.stream().mapToDouble(Float::doubleValue).average().orElse(0);

        // Approximate dew point formula
        return avgTemp - ((100 - avgRH) / 5);
    }

    @Transactional
    public Greenhouse saveGreenhouse(Greenhouse greenhouse) {
        User owner = userRepository.findById(greenhouse.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        Plant plant = plantRepository.findById(greenhouse.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plant not found"));

        // Ensure the user and plant are managed entities
        greenhouse.setUser(owner);
        greenhouse.setPlant(plant);
        return greenhouseRepository.save(greenhouse);
    }

    public Plant getPlantByGreenhouseId(Integer greenhouseId) {
        Greenhouse greenhouse = greenhouseRepository.findById(greenhouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Greenhouse with id " +
                        greenhouseId + " not found."));
        return greenhouse.getPlant();
    }

    public Optional<Greenhouse> getGreenhouseById(Integer id) {
        return greenhouseRepository.findById(id);
    }

    @Transactional
    public Greenhouse updateGreenhouse(Integer id, Greenhouse greenhouse) {
        greenhouse.setId(id);
        if (greenhouse.getId() == null || !greenhouseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Greenhouse ID not found for update.");
        }

        User owner = userRepository.findById(greenhouse.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Plant plant = plantRepository.findById(greenhouse.getPlant().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plant not found"));

        greenhouse.setUser(owner);
        greenhouse.setPlant(plant);
        return greenhouseRepository.save(greenhouse);
    }

    @Transactional
    public void deleteGreenhouseById(Integer id) {
        if (!greenhouseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Greenhouse ID not found for deletion.");
        }
        greenhouseRepository.deleteById(id);
    }

    public List<GreenhouseSummary> getGreenhousesSummaryByUserId(Integer userId) {
        return greenhouseRepository.findSummaryByUserId(userId);
    }

    public List<Greenhouse> getGreenhousesByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return greenhouseRepository.findByUser(user);
    }

    public List<Greenhouse> getGreenhousesByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return greenhouseRepository.findByUser(user);
    }
}
