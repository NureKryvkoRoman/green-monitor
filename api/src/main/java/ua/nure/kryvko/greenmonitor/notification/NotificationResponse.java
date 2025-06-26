package ua.nure.kryvko.greenmonitor.notification;

import java.util.Date;

public class NotificationResponse {
    Integer id;
    Integer userId;
    Integer greenhouseId;
    String message;
    boolean isRead;
    Date timestamp;
    NotificationUrgency notificationUrgency;

    public NotificationResponse() {}

    public NotificationResponse(Integer id, Integer userId, Integer greenhouseId,
                                String message, boolean isRead, Date timestamp, NotificationUrgency notificationUrgency) {
        this.id = id;
        this.userId = userId;
        this.greenhouseId = greenhouseId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(Integer greenhouseId) {
        this.greenhouseId = greenhouseId;
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
