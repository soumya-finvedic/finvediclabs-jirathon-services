package com.jirathon.notification.kafka;

import com.jirathon.notification.event.PaymentSuccessEvent;
import com.jirathon.notification.service.NotificationService;
import com.jirathon.notification.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    
    private final NotificationService notificationService;

    public PaymentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @KafkaListener(topics = "${kafka.topics.payment-success:payment.success}", groupId = "notification-service")
    public void consumePaymentSuccessEvent(PaymentSuccessEvent event) {
        try {
            log.info("Received payment success event for transaction: {}", event.getPaymentId());
            
            // Build metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("paymentId", event.getPaymentId());
            metadata.put("amount", event.getAmount().toString());
            metadata.put("currency", event.getCurrency());
            metadata.put("contestId", event.getContestId());
            metadata.put("contestTitle", event.getContestTitle());
            metadata.put("payuTxnId", event.getPayuTxnId());
            
            // Create email content
            String title = "Payment Confirmed";
            String message = String.format(
                    "Your payment of %s %s for contest '%s' has been confirmed. Transaction ID: %s",
                    event.getAmount(), event.getCurrency(), event.getContestTitle(), event.getPaymentId()
            );
            String subject = "Payment Confirmation - " + event.getContestTitle();
            
            // Send notification
            notificationService.createAndSendNotification(
                    event.getUserId(),
                    event.getEmail(),
                    event.getFullName(),
                    NotificationType.PAYMENT_SUCCESS,
                    title,
                    message,
                    subject,
                    event.getPaymentId(),
                    "PAYMENT",
                    metadata,
                    true,  // sendEmail
                    true   // broadcastWebSocket
            );
            
            log.info("Payment notification sent for transaction: {}", event.getPaymentId());
        } catch (Exception e) {
            log.error("Error processing payment success event: {}", e.getMessage(), e);
        }
    }
}
