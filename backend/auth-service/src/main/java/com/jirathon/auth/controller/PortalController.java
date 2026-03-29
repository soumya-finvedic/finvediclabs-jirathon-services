package com.jirathon.auth.controller;

import com.jirathon.auth.dto.response.ApiResponse;
import com.jirathon.auth.security.TenantContext;
import com.jirathon.auth.service.TenantPortalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/portals")
public class PortalController {

    private final TenantPortalService tenantPortalService;

    public PortalController(TenantPortalService tenantPortalService) {
        this.tenantPortalService = tenantPortalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPortals() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("defaultPortal", TenantPortalService.DEFAULT_TENANT);
        data.put("activePortal", TenantContext.getTenantId());
        data.put("portals", tenantPortalService.getAvailablePortals());
        return ResponseEntity.ok(ApiResponse.success("Available login portals retrieved", data));
    }
}