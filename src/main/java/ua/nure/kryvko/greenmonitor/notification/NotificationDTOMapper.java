package ua.nure.kryvko.greenmonitor.notification;

public class NotificationDTOMapper {
    public static NotificationResponse toDto(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                notification.getGreenhouse().getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getTimestamp(),
                notification.getNotificationUrgency()
        );
    }
}
