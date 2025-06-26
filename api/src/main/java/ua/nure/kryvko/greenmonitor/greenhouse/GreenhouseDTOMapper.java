package ua.nure.kryvko.greenmonitor.greenhouse;

import ua.nure.kryvko.greenmonitor.notification.NotificationDTOMapper;
import ua.nure.kryvko.greenmonitor.sensor.SensorDTOMapper;

public class GreenhouseDTOMapper {
    public static GreenhouseResponse toDto(Greenhouse greenhouse) {
        return new GreenhouseResponse(
                greenhouse.getId(),
                greenhouse.getUser().getId(),
                greenhouse.getPlant().getId(),
                greenhouse.getName(),
                greenhouse.getDescription(),
                greenhouse.getSensors().stream().map(SensorDTOMapper::toDto).toList(),
                greenhouse.getNotifications().stream().map(NotificationDTOMapper::toDto).toList()
        );
    }
}
