package com.jirathon.notification.kafka;

import com.jirathon.notification.event.RegistrationConfirmedEvent;
import com.jirathon.notification.service.NotificationService;
import com.jirathon.notification.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(RegistrationEventConsumer.class);
    
    private final NotificationService notificationService;

    public RegistrationEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @KafkaListener(topics = "${kafka.topics.registration-confirmed:registration.confirmed}", groupId = "notification-service")
    public void consumeRegistrationConfirmedEvent(RegistrationConfirmedEvent event) {
        try {
            log.info("Received registration confirmed event for registration: {}", event.getRegistrationId());
            
            // Build metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("registrationId", event.getRegistrationId());
            metadata.put("contestId", event.getContestId());
            metadata.put("contestTitle", event.getContestTitle());
            metadata.put("registrationDate", event.getRegistrationDate().toString());
            metadata.put("contestStartDate", event.getContestStartDate().toString());
            
            // Create email content
            String title = "Registration Confirmed";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            String message = String.format(
                    "Your registration for contest '%s' has been confirmed. The contest will start on %s.",
                    event.getContestTitle(),
                    event.getContestStartDate().atZone(java.time.ZoneId.systemDefault()).format(formatter)
            );
            String subject = "Registration Confirmed - " + event.getContestTitle();
            
            // Send notification
            notificationService.createAndSendNotification(
                    event.getUserId(),
                    event.getEmail(),
                    event.getFullName(),
                    NotificationType.REGISTRATION_CONFIRMED,
                    title,
                    message,
                    subject,
                    event.getRegistrationId(),
                    "REGISTRATION",
                    metadata,
                    true,  // sendEmail
                    true   // broadcastWebSocket
            );
            
            log.info("Registration notification sent for registration: {}", event.getRegistrationId());
        } catch (Exception e) {
            log.error("Error processing registration confirmed event: {}", e.getMessage(), e);
        }
    }
}
