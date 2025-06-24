package ua.nure.kryvko.greenmonitor.notification;

import com.google.inject.internal.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import ua.nure.kryvko.greenmonitor.greenhouse.Greenhouse;
import ua.nure.kryvko.greenmonitor.user.User;

import java.util.Date;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    @Nullable
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "greenhouse_id", referencedColumnName = "id")
    Greenhouse greenhouse;

    @NotNull
    String message;

    @NotNull
    boolean isRead = false;

    @NotNull
    Date timestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    NotificationUrgency notificationUrgency;

    public Notification() {}

    public Notification(Integer id, User user, Greenhouse greenhouse, String message,
                        boolean isRead, Date timestamp, NotificationUrgency notificationUrgency) {
        this.id = id;
        this.user = user;
        this.greenhouse = greenhouse;
        this.message = message;
        this.isRead = isRead;
        this.timestamp = timestamp;
        this.notificationUrgency = notificationUrgency;
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

    public Greenhouse getGreenhouse() {
        return greenhouse;
    }

    public void setGreenhouse(Greenhouse greenhouse) {
        this.greenhouse = greenhouse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationUrgency getNotificationUrgency() {
        return notificationUrgency;
    }

    public void setNotificationUrgency(NotificationUrgency notificationUrgency) {
        this.notificationUrgency = notificationUrgency;
    }
}
