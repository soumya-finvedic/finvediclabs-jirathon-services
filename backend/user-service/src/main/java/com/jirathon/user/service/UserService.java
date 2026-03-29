package com.jirathon.user.service;

import com.jirathon.user.dto.request.UpdateProfileRequest;
import com.jirathon.user.dto.request.UpdateRolesRequest;
import com.jirathon.user.dto.request.UpdateStatusRequest;
import com.jirathon.user.dto.response.PagedResponse;
import com.jirathon.user.dto.response.UserResponse;
import com.jirathon.user.exception.ForbiddenException;
import com.jirathon.user.exception.ResourceNotFoundException;
import com.jirathon.user.model.User;
import com.jirathon.user.model.enums.Role;
import com.jirathon.user.model.enums.UserStatus;
import com.jirathon.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ────────────────── Profile ──────────────────

    public UserResponse getUserById(String userId, String tenantId) {
        User user = findUserOrThrow(userId, tenantId);
        return toResponse(user);
    }

    public UserResponse getMyProfile(String userId, String tenantId) {
        return getUserById(userId, tenantId);
    }

    public UserResponse updateProfile(String userId, String tenantId, UpdateProfileRequest request) {
        User user = findUserOrThrow(userId, tenantId);

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        // Build or update profile subdocument
        User.Profile profile = user.getProfile() != null ? user.getProfile() : User.Profile.builder().build();

        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());
        if (request.getTimezone() != null) profile.setTimezone(request.getTimezone());
        if (request.getLanguage() != null) profile.setLanguage(request.getLanguage());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());

        user.setProfile(profile);
        user.setUpdatedBy(userId);
        user = userRepository.save(user);

        log.info("Profile updated for user {} in tenant {}", userId, tenantId);
        return toResponse(user);
    }

    // ────────────────── Listing (paginated) ──────────────────

    public PagedResponse<UserResponse> listUsers(String tenantId, int page, int size, String sortBy, String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<User> userPage = userRepository.findByTenantIdAndDeletedFalse(tenantId, pageable);
        return toPagedResponse(userPage);
    }

    public PagedResponse<UserResponse> listUsersByStatus(String tenantId, UserStatus status, int page, int size) {
        Pageable pageable = buildPageable(page, size, "createdAt", "desc");
        Page<User> userPage = userRepository.findByTenantIdAndStatusAndDeletedFalse(tenantId, status, pageable);
        return toPagedResponse(userPage);
    }

    public PagedResponse<UserResponse> listUsersByOrganization(String tenantId, String orgId, int page, int size) {
        Pageable pageable = buildPageable(page, size, "displayName", "asc");
        Page<User> userPage = userRepository.findByTenantIdAndOrganizationIdAndDeletedFalse(tenantId, orgId, pageable);
        return toPagedResponse(userPage);
    }

    public PagedResponse<UserResponse> searchUsers(String tenantId, String query, int page, int size) {
        String safeRegex = "(?i)" + Pattern.quote(query);
        Pageable pageable = buildPageable(page, size, "displayName", "asc");
        Page<User> userPage = userRepository.findByTenantIdAndDisplayNameRegexAndDeletedFalse(tenantId, safeRegex, pageable);
        return toPagedResponse(userPage);
    }

    public PagedResponse<UserResponse> getLeaderboard(String tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "stats.totalScore"));
        Page<User> userPage = userRepository.findByTenantIdAndDeletedFalse(tenantId, pageable);
        return toPagedResponse(userPage);
    }

    // ────────────────── Admin: Role Management ──────────────────

    public UserResponse updateRoles(String tenantId, UpdateRolesRequest request, String adminId) {
        User user = findUserOrThrow(request.getUserId(), tenantId);

        // Prevent escalation: only SUPER_ADMIN can assign SUPER_ADMIN
        if (request.getRoles().contains(Role.SUPER_ADMIN)) {
            throw new ForbiddenException("Only Super Admins can assign the SUPER_ADMIN role");
        }

        user.setRoles(request.getRoles());
        user.setUpdatedBy(adminId);
        user = userRepository.save(user);

        log.info("Roles updated for user {} to {} by admin {} in tenant {}",
                request.getUserId(), request.getRoles(), adminId, tenantId);
        return toResponse(user);
    }

    public UserResponse updateStatus(String tenantId, UpdateStatusRequest request, String adminId) {
        User user = findUserOrThrow(request.getUserId(), tenantId);

        user.setStatus(request.getStatus());
        user.setUpdatedBy(adminId);
        if (request.getStatus() == UserStatus.BANNED) {
            // No additional fields needed; status conveys the ban
        }
        user = userRepository.save(user);

        log.info("Status updated for user {} to {} by admin {} in tenant {}",
                request.getUserId(), request.getStatus(), adminId, tenantId);
        return toResponse(user);
    }

    // ────────────────── Soft Delete ──────────────────

    public void deleteUser(String userId, String tenantId, String adminId) {
        User user = findUserOrThrow(userId, tenantId);
        user.setDeleted(true);
        user.setDeletedAt(Instant.now());
        user.setUpdatedBy(adminId);
        userRepository.save(user);
        log.info("User {} soft-deleted by admin {} in tenant {}", userId, adminId, tenantId);
    }

    // ────────────────── Internal helpers ──────────────────

    private User findUserOrThrow(String userId, String tenantId) {
        System.err.println("Looking up userId=" + userId + " tenantId=" + tenantId);

        Optional<User> result = userRepository.findByIdAndTenantIdAndDeletedFalse(userId, tenantId);

        // ✅ Print what MongoDB returned
        if (result.isPresent()) {
            System.err.println("✅ User FOUND: " + result.get().getId() + " | " + result.get().getEmail());
        } else {
            System.err.println("❌ User NOT FOUND — trying diagnostics...");

            // Check 1: Does the ID exist at all (ignore tenantId & deleted)?
            Optional<User> rawById = userRepository.findById(userId);
            System.err.println("   findById only → " + (rawById.isPresent() ? "FOUND ✅" : "NOT FOUND ❌"));

            if (rawById.isPresent()) {
                User u = rawById.get();
                System.err.println("   stored tenantId  = '" + u.getTenantId() + "'");
                System.err.println("   queried tenantId = '" + tenantId + "'");
                System.err.println("   tenantId match?  = " + tenantId.equals(u.getTenantId()));
                System.err.println("   deleted?         = " + u.isDeleted());
            }
        }

        return result.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        return PageRequest.of(safePage, safeSize, sort);
    }

    private PagedResponse<UserResponse> toPagedResponse(Page<User> page) {
        List<UserResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PagedResponse.<UserResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    private UserResponse toResponse(User user) {
        UserResponse.ProfileResponse profileResp = null;
        if (user.getProfile() != null) {
            User.Profile p = user.getProfile();
            profileResp = UserResponse.ProfileResponse.builder()
                    .phone(p.getPhone())
                    .bio(p.getBio())
                    .skills(p.getSkills())
                    .timezone(p.getTimezone())
                    .language(p.getLanguage())
                    .linkedinUrl(p.getLinkedinUrl())
                    .githubUrl(p.getGithubUrl())
                    .city(p.getCity())
                    .country(p.getCountry())
                    .build();
        }

        UserResponse.StatsResponse statsResp = null;
        if (user.getStats() != null) {
            User.Stats s = user.getStats();
            statsResp = UserResponse.StatsResponse.builder()
                    .contestsParticipated(s.getContestsParticipated())
                    .totalScore(s.getTotalScore())
                    .bestRank(s.getBestRank())
                    .avgAccuracy(s.getAvgAccuracy())
                    .contestsWon(s.getContestsWon())
                    .totalSubmissions(s.getTotalSubmissions())
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .tenantId(user.getTenantId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .organizationId(user.getOrganizationId())
                .emailVerified(user.isEmailVerified())
                .status(user.getStatus().name())
                .profile(profileResp)
                .stats(statsResp)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
