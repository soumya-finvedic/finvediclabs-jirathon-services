package com.jirathon.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String email;

    private String fullName;

    @Indexed
    private NotificationType type;

    @Indexed
    private NotificationStatus status;

    private String title;

    private String message;

    private String subject;

    private String relatedEntityId;

    private String relatedEntityType;

    private Map<String, String> metadata = new HashMap<>();

    private boolean emailSent;

    private boolean websocketSent;

    @Indexed
    private Instant createdAt;

    private Instant sentAt;

    private Instant readAt;

    private Instant archivedAt;

    @Indexed(expireAfterSeconds = 2592000)
    private Instant expiresAt;

    public Notification() {
        this.metadata = new HashMap<>();
    }

    public Notification(String id, String userId, String email, String fullName, NotificationType type,
                        NotificationStatus status, String title, String message, String subject,
                        String relatedEntityId, String relatedEntityType, Map<String, String> metadata,
                        boolean emailSent, boolean websocketSent, Instant createdAt, Instant sentAt,
                        Instant readAt, Instant archivedAt, Instant expiresAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.type = type;
        this.status = status;
        this.title = title;
        this.message = message;
        this.subject = subject;
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
        this.metadata = metadata != null ? metadata : new HashMap<>();
        this.emailSent = emailSent;
        this.websocketSent = websocketSent;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.readAt = readAt;
        this.archivedAt = archivedAt;
        this.expiresAt = expiresAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(String relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public boolean isWebsocketSent() {
        return websocketSent;
    }

    public void setWebsocketSent(boolean websocketSent) {
        this.websocketSent = websocketSent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Instant archivedAt) {
        this.archivedAt = archivedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public static final class Builder {
        private String id;
        private String userId;
        private String email;
        private String fullName;
        private NotificationType type;
        private NotificationStatus status;
        private String title;
        private String message;
        private String subject;
        private String relatedEntityId;
        private String relatedEntityType;
        private Map<String, String> metadata = new HashMap<>();
        private boolean emailSent;
        private boolean websocketSent;
        private Instant createdAt;
        private Instant sentAt;
        private Instant readAt;
        private Instant archivedAt;
        private Instant expiresAt;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder status(NotificationStatus status) {
            this.status = status;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder relatedEntityId(String relatedEntityId) {
            this.relatedEntityId = relatedEntityId;
            return this;
        }

        public Builder relatedEntityType(String relatedEntityType) {
            this.relatedEntityType = relatedEntityType;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata != null ? metadata : new HashMap<>();
            return this;
        }

        public Builder emailSent(boolean emailSent) {
            this.emailSent = emailSent;
            return this;
        }

        public Builder websocketSent(boolean websocketSent) {
            this.websocketSent = websocketSent;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder sentAt(Instant sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public Builder readAt(Instant readAt) {
            this.readAt = readAt;
            return this;
        }

        public Builder archivedAt(Instant archivedAt) {
            this.archivedAt = archivedAt;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Notification build() {
            return new Notification(id, userId, email, fullName, type, status, title, message, subject,
                    relatedEntityId, relatedEntityType, metadata, emailSent, websocketSent, createdAt,
                    sentAt, readAt, archivedAt, expiresAt);
        }
    }
}
