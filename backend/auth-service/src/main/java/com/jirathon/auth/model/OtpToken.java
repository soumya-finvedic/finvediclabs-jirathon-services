package com.jirathon.auth.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "otp_tokens")
@CompoundIndex(name = "tenant_email_purpose", def = "{'tenantId': 1, 'email': 1, 'purpose': 1}")
public class OtpToken {

    @Id
    private String id;

    private String tenantId;

    private String email;

    private String otpHash;

    private String purpose; // EMAIL_VERIFICATION, PASSWORD_RESET, LOGIN

    private int attemptCount;

    private static final int MAX_ATTEMPTS = 5;

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    private boolean used;

    @CreatedDate
    private Instant createdAt;

    public OtpToken() {
    }

    public OtpToken(String id, String tenantId, String email, String otpHash, String purpose,
                    int attemptCount, Instant expiresAt, boolean used, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.otpHash = otpHash;
        this.purpose = purpose;
        this.attemptCount = attemptCount;
        this.expiresAt = expiresAt;
        this.used = used;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String tenantId;
        private String email;
        private String otpHash;
        private String purpose;
        private int attemptCount;
        private Instant expiresAt;
        private boolean used;
        private Instant createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder otpHash(String otpHash) { this.otpHash = otpHash; return this; }
        public Builder purpose(String purpose) { this.purpose = purpose; return this; }
        public Builder attemptCount(int attemptCount) { this.attemptCount = attemptCount; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder used(boolean used) { this.used = used; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public OtpToken build() {
            return new OtpToken(id, tenantId, email, otpHash, purpose, attemptCount, expiresAt, used, createdAt);
        }
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getEmail() { return email; }
    public String getOtpHash() { return otpHash; }
    public String getPurpose() { return purpose; }
    public int getAttemptCount() { return attemptCount; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public Instant getCreatedAt() { return createdAt; }

    public void setUsed(boolean used) { this.used = used; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean hasExceededAttempts() {
        return attemptCount >= MAX_ATTEMPTS;
    }
}
