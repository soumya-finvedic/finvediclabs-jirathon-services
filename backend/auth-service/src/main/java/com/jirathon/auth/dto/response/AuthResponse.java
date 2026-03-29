package com.jirathon.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private long expiresIn;
        private UserInfo user;

        public Builder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public Builder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder expiresIn(long expiresIn) { this.expiresIn = expiresIn; return this; }
        public Builder user(UserInfo user) { this.user = user; return this; }

        public AuthResponse build() {
            return new AuthResponse(accessToken, refreshToken, tokenType, expiresIn, user);
        }
    }

    public static class UserInfo {
        private String id;
        private String tenantId;
        private String email;
        private String phoneNumber;
        private String displayName;
        private String avatarUrl;
        private String organizationId;
        private String status;
        private Set<String> roles;
        private boolean emailVerified;
        private Instant createdAt;
        private Instant lastLoginAt;

        public UserInfo() {
        }

        public UserInfo(String id, String tenantId, String email, String phoneNumber, String displayName,
                        String avatarUrl, String organizationId, String status, Set<String> roles,
                        boolean emailVerified, Instant createdAt, Instant lastLoginAt) {
            this.id = id;
            this.tenantId = tenantId;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.displayName = displayName;
            this.avatarUrl = avatarUrl;
            this.organizationId = organizationId;
            this.status = status;
            this.roles = roles;
            this.emailVerified = emailVerified;
            this.createdAt = createdAt;
            this.lastLoginAt = lastLoginAt;
        }

        public static UserInfoBuilder builder() {
            return new UserInfoBuilder();
        }

        public static class UserInfoBuilder {
            private String id;
            private String tenantId;
            private String email;
            private String phoneNumber;
            private String displayName;
            private String avatarUrl;
            private String organizationId;
            private String status;
            private Set<String> roles;
            private boolean emailVerified;
            private Instant createdAt;
            private Instant lastLoginAt;

            public UserInfoBuilder id(String id) {
                this.id = id;
                return this;
            }

            public UserInfoBuilder tenantId(String tenantId) {
                this.tenantId = tenantId;
                return this;
            }

            public UserInfoBuilder email(String email) {
                this.email = email;
                return this;
            }

            public UserInfoBuilder phoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
                return this;
            }

            public UserInfoBuilder displayName(String displayName) {
                this.displayName = displayName;
                return this;
            }

            public UserInfoBuilder avatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
                return this;
            }

            public UserInfoBuilder organizationId(String organizationId) {
                this.organizationId = organizationId;
                return this;
            }

            public UserInfoBuilder status(String status) {
                this.status = status;
                return this;
            }

            public UserInfoBuilder roles(Set<String> roles) {
                this.roles = roles;
                return this;
            }

            public UserInfoBuilder emailVerified(boolean emailVerified) {
                this.emailVerified = emailVerified;
                return this;
            }

            public UserInfoBuilder createdAt(Instant createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public UserInfoBuilder lastLoginAt(Instant lastLoginAt) {
                this.lastLoginAt = lastLoginAt;
                return this;
            }

            public UserInfo build() {
                return new UserInfo(id, tenantId, email, phoneNumber, displayName, avatarUrl,
                        organizationId, status, roles, emailVerified, createdAt, lastLoginAt);
            }
        }

        public String getId() { return id; }
        public String getTenantId() { return tenantId; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getDisplayName() { return displayName; }
        public String getAvatarUrl() { return avatarUrl; }
        public String getOrganizationId() { return organizationId; }
        public String getStatus() { return status; }
        public Set<String> getRoles() { return roles; }
        public boolean isEmailVerified() { return emailVerified; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getLastLoginAt() { return lastLoginAt; }
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public UserInfo getUser() { return user; }
}
