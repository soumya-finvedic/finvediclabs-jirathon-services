package com.jirathon.auth.controller;

import com.jirathon.auth.dto.request.*;
import com.jirathon.auth.dto.response.ApiResponse;
import com.jirathon.auth.dto.response.AuthResponse;
import com.jirathon.auth.exception.AuthException;
import com.jirathon.auth.model.User;
import com.jirathon.auth.repository.UserRepository;
import com.jirathon.auth.security.UserPrincipal;
import com.jirathon.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        AuthResponse response = authService.register(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful. Please verify your email.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId,
            HttpServletRequest httpRequest
    ) {
        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);

        AuthResponse response = authService.login(request, tenantId, deviceInfo, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest
    ) {
        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);

        AuthResponse response = authService.refreshAccessToken(request.getRefreshToken(), deviceInfo, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        try {
            User user = authService.verifyEmail(tenantId, request.getEmail(), request.getOtp());
            
            // Verify user was actually updated in database
            if (!user.isEmailVerified() || user.getStatus() != com.jirathon.auth.model.enums.UserStatus.ACTIVE) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Email verification failed: database update did not persist"));
            }
            
            AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                    .id(user.getId())
                    .tenantId(user.getTenantId())
                    .email(user.getEmail())
                    .displayName(user.getDisplayName())
                    .avatarUrl(user.getAvatarUrl())
                    .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                    .emailVerified(user.isEmailVerified())
                    .status(user.getStatus().name())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully", userInfo));
        } catch (AuthException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Email verification failed: " + ex.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(
            @RequestParam String email,
            @RequestParam(defaultValue = "EMAIL_VERIFICATION") String purpose,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        authService.resendOtp(tenantId, email, purpose);
        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserPrincipal principal) {
        authService.logout(principal.getId(), principal.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User user = userRepository.findByIdAndTenantIdAndDeletedFalse(principal.getId(), principal.getTenantId())
                .orElseThrow(() -> new AuthException("User not found"));

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .tenantId(user.getTenantId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .organizationId(user.getOrganizationId())
                .status(user.getStatus() != null ? user.getStatus().name() : "UNKNOWN")
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
