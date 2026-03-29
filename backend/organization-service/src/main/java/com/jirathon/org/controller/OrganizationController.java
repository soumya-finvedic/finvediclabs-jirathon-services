package com.jirathon.org.controller;

import com.jirathon.org.dto.*;
import com.jirathon.org.model.enums.MembershipStatus;
import com.jirathon.org.model.enums.OrgRole;
import com.jirathon.org.security.UserPrincipal;
import com.jirathon.org.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrgResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        OrgResponse response = organizationService.createOrganization(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Organization created successfully", response));
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrgResponse>> getOrganization(@PathVariable String orgId) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.getOrganization(orgId)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<OrgResponse>> getOrganizationBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.getOrganizationBySlug(slug)));
    }

    @PutMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrgResponse>> updateOrganization(
            @PathVariable String orgId,
            @Valid @RequestBody UpdateOrganizationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Organization updated",
                organizationService.updateOrganization(orgId, request, principal)));
    }

    @DeleteMapping("/{orgId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(
            @PathVariable String orgId,
            @AuthenticationPrincipal UserPrincipal principal) {
        organizationService.deleteOrganization(orgId, principal);
        return ResponseEntity.ok(ApiResponse.success("Organization deleted", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrgResponse>>> listOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.listOrganizations(page, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<OrgResponse>>> searchOrganizations(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.searchOrganizations(query, page, size)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrgResponse>>> getMyOrganizations(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.getMyOrganizations(principal)));
    }

    // ── Membership Endpoints ───────────────────────────────────────

    @PostMapping("/{orgId}/join")
    public ResponseEntity<ApiResponse<MemberResponse>> joinOrganization(
            @PathVariable String orgId,
            @AuthenticationPrincipal UserPrincipal principal) {
        MemberResponse response = organizationService.joinOrganization(orgId, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Join request processed", response));
    }

    @PostMapping("/{orgId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveOrganization(
            @PathVariable String orgId,
            @AuthenticationPrincipal UserPrincipal principal) {
        organizationService.leaveOrganization(orgId, principal);
        return ResponseEntity.ok(ApiResponse.success("Left organization", null));
    }

    @GetMapping("/{orgId}/members")
    public ResponseEntity<ApiResponse<PagedResponse<MemberResponse>>> listMembers(
            @PathVariable String orgId,
            @RequestParam(required = false) MembershipStatus status,
            @RequestParam(required = false) OrgRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                organizationService.listMembers(orgId, status, role, page, size)));
    }

    @PostMapping("/{orgId}/members/{userId}/approve")
    public ResponseEntity<ApiResponse<MemberResponse>> approveMember(
            @PathVariable String orgId,
            @PathVariable String userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Member approved",
                organizationService.approveMember(orgId, userId, principal)));
    }

    @DeleteMapping("/{orgId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String orgId,
            @PathVariable String userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        organizationService.removeMember(orgId, userId, principal);
        return ResponseEntity.ok(ApiResponse.success("Member removed", null));
    }

    @PutMapping("/{orgId}/members/role")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMemberRole(
            @PathVariable String orgId,
            @Valid @RequestBody UpdateMemberRoleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Role updated",
                organizationService.updateMemberRole(orgId, request, principal)));
    }
}
