package ua.nure.kryvko.greenmonitor.sensorState;

import java.util.Date;

public class SensorStateResponse {
    Integer id;
    Integer sensorId;
    Date timestamp;
    Float temperature;
    Float humidity;
    Float moisture;

    public SensorStateResponse() {}

    public SensorStateResponse(Integer id, Integer sensorId, Date timestamp, Float temperature, Float humidity, Float moisture) {
        this.id = id;
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.moisture = moisture;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Float getMoisture() {
        return moisture;
    }

    public void setMoisture(Float moisture) {
        this.moisture = moisture;
    }
}
