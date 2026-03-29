package com.jirathon.auth.model;

import com.jirathon.auth.model.enums.OAuthProvider;
import com.jirathon.auth.model.enums.Role;
import com.jirathon.auth.model.enums.UserStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@CompoundIndexes({
    @CompoundIndex(name = "tenant_email_unique", def = "{'tenantId': 1, 'email': 1}", unique = true),
    @CompoundIndex(name = "tenant_oauth_lookup", def = "{'tenantId': 1, 'oauthProvider': 1, 'oauthProviderId': 1}"),
    @CompoundIndex(name = "tenant_roles", def = "{'tenantId': 1, 'roles': 1}"),
    @CompoundIndex(name = "tenant_status_created", def = "{'tenantId': 1, 'status': 1, 'createdAt': -1}")
})
public class User {

    @Id
    private String id;

    private String tenantId;

    private String email;

    private String passwordHash;

    private String displayName;

    private String username;

    private String phoneNumber;

    private String avatarUrl;

    private OAuthProvider oauthProvider;

    private String oauthProviderId;

    private Set<Role> roles = new HashSet<>(Set.of(Role.USER));

    private String organizationId;

    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    private boolean emailVerified = false;

    private Instant lastLoginAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private boolean deleted = false;

    private Instant deletedAt;

    public User() {
    }

    public User(String id, String tenantId, String email, String passwordHash, String displayName,
                String username, String phoneNumber, String avatarUrl,
                OAuthProvider oauthProvider, String oauthProviderId, Set<Role> roles, String organizationId,
                UserStatus status, boolean emailVerified, Instant lastLoginAt, Instant createdAt, Instant updatedAt,
                boolean deleted, Instant deletedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.oauthProvider = oauthProvider;
        this.oauthProviderId = oauthProviderId;
        this.roles = roles != null ? roles : new HashSet<>(Set.of(Role.USER));
        this.organizationId = organizationId;
        this.status = status != null ? status : UserStatus.PENDING_VERIFICATION;
        this.emailVerified = emailVerified;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String tenantId;
        private String email;
        private String passwordHash;
        private String displayName;
        private String username;
        private String phoneNumber;
        private String avatarUrl;
        private OAuthProvider oauthProvider;
        private String oauthProviderId;
        private Set<Role> roles = new HashSet<>(Set.of(Role.USER));
        private String organizationId;
        private UserStatus status = UserStatus.PENDING_VERIFICATION;
        private boolean emailVerified;
        private Instant lastLoginAt;
        private Instant createdAt;
        private Instant updatedAt;
        private boolean deleted;
        private Instant deletedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }
        public Builder oauthProvider(OAuthProvider oauthProvider) { this.oauthProvider = oauthProvider; return this; }
        public Builder oauthProviderId(String oauthProviderId) { this.oauthProviderId = oauthProviderId; return this; }
        public Builder roles(Set<Role> roles) { this.roles = roles; return this; }
        public Builder organizationId(String organizationId) { this.organizationId = organizationId; return this; }
        public Builder status(UserStatus status) { this.status = status; return this; }
        public Builder emailVerified(boolean emailVerified) { this.emailVerified = emailVerified; return this; }
        public Builder lastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder deletedAt(Instant deletedAt) { this.deletedAt = deletedAt; return this; }

        public User build() {
            return new User(id, tenantId, email, passwordHash, displayName, username, phoneNumber,
                    avatarUrl, oauthProvider, oauthProviderId, roles, organizationId, status,
                    emailVerified, lastLoginAt, createdAt, updatedAt, deleted, deletedAt);
        }
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public String getUsername() { return username; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAvatarUrl() { return avatarUrl; }
    public OAuthProvider getOauthProvider() { return oauthProvider; }
    public String getOauthProviderId() { return oauthProviderId; }
    public Set<Role> getRoles() { return roles; }
    public String getOrganizationId() { return organizationId; }
    public UserStatus getStatus() { return status; }
    public boolean isEmailVerified() { return emailVerified; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return deleted; }
    public Instant getDeletedAt() { return deletedAt; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setUsername(String username) { this.username = username; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setOauthProvider(OAuthProvider oauthProvider) { this.oauthProvider = oauthProvider; }
    public void setOauthProviderId(String oauthProviderId) { this.oauthProviderId = oauthProviderId; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
