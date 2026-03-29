package com.jirathon.auth.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private String id;
    private String tenantId;
    private String email;
    private String passwordHash;
    private Set<String> roles;
    private boolean active;

    public UserPrincipal() {
    }

    public UserPrincipal(String id, String tenantId, String email, String passwordHash, Set<String> roles, boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles != null ? roles : new LinkedHashSet<>();
        this.active = active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String tenantId;
        private String email;
        private String passwordHash;
        private Set<String> roles = new LinkedHashSet<>();
        private boolean active;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder roles(Set<String> roles) { this.roles = roles; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public UserPrincipal build() {
            return new UserPrincipal(id, tenantId, email, passwordHash, roles, active);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public static UserPrincipal fromUser(com.jirathon.auth.model.User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .tenantId(user.getTenantId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .active(user.getStatus() == com.jirathon.auth.model.enums.UserStatus.ACTIVE)
                .build();
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean isActive() {
        return active;
    }
}
