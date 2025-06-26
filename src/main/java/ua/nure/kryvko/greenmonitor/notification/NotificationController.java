package ua.nure.kryvko.greenmonitor.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.kryvko.greenmonitor.auth.CustomUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

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

    @GetMapping("/my")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(Authentication authentication) {
        try {
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            List<Notification> notifications = notificationService.getNotificationByUserId(userId);
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

    @GetMapping("/my/unread")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsByUser(Authentication authentication) {
        try {
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            List<Notification> notifications = notificationService.getUnreadNotificationsByUser(userId);
            return ResponseEntity.ok(notifications.stream().map(NotificationDTOMapper::toDto)
                    .collect(Collectors.toList()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my/urgency")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotificationsByUser(Authentication authentication,
                                                                                   @RequestParam NotificationUrgency urgency) {
        try {
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            List<Notification> notifications = notificationService.getUnreadNotificationsByUserUrgency(userId, urgency);
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
