package com.jirathon.user.controller;

import com.jirathon.user.dto.request.UpdateRolesRequest;
import com.jirathon.user.dto.request.UpdateStatusRequest;
import com.jirathon.user.dto.response.ApiResponse;
import com.jirathon.user.dto.response.PagedResponse;
import com.jirathon.user.dto.response.UserResponse;
import com.jirathon.user.model.enums.UserStatus;
import com.jirathon.user.security.UserPrincipal;
import com.jirathon.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> listUsers(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        PagedResponse<UserResponse> response = userService.listUsers(
                principal.getTenantId(), page, size, sortBy, direction
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> listByStatus(
            @PathVariable UserStatus status,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<UserResponse> response = userService.listUsersByStatus(
                principal.getTenantId(), status, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/roles")
    public ResponseEntity<ApiResponse<UserResponse>> updateRoles(
            @Valid @RequestBody UpdateRolesRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse response = userService.updateRoles(
                principal.getTenantId(), request, principal.getId()
        );
        return ResponseEntity.ok(ApiResponse.success("Roles updated", response));
    }

    @PutMapping("/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @Valid @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse response = userService.updateStatus(
                principal.getTenantId(), request, principal.getId()
        );
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String userId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.deleteUser(userId, principal.getTenantId(), principal.getId());
        return ResponseEntity.ok(ApiResponse.success("User deleted"));
    }
}
