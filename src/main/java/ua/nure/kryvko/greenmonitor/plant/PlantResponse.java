package ua.nure.kryvko.greenmonitor.plant;

public class PlantResponse {
    Integer id;

    String name;

    Float minTemperature;

    Float maxTemperature;

    Float minHumidity;

    Float maxHumidity;

    Float minMoisture;

    Float maxMoisture;

    public PlantResponse() {}

    public PlantResponse(Integer id, String name, Float minTemperature, Float maxTemperature,
                         Float minHumidity, Float maxHumidity, Float minMoisture, Float maxMoisture) {
        this.id = id;
        this.name = name;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minMoisture = minMoisture;
        this.maxMoisture = maxMoisture;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Float getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(Float minHumidity) {
        this.minHumidity = minHumidity;
    }

    public Float getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(Float maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public Float getMinMoisture() {
        return minMoisture;
    }

    public void setMinMoisture(Float minMoisture) {
        this.minMoisture = minMoisture;
    }

    public Float getMaxMoisture() {
        return maxMoisture;
    }

    public void setMaxMoisture(Float maxMoisture) {
        this.maxMoisture = maxMoisture;
    }
}
