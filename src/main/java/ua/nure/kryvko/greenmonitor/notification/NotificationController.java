package ua.nure.kryvko.greenmonitor.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody Notification notification) {
        try {
            Notification savedNotification = notificationService.createNotification(notification);
            return ResponseEntity.ok(NotificationDTOMapper.toDto(savedNotification));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessNotification(#id, authentication)")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Integer id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        if (notification.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(NotificationDTOMapper.toDto(notification.get()));
    }

    //TODO: add deducting user id by token
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable Integer id) {
        try {
            List<Notification> notifications = notificationService.getNotificationByUserId(id);
            return ResponseEntity.ok(notifications.stream().map(NotificationDTOMapper::toDto)
                    .collect(Collectors.toList()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/greenhouse/{id}")
    @PreAuthorize("@authorizationService.canAccessGreenhouse(#id, authentication)")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByGreenhouseId(@PathVariable Integer id) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByGreenhouse(id);
            return ResponseEntity.ok(notifications.stream().map(NotificationDTOMapper::toDto)
                    .collect(Collectors.toList()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/unread/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsByUser(@PathVariable Integer id) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsByUser(id);
            return ResponseEntity.ok(notifications.stream().map(NotificationDTOMapper::toDto)
                    .collect(Collectors.toList()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/urgency/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsByUser(@PathVariable Integer id, @RequestParam NotificationUrgency urgency) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsByUserUrgency(id, urgency);
            return ResponseEntity.ok(notifications.stream().map(NotificationDTOMapper::toDto)
                    .collect(Collectors.toList()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("mark-read/{id}")
    @PreAuthorize("@authorizationService.canAccessNotification(#id, authentication)")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Integer id) {
        try {
            Notification updatedNotification = notificationService.markAsRead(id);
            return ResponseEntity.ok(NotificationDTOMapper.toDto(updatedNotification));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessNotification(#id, authentication)")
    public ResponseEntity<NotificationResponse> updateNotification(@PathVariable Integer id, @RequestBody Notification notification) {
        try {
            notification.setId(id);
            Notification updatedNotification = notificationService.updateNotification(notification);
            return ResponseEntity.ok(NotificationDTOMapper.toDto(updatedNotification));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessNotification(#id, authentication)")
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer id) {
        try {
            notificationService.deleteNotificationById(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
