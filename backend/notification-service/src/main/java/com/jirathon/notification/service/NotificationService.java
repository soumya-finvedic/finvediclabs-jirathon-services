package com.jirathon.notification.service;

import com.jirathon.notification.dto.request.NotificationPreferenceRequest;
import com.jirathon.notification.dto.response.NotificationResponse;
import com.jirathon.notification.exception.NotificationException;
import com.jirathon.notification.model.Notification;
import com.jirathon.notification.model.NotificationPreference;
import com.jirathon.notification.model.NotificationStatus;
import com.jirathon.notification.model.NotificationType;
import com.jirathon.notification.repository.NotificationPreferenceRepository;
import com.jirathon.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final EmailService emailService;
    private final WebSocketNotificationBroadcaster webSocketBroadcaster;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceRepository preferenceRepository,
                               EmailService emailService,
                               WebSocketNotificationBroadcaster webSocketBroadcaster) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.emailService = emailService;
        this.webSocketBroadcaster = webSocketBroadcaster;
    }
    
    /**
     * Create and send a notification
     */
    public NotificationResponse createAndSendNotification(
            String userId,
            String email,
            String fullName,
            NotificationType type,
            String title,
            String message,
            String subject,
            String relatedEntityId,
            String relatedEntityType,
            Map<String, String> metadata,
            boolean sendEmail,
            boolean broadcastWebSocket) {
        
        // Check user preferences
        NotificationPreference preference = getOrCreatePreference(userId);
        if (preference.isUnsubscribedAll()) {
            throw new NotificationException("User has unsubscribed from all notifications", "USER_UNSUBSCRIBED");
        }
        
        // Check notification type preference
        if (!isNotificationTypeEnabled(type, preference)) {
            log.debug("Notification type {} is disabled for user {}", type, userId);
            throw new NotificationException("Notification type is disabled for this user", "TYPE_DISABLED");
        }
        
        // Create notification document
        Notification notification = Notification.builder()
                .userId(userId)
                .email(email)
                .fullName(fullName)
                .type(type)
                .status(NotificationStatus.PENDING)
                .title(title)
                .message(message)
                .subject(subject)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .metadata(metadata)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(2592000)) // 30 days
                .build();
        
        // Save notification
        notification = notificationRepository.save(notification);
        log.info("Notification created: {} for user: {}", notification.getId(), userId);
        
        // Send email if enabled
        if (sendEmail && preference.isEmailNotificationsEnabled()) {
            try {
                emailService.sendNotificationEmail(notification);
                notification.setEmailSent(true);
            } catch (Exception e) {
                log.error("Failed to send email for notification {}: {}", notification.getId(), e.getMessage());
                notification.setEmailSent(false);
            }
        }
        
        // Broadcast via WebSocket if enabled
        if (broadcastWebSocket) {
            try {
                webSocketBroadcaster.broadcastNotification(notification);
                notification.setWebsocketSent(true);
            } catch (Exception e) {
                log.error("Failed to broadcast WebSocket notification {}: {}", notification.getId(), e.getMessage());
                notification.setWebsocketSent(false);
            }
        }
        
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(Instant.now());
        notification = notificationRepository.save(notification);
        
        return toResponse(notification);
    }
    
    /**
     * Get unread notifications for a user
     */
    public List<NotificationResponse> getUnreadNotifications(String userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationStatus.PENDING);
        return notifications.stream().map(this::toResponse).collect(Collectors.toList());
    }
    
    /**
     * Get paginated notifications for a user
     */
    public List<NotificationResponse> getUserNotifications(String userId, int limit) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get notifications by type for a user
     */
    public List<NotificationResponse> getUserNotificationsByType(String userId, NotificationType type) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        return notifications.stream().map(this::toResponse).collect(Collectors.toList());
    }
    
    /**
     * Mark notification as read
     */
    public NotificationResponse markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException("Notification not found", "NOT_FOUND"));
        
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(Instant.now());
        notification = notificationRepository.save(notification);
        
        return toResponse(notification);
    }
    
    /**
     * Archive notification
     */
    public NotificationResponse archiveNotification(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException("Notification not found", "NOT_FOUND"));
        
        notification.setStatus(NotificationStatus.ARCHIVED);
        notification.setArchivedAt(Instant.now());
        notification = notificationRepository.save(notification);
        
        return toResponse(notification);
    }
    
    /**
     * Get or create User preference
     */
    public NotificationPreference getOrCreatePreference(String userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreference pref = NotificationPreference.builder()
                            .userId(userId)
                            .emailNotificationsEnabled(true)
                            .paymentNotificationsEnabled(true)
                            .contestNotificationsEnabled(true)
                            .scoreUpdateNotificationsEnabled(true)
                            .leaderboardNotificationsEnabled(true)
                            .systemAlertNotificationsEnabled(true)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    return preferenceRepository.save(pref);
                });
    }
    
    /**
     * Update notification preferences
     */
    public NotificationPreference updatePreference(String userId, NotificationPreferenceRequest request) {
        NotificationPreference preference = getOrCreatePreference(userId);
        
        preference.setEmailNotificationsEnabled(request.isEmailNotificationsEnabled());
        preference.setPaymentNotificationsEnabled(request.isPaymentNotificationsEnabled());
        preference.setContestNotificationsEnabled(request.isContestNotificationsEnabled());
        preference.setScoreUpdateNotificationsEnabled(request.isScoreUpdateNotificationsEnabled());
        preference.setLeaderboardNotificationsEnabled(request.isLeaderboardNotificationsEnabled());
        preference.setSystemAlertNotificationsEnabled(request.isSystemAlertNotificationsEnabled());
        preference.setPreferredEmail(request.getPreferredEmail());
        preference.setTimezone(request.getTimezone());
        preference.setUnsubscribedAll(request.isUnsubscribedAll());
        preference.setUpdatedAt(Instant.now());
        
        return preferenceRepository.save(preference);
    }
    
    /**
     * Get unread count for a user
     */
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.PENDING);
    }
    
    private boolean isNotificationTypeEnabled(NotificationType type, NotificationPreference preference) {
        return switch (type) {
            case PAYMENT_SUCCESS -> preference.isPaymentNotificationsEnabled();
            case CONTEST_START, CONTEST_END, REGISTRATION_CONFIRMED -> preference.isContestNotificationsEnabled();
            case SCORE_UPDATE -> preference.isScoreUpdateNotificationsEnabled();
            case LEADERBOARD_ACHIEVEMENT -> preference.isLeaderboardNotificationsEnabled();
            case SYSTEM_ALERT -> preference.isSystemAlertNotificationsEnabled();
            default -> true;
        };
    }
    
    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .email(notification.getEmail())
                .fullName(notification.getFullName())
                .type(notification.getType())
                .status(notification.getStatus())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .subject(notification.getSubject())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .metadata(notification.getMetadata())
                .emailSent(notification.isEmailSent())
                .websocketSent(notification.isWebsocketSent())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .archivedAt(notification.getArchivedAt())
                .build();
    }
}
