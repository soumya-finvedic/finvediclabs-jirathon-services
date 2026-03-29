package com.jirathon.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.jirathon.org.model.enums.OrgRole;

public class UpdateMemberRoleRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Role is required")
    private OrgRole role;

    public String getUserId()  { return userId; }
    public OrgRole getRole()   { return role; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setRole(OrgRole role)    { this.role = role; }
}
