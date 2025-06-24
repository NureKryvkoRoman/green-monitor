package ua.nure.kryvko.greenmonitor.greenhouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ua.nure.kryvko.greenmonitor.notification.Notification;
import ua.nure.kryvko.greenmonitor.plant.Plant;
import ua.nure.kryvko.greenmonitor.sensor.Sensor;
import ua.nure.kryvko.greenmonitor.user.User;

import java.util.List;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "greenhouse")
public class Greenhouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    @OneToMany(mappedBy = "greenhouse")
    List<Notification> notifications;

    @OneToMany(mappedBy = "greenhouse")
    List<Sensor> sensors;

    @ManyToOne
    @JoinColumn(name = "plant_id", referencedColumnName = "id", nullable = false)
    Plant plant;

    @NotBlank
    String name;

    String description;

    public Greenhouse() {}

    public Greenhouse(Integer id, User user, Plant plant, String name,
                      String description, List<Notification> notifications, List<Sensor> sensors) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.notifications = notifications;
        this.sensors = sensors;
        this.plant = plant;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}
