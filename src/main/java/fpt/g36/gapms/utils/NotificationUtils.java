package fpt.g36.gapms.utils;

import fpt.g36.gapms.enums.NotificationEnum;
import fpt.g36.gapms.models.dto.notification.NotificationDTO;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.NotificationService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationUtils {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public NotificationUtils(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public void sendNotificationToRole(String role, String msg, NotificationEnum type,
                                       String targetUrl, String src, Boolean sendSms) {
        List<User> users = userRepository.findAllByRole(role);
        for (User user : users) {
            NotificationDTO notification = createNotification(
                    msg, type, targetUrl, user.getId(), src
            );
            notificationService.saveAndSendMultiChannelNotification(notification, sendSms);
        }
    }

    public void sendNotificationToUser(Long userId, String msg, NotificationEnum type, String targetUrl, String src, Boolean sendSms) {
        NotificationDTO notification = createNotification(
                msg, type, targetUrl, userId, src
        );
        notificationService.saveAndSendMultiChannelNotification(notification, sendSms);
    }

    // This method is used to create a notification object
    public void sendRfqApproveToTechnical(Long rfqId) {
        String msg = "Một RFQ mới (#" + rfqId + ") đã được chấp nhận và đang chờ xử lý.";
        String targetUrl = "/technical/rfq-details/" + rfqId;
        sendNotificationToRole("TECHNICAL", msg, NotificationEnum.SUCCESS, targetUrl, "Quản lý RFQ", true);
    }

    private NotificationDTO createNotification(String message, NotificationEnum type,
                                               String targetUrl, Long targetUserId, String source) {
        NotificationDTO notification = new NotificationDTO();
        notification.setMessage(message);
        notification.setType(type);
        notification.setTargetUrl(targetUrl);
        notification.setTargetUserId(targetUserId);
        notification.setSource(source);
        notification.setTimestamp(LocalDateTime.now());
        return notification;
    }
}
