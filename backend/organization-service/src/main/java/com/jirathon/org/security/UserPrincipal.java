package com.jirathon.org.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private String id;
    private String email;
    private String tenantId;
    private List<String> roles;

    public UserPrincipal() {}

    public UserPrincipal(String id, String email, String tenantId, List<String> roles) {
        this.id = id; this.email = email; this.tenantId = tenantId; this.roles = roles;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, email, tenantId;
        private List<String> roles;
        public Builder id(String v)        { this.id = v; return this; }
        public Builder email(String v)     { this.email = v; return this; }
        public Builder tenantId(String v)  { this.tenantId = v; return this; }
        public Builder roles(List<String> v){ this.roles = v; return this; }
        public UserPrincipal build() { return new UserPrincipal(id, email, tenantId, roles); }
    }

    public String getId()        { return id; }
    public String getEmail()     { return email; }
    public String getTenantId()  { return tenantId; }
    public List<String> getRoles(){ return roles; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override public String getPassword()              { return null; }
    @Override public String getUsername()              { return email; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
