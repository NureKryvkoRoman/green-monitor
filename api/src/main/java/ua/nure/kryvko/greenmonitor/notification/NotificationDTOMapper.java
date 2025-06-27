package ua.nure.kryvko.greenmonitor.notification;

public class NotificationDTOMapper {
    public static NotificationResponse toDto(Notification notification) {
        Integer greenhouseId = null;
        if (notification.getGreenhouse() != null) {
            greenhouseId = notification.getGreenhouse().getId();
        }

        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                greenhouseId,
                notification.getMessage(),
                notification.isRead(),
                notification.getTimestamp(),
                notification.getNotificationUrgency()
        );
    }
}
