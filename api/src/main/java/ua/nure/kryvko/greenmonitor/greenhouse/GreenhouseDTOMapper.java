package ua.nure.kryvko.greenmonitor.greenhouse;

import ua.nure.kryvko.greenmonitor.notification.NotificationDTOMapper;
import ua.nure.kryvko.greenmonitor.notification.NotificationResponse;
import ua.nure.kryvko.greenmonitor.sensor.SensorDTOMapper;
import ua.nure.kryvko.greenmonitor.sensor.SensorResponse;

import java.util.ArrayList;
import java.util.List;

public class GreenhouseDTOMapper {
    public static GreenhouseResponse toDto(Greenhouse greenhouse) {
        return new GreenhouseResponse(
                greenhouse.getId(),
                greenhouse.getUser().getId(),
                greenhouse.getPlant().getId(),
                greenhouse.getName(),
                greenhouse.getDescription(),
                convertSensors(greenhouse),
                convertNotifications(greenhouse)
        );
    }

    private static List<SensorResponse> convertSensors(Greenhouse greenhouse) {
        if (greenhouse.getSensors() != null) {
            return greenhouse.getSensors().stream().map(SensorDTOMapper::toDto).toList();
        }
        return new ArrayList<>();
    }

    private static List<NotificationResponse> convertNotifications(Greenhouse greenhouse) {
        if (greenhouse.getNotifications() != null) {
            return greenhouse.getNotifications().stream().map(NotificationDTOMapper::toDto).toList();
        }
        return new ArrayList<>();
    }
}
