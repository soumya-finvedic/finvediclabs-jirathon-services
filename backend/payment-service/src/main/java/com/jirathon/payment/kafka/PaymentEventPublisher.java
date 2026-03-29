package com.jirathon.payment.kafka;

import com.jirathon.payment.event.PaymentSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate;

    @Value("${kafka.topics.payment-success:payment.success}")
    private String paymentSuccessTopic;

    public PaymentEventPublisher(KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        log.info("Publishing payment.success event for registrationId={} transactionId={}",
                event.getRegistrationId(), event.getPaymentTransactionId());

        CompletableFuture<SendResult<String, PaymentSuccessEvent>> future =
                kafkaTemplate.send(paymentSuccessTopic, event.getRegistrationId(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish payment.success event for registrationId={}: {}",
                        event.getRegistrationId(), ex.getMessage(), ex);
            } else {
                log.info("payment.success event published successfully for registrationId={} partition={} offset={}",
                        event.getRegistrationId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
