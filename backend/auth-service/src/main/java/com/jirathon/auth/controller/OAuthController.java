package com.jirathon.auth.controller;

import com.jirathon.auth.dto.request.OAuthCallbackRequest;
import com.jirathon.auth.dto.response.ApiResponse;
import com.jirathon.auth.dto.response.AuthResponse;
import com.jirathon.auth.dto.response.OAuthUrlResponse;
import com.jirathon.auth.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/google/url")
    public ResponseEntity<ApiResponse<OAuthUrlResponse>> getGoogleAuthUrl(
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        OAuthUrlResponse response = OAuthUrlResponse.builder()
                .provider("GOOGLE")
                .authorizationUrl(oAuthService.getGoogleAuthUrl(tenantId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/azure/url")
    public ResponseEntity<ApiResponse<OAuthUrlResponse>> getAzureAuthUrl(
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        OAuthUrlResponse response = OAuthUrlResponse.builder()
                .provider("AZURE_AD")
                .authorizationUrl(oAuthService.getAzureAuthUrl(tenantId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/google/callback")
    public ResponseEntity<ApiResponse<AuthResponse>> googleCallback(
            @Valid @RequestBody OAuthCallbackRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId,
            HttpServletRequest httpRequest
    ) {
        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);

        AuthResponse response = oAuthService.handleGoogleCallback(
                request.getCode(), tenantId, deviceInfo, ipAddress
        );
        return ResponseEntity.ok(ApiResponse.success("Google login successful", response));
    }

    @PostMapping("/azure/callback")
    public ResponseEntity<ApiResponse<AuthResponse>> azureCallback(
            @Valid @RequestBody OAuthCallbackRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId,
            HttpServletRequest httpRequest
    ) {
        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);

        AuthResponse response = oAuthService.handleAzureCallback(
                request.getCode(), tenantId, deviceInfo, ipAddress
        );
        return ResponseEntity.ok(ApiResponse.success("Azure AD login successful", response));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
