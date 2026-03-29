package com.jirathon.notification.controller;

import com.jirathon.notification.dto.request.NotificationPreferenceRequest;
import com.jirathon.notification.dto.request.TestNotificationRequest;
import com.jirathon.notification.dto.response.ApiResponse;
import com.jirathon.notification.dto.response.NotificationResponse;
import com.jirathon.notification.model.NotificationPreference;
import com.jirathon.notification.model.NotificationType;
import com.jirathon.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Get unread notifications for authenticated user
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Unread notifications retrieved"));
    }
    
    /**
     * Get all notifications for authenticated user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUserNotifications(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "50") int limit) {
        log.info("Fetching notifications for user: {} with limit: {}", userId, limit);
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId, limit);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications retrieved"));
    }
    
    /**
     * Get notifications by type for authenticated user
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByType(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable NotificationType type) {
        log.info("Fetching {} notifications for user: {}", type, userId);
        List<NotificationResponse> notifications = notificationService.getUserNotificationsByType(userId, type);
        return ResponseEntity.ok(ApiResponse.success(notifications, String.format("Notifications of type %s retrieved", type)));
    }
    
    /**
     * Get unread count for authenticated user
     */
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Fetching unread count for user: {}", userId);
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count, "Unread count retrieved"));
    }
    
    /**
     * Mark notification as read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable String notificationId,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Marking notification {} as read for user: {}", notificationId, userId);
        NotificationResponse notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification marked as read"));
    }
    
    /**
     * Archive notification
     */
    @PutMapping("/{notificationId}/archive")
    public ResponseEntity<ApiResponse<NotificationResponse>> archiveNotification(
            @PathVariable String notificationId,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Archiving notification {} for user: {}", notificationId, userId);
        NotificationResponse notification = notificationService.archiveNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification archived"));
    }
    
    /**
     * Get notification preferences for authenticated user
     */
    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<NotificationPreference>> getPreferences(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Fetching preferences for user: {}", userId);
        NotificationPreference preference = notificationService.getOrCreatePreference(userId);
        return ResponseEntity.ok(ApiResponse.success(preference, "Preferences retrieved"));
    }
    
    /**
     * Update notification preferences for authenticated user
     */
    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<NotificationPreference>> updatePreferences(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody NotificationPreferenceRequest request) {
        log.info("Updating preferences for user: {}", userId);
        request.setUserId(userId);
        NotificationPreference preference = notificationService.updatePreference(userId, request);
        return ResponseEntity.ok(ApiResponse.success(preference, "Preferences updated"));
    }
    
    /**
     * Admin endpoint: Send test notification
     * Requires ADMIN role
     */
    @PostMapping("/admin/test")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendTestNotification(
            @RequestHeader("X-User-Roles") String roles,
            @Valid @RequestBody TestNotificationRequest request) {
        log.info("Admin sending test notification to user: {}", request.getUserId());
        
        // Check if user has ADMIN role (in real app, use @PreAuthorize)
        if (!roles.contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, "Insufficient permissions"));
        }
        
        try {
            NotificationResponse notification = notificationService.createAndSendNotification(
                    request.getUserId(),
                    request.getEmail(),
                    request.getFullName(),
                    request.getType(),
                    request.getTitle(),
                    request.getMessage(),
                    request.getSubject(),
                    null,
                    "TEST",
                    request.getMetadata(),
                    request.isSendEmail(),
                    request.isBroadcastWebSocket()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(notification, "Test notification sent"));
        } catch (Exception e) {
            log.error("Error sending test notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Error sending notification: " + e.getMessage()));
        }
    }
}
