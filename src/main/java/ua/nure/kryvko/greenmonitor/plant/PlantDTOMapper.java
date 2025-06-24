package ua.nure.kryvko.greenmonitor.plant;

public class PlantDTOMapper {
    public static PlantResponse toDto(Plant plant) {
        return new PlantResponse(
                plant.getId(),
                plant.getName(),
                plant.getMinTemperature(),
                plant.getMaxTemperature(),
                plant.getMinHumidity(),
                plant.getMaxHumidity(),
                plant.getMinMoisture(),
                plant.getMaxMoisture()
        );
    }
}
