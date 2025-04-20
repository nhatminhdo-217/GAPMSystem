package fpt.g36.gapms.models.dto.notification;

import fpt.g36.gapms.enums.NotificationEnum;

import java.time.LocalDateTime;

public class NotificationDTO {
    private String id;
    private String message;
    private NotificationEnum type;
    private String targetUrl;
    private LocalDateTime timestamp;
    private boolean read;
    private Long targetUserId;
    private String source;

    public NotificationDTO() {
    }

    public NotificationDTO(String id, String message, NotificationEnum type, String targetUrl,
                            LocalDateTime timestamp, boolean read, Long targetUserId, String source) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.targetUrl = targetUrl;
        this.timestamp = timestamp;
        this.read = read;
        this.targetUserId = targetUserId;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
