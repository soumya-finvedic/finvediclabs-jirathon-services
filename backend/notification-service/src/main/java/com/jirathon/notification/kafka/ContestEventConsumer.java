package com.jirathon.notification.kafka;

import com.jirathon.notification.event.ContestStartEvent;
import com.jirathon.notification.event.ContestEndEvent;
import com.jirathon.notification.service.NotificationService;
import com.jirathon.notification.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ContestEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ContestEventConsumer.class);
    
    private final NotificationService notificationService;

    public ContestEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @KafkaListener(topics = "${kafka.topics.contest-start:contest.start}", groupId = "notification-service")
    public void consumeContestStartEvent(ContestStartEvent event) {
        try {
            log.info("Received contest start event for contest: {}", event.getContestId());
            
            // Build metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("contestId", event.getContestId());
            metadata.put("contestTitle", event.getContestTitle());
            metadata.put("durationMinutes", event.getDurationMinutes().toString());
            metadata.put("organizationId", event.getOrganizationId());
            
            // Create message
            String title = "Contest Started";
            String message = String.format(
                    "The contest '%s' has started! You have %d minutes to complete it.",
                    event.getContestTitle(), event.getDurationMinutes()
            );
            String subject = "Contest Started - " + event.getContestTitle();
            
            // Broadcast to all users (could be filtered by organization/userIds in real implementation)
            notificationService.createAndSendNotification(
                    "system",
                    "system@jirathon.com",
                    "Jirathon System",
                    NotificationType.CONTEST_START,
                    title,
                    message,
                    subject,
                    event.getContestId(),
                    "CONTEST",
                    metadata,
                    false,  // sendEmail - broadcast first
                    true    // broadcastWebSocket
            );
            
            log.info("Contest start notification broadcast for contest: {}", event.getContestId());
        } catch (Exception e) {
            log.error("Error processing contest start event: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "${kafka.topics.contest-end:contest.end}", groupId = "notification-service")
    public void consumeContestEndEvent(ContestEndEvent event) {
        try {
            log.info("Received contest end event for contest: {}", event.getContestId());
            
            // Build metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("contestId", event.getContestId());
            metadata.put("contestTitle", event.getContestTitle());
            metadata.put("totalParticipants", event.getTotalParticipants().toString());
            metadata.put("organizationId", event.getOrganizationId());
            
            // Create message
            String title = "Contest Ended";
            String message = String.format(
                    "The contest '%s' has ended! %d participants completed the contest. Results will be announced soon.",
                    event.getContestTitle(), event.getTotalParticipants()
            );
            String subject = "Contest Ended - " + event.getContestTitle();
            
            // Broadcast to all users
            notificationService.createAndSendNotification(
                    "system",
                    "system@jirathon.com",
                    "Jirathon System",
                    NotificationType.CONTEST_END,
                    title,
                    message,
                    subject,
                    event.getContestId(),
                    "CONTEST",
                    metadata,
                    false,  // sendEmail - broadcast first
                    true    // broadcastWebSocket
            );
            
            log.info("Contest end notification broadcast for contest: {}", event.getContestId());
        } catch (Exception e) {
            log.error("Error processing contest end event: {}", e.getMessage(), e);
        }
    }
}
