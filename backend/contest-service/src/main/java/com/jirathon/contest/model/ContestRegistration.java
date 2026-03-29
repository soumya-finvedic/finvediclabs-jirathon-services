package com.jirathon.contest.model;

import com.jirathon.contest.model.enums.RegistrationStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "contest_registrations")
@CompoundIndexes({
        @CompoundIndex(name = "tenant_contest_user_unique", def = "{'tenantId': 1, 'contestId': 1, 'userId': 1}", unique = true),
        @CompoundIndex(name = "tenant_user_created", def = "{'tenantId': 1, 'userId': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "tenant_status_created", def = "{'tenantId': 1, 'status': 1, 'createdAt': -1}")
})
public class ContestRegistration {

    @Id
    private String id;

    private String tenantId;
    private String contestId;
    private String userId;
    private String userEmail;
    private String userDisplayName;
    private String teamName;
    private String contactNumber;
    private String couponCode;
    private double amount;

    private RegistrationStatus status = RegistrationStatus.PENDING_PAYMENT;

    private String paymentTransactionId;
    private String paymentStatus;
    private Instant confirmedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public ContestRegistration() {
    }

    public ContestRegistration(String id, String tenantId, String contestId, String userId, String userEmail,
                               String userDisplayName, String teamName, String contactNumber, String couponCode,
                               double amount, RegistrationStatus status, String paymentTransactionId,
                               String paymentStatus, Instant confirmedAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.contestId = contestId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userDisplayName = userDisplayName;
        this.teamName = teamName;
        this.contactNumber = contactNumber;
        this.couponCode = couponCode;
        this.amount = amount;
        this.status = status != null ? status : RegistrationStatus.PENDING_PAYMENT;
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
        private String tenantId;
        private String contestId;
        private String userId;
        private String userEmail;
        private String userDisplayName;
        private String teamName;
        private String contactNumber;
        private String couponCode;
        private double amount;
        private RegistrationStatus status = RegistrationStatus.PENDING_PAYMENT;
        private String paymentTransactionId;
        private String paymentStatus;
        private Instant confirmedAt;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
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

        public ContestRegistration build() {
            return new ContestRegistration(id, tenantId, contestId, userId, userEmail, userDisplayName, teamName,
                    contactNumber, couponCode, amount, status, paymentTransactionId, paymentStatus,
                    confirmedAt, createdAt, updatedAt);
        }
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getContestId() { return contestId; }
    public String getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public String getUserDisplayName() { return userDisplayName; }
    public String getTeamName() { return teamName; }
    public String getContactNumber() { return contactNumber; }
    public String getCouponCode() { return couponCode; }
    public double getAmount() { return amount; }
    public RegistrationStatus getStatus() { return status; }
    public String getPaymentTransactionId() { return paymentTransactionId; }
    public String getPaymentStatus() { return paymentStatus; }
    public Instant getConfirmedAt() { return confirmedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setStatus(RegistrationStatus status) { this.status = status; }
    public void setConfirmedAt(Instant confirmedAt) { this.confirmedAt = confirmedAt; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
}