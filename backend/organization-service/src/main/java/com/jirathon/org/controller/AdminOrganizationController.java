package com.jirathon.org.controller;

import com.jirathon.org.dto.ApiResponse;
import com.jirathon.org.dto.OrgResponse;
import com.jirathon.org.dto.PagedResponse;
import com.jirathon.org.model.enums.OrgStatus;
import com.jirathon.org.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/organizations")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminOrganizationController {

    private final OrganizationService organizationService;

    public AdminOrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrgResponse>>> listOrganizations(
            @RequestParam(required = false) OrgStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                organizationService.adminListOrganizations(status, page, size)));
    }

    @PutMapping("/{orgId}/status")
    public ResponseEntity<ApiResponse<OrgResponse>> updateStatus(
            @PathVariable String orgId,
            @RequestParam OrgStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                organizationService.adminUpdateStatus(orgId, status)));
    }
}
