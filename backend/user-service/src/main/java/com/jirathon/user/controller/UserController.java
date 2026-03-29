package com.jirathon.user.controller;

import com.jirathon.user.dto.request.UpdateProfileRequest;
import com.jirathon.user.dto.response.ApiResponse;
import com.jirathon.user.dto.response.PagedResponse;
import com.jirathon.user.dto.response.UserResponse;
import com.jirathon.user.security.UserPrincipal;
import com.jirathon.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ────────────────── Own Profile ──────────────────

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse response = userService.getMyProfile(principal.getId(), principal.getTenantId());
        System.err.println(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserResponse response = userService.updateProfile(
                principal.getId(), principal.getTenantId(), request
        );
        return ResponseEntity.ok(ApiResponse.success("Profile updated", response));
    }

    // ────────────────── Public User Lookup ──────────────────

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable String userId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse response = userService.getUserById(userId, principal.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> searchUsers(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<UserResponse> response = userService.searchUsers(
                principal.getTenantId(), q, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ────────────────── Leaderboard ──────────────────

    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getLeaderboard(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        PagedResponse<UserResponse> response = userService.getLeaderboard(
                principal.getTenantId(), page, size
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ────────────────── Organization Members ──────────────────

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getUsersByOrg(
            @PathVariable String orgId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<UserResponse> response = userService.listUsersByOrganization(
                principal.getTenantId(), orgId, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
