package ua.nure.kryvko.greenmonitor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ua.nure.kryvko.greenmonitor.greenhouse.Greenhouse;
import ua.nure.kryvko.greenmonitor.greenhouse.GreenhouseRepository;
import ua.nure.kryvko.greenmonitor.notification.Notification;
import ua.nure.kryvko.greenmonitor.notification.NotificationRepository;
import ua.nure.kryvko.greenmonitor.auth.CustomUserDetails;
import ua.nure.kryvko.greenmonitor.sensor.Sensor;
import ua.nure.kryvko.greenmonitor.sensor.SensorRepository;
import ua.nure.kryvko.greenmonitor.sensorState.SensorState;
import ua.nure.kryvko.greenmonitor.sensorState.SensorStateRepository;

import java.util.Optional;

@Component("authorizationService")
public class AuthorizationService {
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private GreenhouseRepository greenhouseRepository;
    @Autowired
    private SensorStateRepository sensorStateRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    public boolean canAccessSensor(Integer sensorId, Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        Optional<Sensor> optionalSensor = sensorRepository.findById(sensorId);
        if (optionalSensor.isEmpty()) {
            return false;
        }

        Sensor sensor = optionalSensor.get();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return sensor.getGreenhouse().getUser().getId().equals(user.getId());
    }

    public boolean canAccessGreenhouse(Integer greenhouseId, Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        Optional<Greenhouse> optionalGreenhouse = greenhouseRepository.findById(greenhouseId);
        if (optionalGreenhouse.isEmpty()) {
            return false;
        }

        Greenhouse greenhouse = optionalGreenhouse.get();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return greenhouse.getUser().getId().equals(user.getId());
    }

    public boolean canAccessSensorState(Integer stateId, Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        Optional<SensorState> optionalSensorState = sensorStateRepository.findById(stateId);
        if (optionalSensorState.isEmpty()) {
            return false;
        }

        SensorState sensorState = optionalSensorState.get();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return sensorState.getSensor().getGreenhouse().getUser().getId().equals(user.getId());
    }

    public boolean canAccessNotification(Integer notificationId, Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isEmpty()) {
            return false;
        }

        Notification notification = optionalNotification.get();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return notification.getUser().getId().equals(user.getId());
    }

}
