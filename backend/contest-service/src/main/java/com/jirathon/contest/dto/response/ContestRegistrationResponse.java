package com.jirathon.contest.dto.response;

import com.jirathon.contest.model.enums.RegistrationStatus;

import java.time.Instant;

public class ContestRegistrationResponse {

    private String id;
    private String contestId;
    private String userId;
    private String userEmail;
    private String userDisplayName;
    private String teamName;
    private String contactNumber;
    private String couponCode;
    private double amount;
    private RegistrationStatus status;
    private String paymentTransactionId;
    private String paymentStatus;
    private Instant confirmedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public ContestRegistrationResponse() {
    }

    public ContestRegistrationResponse(
            String id,
            String contestId,
            String userId,
            String userEmail,
            String userDisplayName,
            String teamName,
            String contactNumber,
            String couponCode,
            double amount,
            RegistrationStatus status,
            String paymentTransactionId,
            String paymentStatus,
            Instant confirmedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.contestId = contestId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userDisplayName = userDisplayName;
        this.teamName = teamName;
        this.contactNumber = contactNumber;
        this.couponCode = couponCode;
        this.amount = amount;
        this.status = status;
        this.paymentTransactionId = paymentTransactionId;
        this.paymentStatus = paymentStatus;
        this.confirmedAt = confirmedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String contestId;
        private String userId;
        private String userEmail;
        private String userDisplayName;
        private String teamName;
        private String contactNumber;
        private String couponCode;
        private double amount;
        private RegistrationStatus status;
        private String paymentTransactionId;
        private String paymentStatus;
        private Instant confirmedAt;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder contestId(String contestId) { this.contestId = contestId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder userDisplayName(String userDisplayName) { this.userDisplayName = userDisplayName; return this; }
        public Builder teamName(String teamName) { this.teamName = teamName; return this; }
        public Builder contactNumber(String contactNumber) { this.contactNumber = contactNumber; return this; }
        public Builder couponCode(String couponCode) { this.couponCode = couponCode; return this; }
        public Builder amount(double amount) { this.amount = amount; return this; }
        public Builder status(RegistrationStatus status) { this.status = status; return this; }
        public Builder paymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; return this; }
        public Builder paymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public Builder confirmedAt(Instant confirmedAt) { this.confirmedAt = confirmedAt; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public ContestRegistrationResponse build() {
            return new ContestRegistrationResponse(id, contestId, userId, userEmail, userDisplayName, teamName,
                    contactNumber, couponCode, amount, status, paymentTransactionId, paymentStatus,
                    confirmedAt, createdAt, updatedAt);
        }
    }

    public String getId() {
        return id;
    }

    public String getContestId() {
        return contestId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public double getAmount() {
        return amount;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}