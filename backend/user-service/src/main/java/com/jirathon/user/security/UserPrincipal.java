package com.jirathon.user.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private String id;
    private String tenantId;
    private String email;
    private Set<String> roles;

    public UserPrincipal() {}

    public UserPrincipal(String id, String tenantId, String email, Set<String> roles) {
        this.id = id; this.tenantId = tenantId; this.email = email; this.roles = roles;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, tenantId, email;
        private Set<String> roles;
        public Builder id(String v)         { this.id = v; return this; }
        public Builder tenantId(String v)   { this.tenantId = v; return this; }
        public Builder email(String v)      { this.email = v; return this; }
        public Builder roles(Set<String> v) { this.roles = v; return this; }
        public UserPrincipal build() { return new UserPrincipal(id, tenantId, email, roles); }
    }

    public String getId()         { return id; }
    public String getTenantId()   { return tenantId; }
    public String getEmail()      { return email; }
    public Set<String> getRoles() { return roles; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    @Override public String getPassword()              { return null; }
    @Override public String getUsername()              { return email; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
