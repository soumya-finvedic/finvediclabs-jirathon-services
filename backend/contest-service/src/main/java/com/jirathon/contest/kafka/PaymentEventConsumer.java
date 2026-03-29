package com.jirathon.contest.kafka;

import com.jirathon.contest.event.PaymentSuccessEvent;
import com.jirathon.contest.model.ContestRegistration;
import com.jirathon.contest.model.enums.RegistrationStatus;
import com.jirathon.contest.repository.ContestRegistrationRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final ContestRegistrationRepository contestRegistrationRepository;

    public PaymentEventConsumer(ContestRegistrationRepository contestRegistrationRepository) {
        this.contestRegistrationRepository = contestRegistrationRepository;
    }

    @KafkaListener(
            topics = "${kafka.topics.payment-success:payment.success}",
            groupId = "${spring.kafka.consumer.group-id:contest-service}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentSuccess(
            ConsumerRecord<String, PaymentSuccessEvent> record,
            Acknowledgment acknowledgment
    ) {
        PaymentSuccessEvent event = record.value();

        if (event == null) {
            log.warn("Received null payload on payment.success topic — skipping. partition={} offset={}",
                    record.partition(), record.offset());
            acknowledgment.acknowledge();
            return;
        }

        log.info("Received payment.success event: registrationId={} transactionId={} partition={} offset={}",
                event.getRegistrationId(), event.getPaymentTransactionId(),
                record.partition(), record.offset());

        try {
            processPaymentSuccess(event);
            acknowledgment.acknowledge();
            log.info("Acknowledged payment.success event for registrationId={}", event.getRegistrationId());
        } catch (Exception ex) {
            log.error("Error processing payment.success event for registrationId={}: {}",
                    event.getRegistrationId(), ex.getMessage(), ex);
            // Do NOT acknowledge — Kafka will redeliver the message
            // In production you should configure a dead-letter topic via SeekToCurrentErrorHandler
        }
    }

    private void processPaymentSuccess(PaymentSuccessEvent event) {
        Optional<ContestRegistration> optReg =
                contestRegistrationRepository.findById(event.getRegistrationId());

        if (optReg.isEmpty()) {
            log.warn("Registration not found for registrationId={} — payment.success event will be acknowledged without action",
                    event.getRegistrationId());
            return;
        }

        ContestRegistration registration = optReg.get();

        // Idempotency guard — already confirmed, nothing to do
        if (registration.getStatus() == RegistrationStatus.CONFIRMED) {
            log.info("Registration {} is already CONFIRMED — skipping duplicate event", event.getRegistrationId());
            return;
        }

        registration.setPaymentTransactionId(event.getPaymentTransactionId());
        registration.setPaymentStatus("SUCCESS");
        registration.setStatus(RegistrationStatus.CONFIRMED);
        registration.setConfirmedAt(Instant.now());

        contestRegistrationRepository.save(registration);

        log.info("Registration {} confirmed automatically via Kafka payment.success event. userId={} contestId={}",
                registration.getId(), registration.getUserId(), registration.getContestId());
    }
}
