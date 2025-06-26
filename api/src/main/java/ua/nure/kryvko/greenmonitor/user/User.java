package ua.nure.kryvko.greenmonitor.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ua.nure.kryvko.greenmonitor.greenhouse.Greenhouse;
import ua.nure.kryvko.greenmonitor.notification.Notification;

import java.util.List;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "application_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Email
    @NotEmpty
    String email;

    @NotEmpty
    String password;

    @Enumerated(EnumType.STRING)
    UserRole role;

    @OneToMany(mappedBy = "user")
    List<Greenhouse> greenhouses;

    @OneToMany(mappedBy = "user")
    List<Notification> notifications;

    public User() {}

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    public List<Greenhouse> getGreenhouses() {
        return greenhouses;
    }

    public void setGreenhouses(List<Greenhouse> greenhouses) {
        this.greenhouses = greenhouses;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
