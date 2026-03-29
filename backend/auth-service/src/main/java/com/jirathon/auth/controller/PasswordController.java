package com.jirathon.auth.controller;

import com.jirathon.auth.dto.request.ForgotPasswordRequest;
import com.jirathon.auth.dto.request.PasswordResetRequest;
import com.jirathon.auth.dto.response.ApiResponse;
import com.jirathon.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class PasswordController {

    private final PasswordResetService passwordResetService;

    public PasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        passwordResetService.initiatePasswordReset(tenantId, request.getEmail());
        // Always return success to prevent email enumeration
        return ResponseEntity.ok(ApiResponse.success(
                "If an account exists with this email, a password reset link has been sent"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        passwordResetService.resetPassword(tenantId, request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successful. Please login with your new password."));
    }
}
