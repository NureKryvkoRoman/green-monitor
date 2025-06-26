package ua.nure.kryvko.greenmonitor.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.kryvko.greenmonitor.greenhouse.Greenhouse;
import ua.nure.kryvko.greenmonitor.user.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUser(User user);
    List<Notification> findAllByGreenhouse(Greenhouse greenhouse);
    List<Notification> findByUserAndIsReadFalse(User user);
    List<Notification> findByUserAndNotificationUrgency(User user, NotificationUrgency notificationUrgency);
}
