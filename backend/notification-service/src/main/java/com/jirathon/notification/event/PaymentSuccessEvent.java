package com.jirathon.notification.event;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentSuccessEvent {
    private String paymentId;
    private String userId;
    private String email;
    private String fullName;
    private String registrationId;
    private String contestId;
    private String contestTitle;
    private BigDecimal amount;
    private String currency;
    private String payuTxnId;
    private String payuPaymentId;
    private Instant timestamp;

    public PaymentSuccessEvent() {
    }

    public PaymentSuccessEvent(String paymentId, String userId, String email, String fullName,
                               String registrationId, String contestId, String contestTitle,
                               BigDecimal amount, String currency, String payuTxnId,
                               String payuPaymentId, Instant timestamp) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.registrationId = registrationId;
        this.contestId = contestId;
        this.contestTitle = contestTitle;
        this.amount = amount;
        this.currency = currency;
        this.payuTxnId = payuTxnId;
        this.payuPaymentId = payuPaymentId;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getContestTitle() {
        return contestTitle;
    }

    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static final class Builder {
        private String paymentId;
        private String userId;
        private String email;
        private String fullName;
        private String registrationId;
        private String contestId;
        private String contestTitle;
        private BigDecimal amount;
        private String currency;
        private String payuTxnId;
        private String payuPaymentId;
        private Instant timestamp;

        private Builder() {
        }

        public Builder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder registrationId(String registrationId) {
            this.registrationId = registrationId;
            return this;
        }

        public Builder contestId(String contestId) {
            this.contestId = contestId;
            return this;
        }

        public Builder contestTitle(String contestTitle) {
            this.contestTitle = contestTitle;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder payuTxnId(String payuTxnId) {
            this.payuTxnId = payuTxnId;
            return this;
        }

        public Builder payuPaymentId(String payuPaymentId) {
            this.payuPaymentId = payuPaymentId;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public PaymentSuccessEvent build() {
            return new PaymentSuccessEvent(paymentId, userId, email, fullName, registrationId,
                    contestId, contestTitle, amount, currency, payuTxnId, payuPaymentId, timestamp);
        }
    }
}
