package com.jirathon.auth.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "password_reset_tokens")
@CompoundIndex(name = "tenant_user", def = "{'tenantId': 1, 'userId': 1}")
public class PasswordResetToken {

    @Id
    private String id;

    private String tenantId;

    private String userId;

    @Indexed(unique = true)
    private String token;  // Raw token for lookup (temporary token, so not hashed)

    private String tokenHash;  // Optional: BCrypt hash for extra security

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    private boolean used;

    @CreatedDate
    private Instant createdAt;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String id, String tenantId, String userId, String token, String tokenHash,
                              Instant expiresAt, boolean used, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.userId = userId;
        this.token = token;
        this.tokenHash = tokenHash;
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
        private String userId;
        private String token;
        private String tokenHash;
        private Instant expiresAt;
        private boolean used;
        private Instant createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder tokenHash(String tokenHash) { this.tokenHash = tokenHash; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder used(boolean used) { this.used = used; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public PasswordResetToken build() {
            return new PasswordResetToken(id, tenantId, userId, token, tokenHash, expiresAt, used, createdAt);
        }
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public String getToken() { return token; }
    public String getTokenHash() { return tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public Instant getCreatedAt() { return createdAt; }

    public void setUsed(boolean used) { this.used = used; }
    public void setToken(String token) { this.token = token; }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
