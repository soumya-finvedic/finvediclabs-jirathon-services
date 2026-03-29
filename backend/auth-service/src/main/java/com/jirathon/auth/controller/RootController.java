package com.jirathon.auth.controller;

import com.jirathon.auth.dto.response.ApiResponse;
import com.jirathon.auth.security.TenantContext;
import com.jirathon.auth.service.TenantPortalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class RootController {

    private final TenantPortalService tenantPortalService;

    public RootController(TenantPortalService tenantPortalService) {
        this.tenantPortalService = tenantPortalService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, Object>>> root() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("service", "auth-service");
        data.put("status", "up");
        data.put("health", "/actuator/health");
        data.put("authBase", "/api/v1/auth");
        data.put("activePortal", TenantContext.getTenantId());
        data.put("availablePortals", tenantPortalService.getAvailablePortals());

        return ResponseEntity.ok(ApiResponse.success("Jirathon Auth Service is running", data));
    }
}
