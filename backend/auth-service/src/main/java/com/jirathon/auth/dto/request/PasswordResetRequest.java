package com.jirathon.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordResetRequest {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be 8-128 characters")
    private String newPassword;

    public String getToken() { return token; }
    public String getNewPassword() { return newPassword; }

    public void setToken(String token) { this.token = token; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
