package com.jirathon.payment.model;

import com.jirathon.payment.model.enums.PaymentStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payment_transactions")
@CompoundIndexes({
        @CompoundIndex(name = "registration_unique", def = "{'registrationId': 1}", unique = true),
        @CompoundIndex(name = "payu_txn_unique", def = "{'payuTxnId': 1}", unique = true),
        @CompoundIndex(name = "user_status_created", def = "{'userId': 1, 'status': 1, 'createdAt': -1}")
})
public class PaymentTransaction {

    @Id
    private String id;
    private String registrationId;
    private String contestId;
    private String userId;

    private double originalAmount;
    private double discountAmount;
    private double payableAmount;
    private String couponCode;
    private boolean couponCounted;

    private String payuTxnId;
    private String payuPaymentId;
    private String payuStatus;

    private PaymentStatus status;
    private String failureReason;

    private Refund refund;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public static class Refund {
        private String refundId;
        private double amount;
        private String status;
        private String reason;
        private Instant refundedAt;

        public String getRefundId() {
            return refundId;
        }

        public void setRefundId(String refundId) {
            this.refundId = refundId;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Instant getRefundedAt() {
            return refundedAt;
        }

        public void setRefundedAt(Instant refundedAt) {
            this.refundedAt = refundedAt;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public boolean isCouponCounted() {
        return couponCounted;
    }

    public void setCouponCounted(boolean couponCounted) {
        this.couponCounted = couponCounted;
    }

    public String getPayuTxnId() {
        return payuTxnId;
    }

    public void setPayuTxnId(String payuTxnId) {
        this.payuTxnId = payuTxnId;
    }

    public String getPayuPaymentId() {
        return payuPaymentId;
    }

    public void setPayuPaymentId(String payuPaymentId) {
        this.payuPaymentId = payuPaymentId;
    }

    public String getPayuStatus() {
        return payuStatus;
    }

    public void setPayuStatus(String payuStatus) {
        this.payuStatus = payuStatus;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
