package ua.nure.kryvko.greenmonitor.greenhouse;

import ua.nure.kryvko.greenmonitor.notification.NotificationResponse;
import ua.nure.kryvko.greenmonitor.sensor.SensorResponse;

import java.util.List;

public class GreenhouseResponse {
    Integer id;
    Integer userId;
    Integer plantId;
    String name;
    String description;
    List<SensorResponse> sensors;
    List<NotificationResponse> notifications;

    public GreenhouseResponse() {}

    public GreenhouseResponse(Integer id, Integer userId, Integer plantId, String name,
                              String description, List<SensorResponse> sensors, List<NotificationResponse> notifications) {
        this.id = id;
        this.userId = userId;
        this.plantId = plantId;
        this.name = name;
        this.description = description;
        this.sensors = sensors;
        this.notifications = notifications;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SensorResponse> getSensors() {
        return sensors;
    }

    public void setSensors(List<SensorResponse> sensors) {
        this.sensors = sensors;
    }

    public List<NotificationResponse> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationResponse> notifications) {
        this.notifications = notifications;
    }

    public Integer getPlantId() {
        return plantId;
    }

    public void setPlantId(Integer plantId) {
        this.plantId = plantId;
    }
}
