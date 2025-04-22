package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.notification.NotificationDTO;
import fpt.g36.gapms.models.entities.Notification;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.NotificationRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.NotificationService;
import fpt.g36.gapms.services.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SmsService smsService;

    public NotificationServiceImpl(UserRepository userRepository, NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate, SmsService smsService) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.smsService = smsService;
    }

    @Override
    public Notification saveAndSendNotification(NotificationDTO notificationDTO) {
        User targetUser = userRepository.findById(notificationDTO.getTargetUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + notificationDTO.getTargetUserId()));

        Notification notification = new Notification();
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(notificationDTO.getType());
        notification.setTargetUrl(notificationDTO.getTargetUrl());
        notification.setRead(false);
        notification.setTargetUser(targetUser);
        notification.setSource(notificationDTO.getSource());
        notification.setCreateAt(LocalDateTime.now());
        notification.setUpdateAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);

        // Create notificationDTO to send over WebSocket
        NotificationDTO responseDTO = getNotificationDTO(savedNotification);

        messagingTemplate.convertAndSendToUser(
                targetUser.getId().toString(),
                "/queue/notifications",
                responseDTO
        );

        return savedNotification;
    }

    @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByTargetUser_IdOrderByCreateAtDesc(userId);
    }

    @Override
    public Page<Notification> getNotificationsByUserIdPaginated(Long userId, Pageable pageable) {
        return notificationRepository.findByTargetUser_IdOrderByCreateAtDesc(userId, pageable);
    }

    @Override
    @Transactional
    public Notification markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        notification.setRead(true);
        notification.setUpdateAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByTargetUserIdAndReadOrderByCreateAtDesc(userId, false);

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setUpdateAt(LocalDateTime.now());
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }

    @Override
    @Transactional
    public Notification saveAndSendMultiChannelNotification(NotificationDTO notificationDTO, boolean sendSms) {

        User targetUser = userRepository.findById(notificationDTO.getTargetUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + notificationDTO.getTargetUserId()));

        Notification notification = saveAndSendNotification(notificationDTO);

        // Send SMS if required
        if (sendSms && targetUser.getSmsEnabled()) {
            String smsContent = notificationDTO.getMessage();
            String header = "localhost:8080";
            if (notificationDTO.getTargetUrl() != null && !notificationDTO.getTargetUrl().isEmpty()) {
                smsContent += " Chi tiết tại: " + header + notificationDTO.getTargetUrl();
            }

            smsService.sendSmsNotification(notificationDTO.getTargetUserId(), smsContent);
        }
        return notification;
    }


    // Private Method
    private static NotificationDTO getNotificationDTO(Notification savedNotification) {
        NotificationDTO responseDTO = new NotificationDTO();
        responseDTO.setId(savedNotification.getId().toString());
        responseDTO.setMessage(savedNotification.getMessage());
        responseDTO.setType(savedNotification.getType());
        responseDTO.setTargetUrl(savedNotification.getTargetUrl());
        responseDTO.setTimestamp(savedNotification.getCreateAt());
        responseDTO.setRead(savedNotification.isRead());
        responseDTO.setTargetUserId(savedNotification.getTargetUser().getId());
        responseDTO.setSource(savedNotification.getSource());
        return responseDTO;
    }

    private String createShortUrl(String longUrl) {
        if (longUrl == null || longUrl.isEmpty()) {
            return "";
        }

        try {
            //Use TinyURL API to shorten the URL
            String apiUrl = "https://tinyurl.com/api-create.php?url=" + URLEncoder.encode(longUrl, StandardCharsets.UTF_8);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                String shortUrl = reader.readLine();
                if (shortUrl != null && !shortUrl.isEmpty()) {
                    return shortUrl;
                }
            }

        } catch (IOException e){
            logger.error("Error shortening URL: {}", e.getMessage(), e);
        }
        return longUrl;
    }
}
