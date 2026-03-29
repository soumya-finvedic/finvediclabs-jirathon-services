package com.jirathon.payment.service;

import com.jirathon.payment.dto.request.CreatePaymentRequest;
import com.jirathon.payment.dto.request.RefundRequest;
import com.jirathon.payment.dto.response.PaymentInitiationResponse;
import com.jirathon.payment.dto.response.PaymentResponse;
import com.jirathon.payment.event.PaymentSuccessEvent;
import com.jirathon.payment.kafka.PaymentEventPublisher;
import com.jirathon.payment.model.PaymentTransaction;
import com.jirathon.payment.model.enums.PaymentStatus;
import com.jirathon.payment.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentTransactionRepository paymentRepository;
    private final PayUClient payUClient;
    private final PaymentEventPublisher eventPublisher;

    public PaymentService(
            PaymentTransactionRepository paymentRepository,
            PayUClient payUClient,
            PaymentEventPublisher eventPublisher
    ) {
        this.paymentRepository = paymentRepository;
        this.payUClient = payUClient;
        this.eventPublisher = eventPublisher;
    }

    public PaymentInitiationResponse initiatePayment(CreatePaymentRequest request) {
        paymentRepository.findByRegistrationId(request.getRegistrationId()).ifPresent(existing -> {
            throw new IllegalArgumentException("Payment already exists for registration: " + request.getRegistrationId());
        });

        double originalAmount = request.getAmount();
        double discountAmount = 0.0;
        double payableAmount = originalAmount;

        // COUPON FUNCTIONALITY TEMPORARILY DISABLED
        // Coupon code in the request will be ignored until re-enabled

        String payuTxnId = "JIRA" + UUID.randomUUID().toString().replace("-", "").substring(0, 18);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setRegistrationId(request.getRegistrationId());
        transaction.setContestId(request.getContestId());
        transaction.setUserId(request.getUserId());
        transaction.setOriginalAmount(originalAmount);
        transaction.setDiscountAmount(discountAmount);
        transaction.setPayableAmount(payableAmount);
        transaction.setCouponCode(null);
        transaction.setCouponCounted(false);
        transaction.setPayuTxnId(payuTxnId);
        transaction.setStatus(PaymentStatus.PENDING);

        transaction = paymentRepository.save(transaction);

        Map<String, String> formData = payUClient.buildPaymentFormData(
                payuTxnId,
                String.format("%.2f", payableAmount),
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                "contest_registration",
                request.getRegistrationId(),
                request.getContestId()
        );

        PaymentInitiationResponse response = new PaymentInitiationResponse();
        response.setTransactionId(transaction.getId());
        response.setPayuTxnId(payuTxnId);
        response.setOriginalAmount(originalAmount);
        response.setDiscountAmount(discountAmount);
        response.setPayableAmount(payableAmount);
        response.setPaymentUrl(payUClient.getPaymentUrl());
        response.setPayuFormData(formData);
        return response;
    }

    public PaymentResponse getByTransactionId(String transactionId) {
        PaymentTransaction tx = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + transactionId));
        return toResponse(tx);
    }

    public PaymentResponse getByRegistrationId(String registrationId) {
        PaymentTransaction tx = paymentRepository.findByRegistrationId(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for registration: " + registrationId));
        return toResponse(tx);
    }

    public List<PaymentResponse> listByUser(String userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaymentResponse refund(String transactionId, RefundRequest request) {
        PaymentTransaction tx = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + transactionId));

        if (tx.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalArgumentException("Only successful payments can be refunded");
        }

        if (request.getAmount() > tx.getPayableAmount()) {
            throw new IllegalArgumentException("Refund amount exceeds paid amount");
        }

        tx.setStatus(PaymentStatus.REFUNDED);
        PaymentTransaction.Refund refund = new PaymentTransaction.Refund();
        refund.setRefundId("RFND-" + UUID.randomUUID().toString().substring(0, 8));
        refund.setAmount(request.getAmount());
        refund.setStatus("SUCCESS");
        refund.setReason(request.getReason());
        refund.setRefundedAt(Instant.now());
        tx.setRefund(refund);

        return toResponse(paymentRepository.save(tx));
    }

    public PaymentResponse simulateSuccess(String transactionId) {
        PaymentTransaction tx = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + transactionId));

        if (tx.getStatus() == PaymentStatus.SUCCESS) {
            return toResponse(tx);
        }

        tx.setStatus(PaymentStatus.SUCCESS);
        tx.setPayuStatus("success");
        tx.setPayuPaymentId("MOCK-PAY-" + UUID.randomUUID().toString().substring(0, 8));
        tx = paymentRepository.save(tx);

        // Publish Kafka event (same as real webhook flow)
        publishSuccessEvent(tx);

        return toResponse(tx);
    }

    public void processWebhook(Map<String, String> payload, String webhookSecret) {
//        if (!payUClient.validateWebhookSignature(webhookSecret)) {
//            throw new IllegalArgumentException("Invalid webhook secret");
//        }

        String txnId = payload.get("txnid");
        if (txnId == null || txnId.isBlank()) {
            throw new IllegalArgumentException("Missing txnid in webhook payload");
        }

        PaymentTransaction tx = paymentRepository.findByPayuTxnId(txnId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for txnid: " + txnId));

        // ── Idempotency guard ─────────────────────────────────────────────────
        // PayU may call the webhook multiple times for the same transaction.
        // If we already marked it SUCCESS, skip processing to avoid duplicate
        // Kafka events and double-confirming the contest registration.
        if (PaymentStatus.SUCCESS.equals(tx.getStatus())) {
            log.info("Transaction {} already SUCCESS — ignoring duplicate webhook for txnid={}",
                    tx.getId(), txnId);
            return;
        }

        String status = payload.getOrDefault("status", "").toLowerCase();
        tx.setPayuStatus(status);
        tx.setPayuPaymentId(payload.get("mihpayid"));

        if ("success".equals(status)) {
            tx.setStatus(PaymentStatus.SUCCESS);
            // COUPON FUNCTIONALITY TEMPORARILY DISABLED: applyCouponUsageIfNeeded(tx);
        } else if ("failure".equals(status) || "failed".equals(status)) {
            tx.setStatus(PaymentStatus.FAILED);
            tx.setFailureReason(payload.getOrDefault("error_Message", "Payment failed"));
        }

        tx = paymentRepository.save(tx);

        // Publish Kafka event when payment succeeds via webhook
        if (PaymentStatus.SUCCESS.equals(tx.getStatus())) {
            publishSuccessEvent(tx);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void publishSuccessEvent(PaymentTransaction tx) {
        try {
            PaymentSuccessEvent event = new PaymentSuccessEvent(
                    tx.getId(),
                    tx.getRegistrationId(),
                    tx.getContestId(),
                    tx.getUserId(),
                    tx.getPayableAmount(),
                    tx.getPayuTxnId(),
                    tx.getPayuPaymentId()
            );
            eventPublisher.publishPaymentSuccess(event);
        } catch (Exception ex) {
            // Never let Kafka failure break the payment flow — log and continue
            log.error("Failed to publish payment.success event for transactionId={}: {}",
                    tx.getId(), ex.getMessage(), ex);
        }
    }

    private PaymentResponse toResponse(PaymentTransaction tx) {
        PaymentResponse response = new PaymentResponse();
        response.setId(tx.getId());
        response.setRegistrationId(tx.getRegistrationId());
        response.setContestId(tx.getContestId());
        response.setUserId(tx.getUserId());
        response.setOriginalAmount(tx.getOriginalAmount());
        response.setDiscountAmount(tx.getDiscountAmount());
        response.setPayableAmount(tx.getPayableAmount());
        response.setCouponCode(tx.getCouponCode());
        response.setStatus(tx.getStatus());
        response.setPayuTxnId(tx.getPayuTxnId());
        response.setPayuPaymentId(tx.getPayuPaymentId());
        response.setPayuStatus(tx.getPayuStatus());
        response.setFailureReason(tx.getFailureReason());
        response.setCreatedAt(tx.getCreatedAt());
        response.setUpdatedAt(tx.getUpdatedAt());
        return response;
    }
}
