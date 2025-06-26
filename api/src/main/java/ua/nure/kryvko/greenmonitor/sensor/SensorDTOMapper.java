package ua.nure.kryvko.greenmonitor.sensor;

public class SensorDTOMapper {
    public static SensorResponse toDto(Sensor sensor) {
        return new SensorResponse(
                sensor.getId(),
                sensor.getGreenhouse().getId(),
                sensor.getNote()
        );
    }
}
