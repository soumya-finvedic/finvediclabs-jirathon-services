package com.jirathon.user.dto.request;

import com.jirathon.user.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class UpdateRolesRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Roles are required")
    private Set<Role> roles;

    public String getUserId()     { return userId; }
    public Set<Role> getRoles()   { return roles; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setRoles(Set<Role> roles){ this.roles = roles; }
}
