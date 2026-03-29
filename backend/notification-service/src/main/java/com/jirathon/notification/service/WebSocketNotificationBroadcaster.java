package com.jirathon.notification.service;

import com.jirathon.notification.dto.response.NotificationResponse;
import com.jirathon.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(WebSocketNotificationBroadcaster.class);
    
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * Broadcast notification to specific user via WebSocket
     */
    public void broadcastNotification(Notification notification) {
        try {
            NotificationResponse response = toResponse(notification);
            String destination = "/user/" + notification.getUserId() + "/queue/notifications";
            messagingTemplate.convertAndSend(destination, response);
            log.debug("WebSocket notification broadcast to user: {} on destination: {}", 
                    notification.getUserId(), destination);
        } catch (Exception e) {
            log.error("Failed to broadcast notification via WebSocket: {}", e.getMessage());
            throw new RuntimeException("WebSocket broadcast failed", e);
        }
    }
    
    /**
     * Broadcast notification to all connected clients
     */
    public void broadcastNotificationToAll(Notification notification) {
        try {
            NotificationResponse response = toResponse(notification);
            messagingTemplate.convertAndSend("/topic/notifications", response);
            log.debug("WebSocket notification broadcast to all clients");
        } catch (Exception e) {
            log.error("Failed to broadcast notification to all via WebSocket: {}", e.getMessage());
            throw new RuntimeException("WebSocket broadcast to all failed", e);
        }
    }
    
    /**
     * Broadcast to specific topic (e.g., contest-specific notifications)
     */
    public void broadcastToTopic(String topic, Notification notification) {
        try {
            NotificationResponse response = toResponse(notification);
            messagingTemplate.convertAndSend("/topic/" + topic, response);
            log.debug("WebSocket notification broadcast to topic: {}", topic);
        } catch (Exception e) {
            log.error("Failed to broadcast notification to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("WebSocket broadcast to topic failed", e);
        }
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
