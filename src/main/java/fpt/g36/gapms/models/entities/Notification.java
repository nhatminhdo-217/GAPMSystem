package fpt.g36.gapms.models.entities;

import fpt.g36.gapms.enums.NotificationEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationEnum type;

    @Column(name = "target_url")
    private String targetUrl;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @Column(nullable = false)
    private String source;

    // Constructors, getters, and setters

    public Notification() {
    }

    public Notification(Long id, LocalDateTime createAt, LocalDateTime updateAt,
                        String message, NotificationEnum type, String targetUrl,
                        boolean read, User targetUser, String source) {
        super(id, createAt, updateAt);
        this.message = message;
        this.type = type;
        this.targetUrl = targetUrl;
        this.read = read;
        this.targetUser = targetUser;
        this.source = source;
    }

    // Getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationEnum getType() {
        return type;
    }

    public void setType(NotificationEnum type) {
        this.type = type;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}