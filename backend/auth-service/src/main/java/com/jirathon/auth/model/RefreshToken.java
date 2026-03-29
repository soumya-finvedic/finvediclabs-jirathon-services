package com.jirathon.auth.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh_tokens")
@CompoundIndex(name = "tenant_user_token", def = "{'tenantId': 1, 'userId': 1}")
public class RefreshToken {

    @Id
    private String id;

    private String tenantId;

    private String userId;

    @Indexed(unique = true)
    private String tokenHash;

    private String deviceInfo;

    private String ipAddress;

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    private boolean revoked;

    @CreatedDate
    private Instant createdAt;

    public RefreshToken() {
    }

    public RefreshToken(String id, String tenantId, String userId, String tokenHash,
                        String deviceInfo, String ipAddress, Instant expiresAt, boolean revoked, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String tenantId;
        private String userId;
        private String tokenHash;
        private String deviceInfo;
        private String ipAddress;
        private Instant expiresAt;
        private boolean revoked;
        private Instant createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder tokenHash(String tokenHash) { this.tokenHash = tokenHash; return this; }
        public Builder deviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder revoked(boolean revoked) { this.revoked = revoked; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public RefreshToken build() {
            return new RefreshToken(id, tenantId, userId, tokenHash, deviceInfo, ipAddress, expiresAt, revoked, createdAt);
        }
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public String getDeviceInfo() { return deviceInfo; }
    public String getIpAddress() { return ipAddress; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public Instant getCreatedAt() { return createdAt; }

    public void setRevoked(boolean revoked) { this.revoked = revoked; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
