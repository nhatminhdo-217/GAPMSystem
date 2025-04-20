package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    List<Notification> findByTargetUser_IdOrderByCreateAtDesc(Long targetUserId);

    Page<Notification> findByTargetUser_IdOrderByCreateAtDesc(Long userId, Pageable pageable);

    @Query(
            "SELECT COUNT(n) FROM Notification n " +
            "WHERE n.targetUser.id = :userId AND n.read = false"
    )
    Long countUnreadNotifications(@Param("userId") Long userId);

    List<Notification> findByTargetUserIdAndReadOrderByCreateAtDesc(Long userId, boolean read);
}
