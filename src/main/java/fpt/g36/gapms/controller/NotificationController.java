package fpt.g36.gapms.controller;

import fpt.g36.gapms.models.dto.notification.NotificationDTO;
import fpt.g36.gapms.models.entities.Notification;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.services.NotificationService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final UserUtils userUtils;
    private final NotificationService notificationService;

    public NotificationController(UserUtils userUtils, NotificationService notificationService) {
        this.userUtils = userUtils;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String getNotifications() {
        return "notifications/list";
    }

    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNotification(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ){
        User currentUser = userUtils.getOptionalUserInfo(model);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getNotificationsByUserIdPaginated(currentUser.getId(), pageable);

        Long unreadCount = notificationService.countUnreadNotifications(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notifications.getContent());
        response.put("currentPage", notifications.getNumber());
        response.put("totalItems", notifications.getTotalElements());
        response.put("totalPages", notifications.getTotalPages());
        response.put("unreadCount", unreadCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/read/{id}")
    @ResponseBody
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id, Model model) {
        User currentUser = userUtils.getOptionalUserInfo(model);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        Notification notification = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/api/read-all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAllAsRead(Model model) {
        User currentUser = userUtils.getOptionalUserInfo(model);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        notificationService.markAllNotificationsAsRead(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUnreadCount(Model model) {
        User currentUser = userUtils.getOptionalUserInfo(model);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        long unreadCount = notificationService.countUnreadNotifications(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        return ResponseEntity.ok(response);
    }

    @MessageMapping("/send-notification")
    public void sendNotification(@Payload NotificationDTO notificationDTO) {
        notificationService.saveAndSendNotification(notificationDTO);
    }

}
