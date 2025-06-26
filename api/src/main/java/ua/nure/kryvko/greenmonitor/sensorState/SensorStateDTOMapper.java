package ua.nure.kryvko.greenmonitor.sensorState;

public class SensorStateDTOMapper {
    public static SensorStateResponse toDto(SensorState sensorState) {
        return new SensorStateResponse(
                sensorState.getId(),
                sensorState.getSensor().getId(),
                sensorState.getTimestamp(),
                sensorState.getTemperature(),
                sensorState.getHumidity(),
                sensorState.getMoisture()
        );
    }
}
