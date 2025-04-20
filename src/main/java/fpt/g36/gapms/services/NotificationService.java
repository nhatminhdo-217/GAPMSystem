package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.notification.NotificationDTO;
import fpt.g36.gapms.models.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    // Lưu thông báo vào database và gửi thông báo real-time
    Notification saveAndSendNotification(NotificationDTO notificationDTO);

    // Lấy tất cả thông báo của một user
    List<Notification> getAllNotificationsByUserId(Long userId);

    // Lấy thông báo có phân trang
    Page<Notification> getNotificationsByUserIdPaginated(Long userId, Pageable pageable);

    // Đánh dấu một thông báo là đã đọc
    Notification markNotificationAsRead(Long notificationId);

    // Đánh dấu tất cả thông báo của một user là đã đọc
    void markAllNotificationsAsRead(Long userId);

    // Đếm số thông báo chưa đọc
    Long countUnreadNotifications(Long userId);
}
