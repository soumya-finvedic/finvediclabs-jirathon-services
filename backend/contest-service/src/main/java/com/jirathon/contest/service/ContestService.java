package com.jirathon.contest.service;

import com.jirathon.contest.config.ContestProperties;
import com.jirathon.contest.dto.request.CreateContestRequest;
import com.jirathon.contest.dto.request.UpdateRegistrationDetailsRequest;
import com.jirathon.contest.dto.request.UpdateContestRequest;
import com.jirathon.contest.dto.response.BannerUploadResponse;
import com.jirathon.contest.dto.response.ContestRegistrationResponse;
import com.jirathon.contest.dto.response.ContestResponse;
import com.jirathon.contest.dto.response.PagedResponse;
import com.jirathon.contest.exception.ResourceNotFoundException;
import com.jirathon.contest.model.Contest;
import com.jirathon.contest.model.ContestRegistration;
import com.jirathon.contest.model.enums.ContestStatus;
import com.jirathon.contest.model.enums.ContestType;
import com.jirathon.contest.model.enums.RegistrationStatus;
import com.jirathon.contest.repository.ContestRepository;
import com.jirathon.contest.repository.ContestRegistrationRepository;
import com.jirathon.contest.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ContestService {

    private static final Logger log = LoggerFactory.getLogger(ContestService.class);
    private static final Set<String> ALLOWED_BANNER_TYPES = Set.of("image/png", "image/jpeg", "image/webp");

    private final ContestRepository contestRepository;
    private final ContestRegistrationRepository contestRegistrationRepository;
    private final ContestProperties contestProperties;
    private final RestClient restClient;

    public ContestService(
            ContestRepository contestRepository,
            ContestRegistrationRepository contestRegistrationRepository,
            ContestProperties contestProperties,
            @Value("${services.payment.base-url:http://payment-service:8088/api/v1/payments}") String paymentBaseUrl
    ) {
        this.contestRepository = contestRepository;
        this.contestRegistrationRepository = contestRegistrationRepository;
        this.contestProperties = contestProperties;
        this.restClient = RestClient.builder().baseUrl(paymentBaseUrl).build();
    }

    public ContestResponse createContest(String tenantId, String userId, CreateContestRequest request) {
        validateTimeline(request.getRegistrationDeadline(), request.getStartTime(), request.getEndTime());
        validateLanguages(request.getSupportedLanguages());

        contestRepository.findByTenantIdAndSlugAndDeletedFalse(tenantId, request.getSlug())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Contest slug already exists: " + request.getSlug());
                });

        Contest contest = Contest.builder()
                .tenantId(tenantId)
                .title(request.getTitle().trim())
                .slug(request.getSlug().trim())
                .description(request.getDescription().trim())
                .rules(request.getRules())
                .contestType(request.getContestType())
                .supportedLanguages(normalizeLanguages(request.getSupportedLanguages()))
                .registrationDeadline(request.getRegistrationDeadline())
                .registrationFee(request.getRegistrationFee())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        Contest saved = contestRepository.save(contest);
        log.info("Contest {} created by {} in tenant {}", saved.getId(), userId, tenantId);
        return toResponse(saved);
    }

    public ContestResponse updateContest(String tenantId, String contestId, String userId, UpdateContestRequest request) {
        Contest contest = findContestOrThrow(tenantId, contestId);

        if (request.getTitle() != null) {
            contest.setTitle(request.getTitle().trim());
        }
        if (request.getSlug() != null) {
            String newSlug = request.getSlug().trim();
            contestRepository.findByTenantIdAndSlugAndDeletedFalse(tenantId, newSlug)
                    .filter(existing -> !existing.getId().equals(contestId))
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("Contest slug already exists: " + newSlug);
                    });
            contest.setSlug(newSlug);
        }
        if (request.getDescription() != null) {
            contest.setDescription(request.getDescription().trim());
        }
        if (request.getRules() != null) {
            contest.setRules(request.getRules());
        }
        if (request.getContestType() != null) {
            contest.setContestType(request.getContestType());
        }
        if (request.getSupportedLanguages() != null) {
            validateLanguages(request.getSupportedLanguages());
            contest.setSupportedLanguages(normalizeLanguages(request.getSupportedLanguages()));
        }
        if (request.getRegistrationDeadline() != null) {
            contest.setRegistrationDeadline(request.getRegistrationDeadline());
        }
        if (request.getRegistrationFee() != null) {
            contest.setRegistrationFee(request.getRegistrationFee());
        }
        if (request.getStartTime() != null) {
            contest.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            contest.setEndTime(request.getEndTime());
        }
        if (request.getStatus() != null) {
            contest.setStatus(request.getStatus());
        }

        validateTimeline(contest.getRegistrationDeadline(), contest.getStartTime(), contest.getEndTime());

        contest.setUpdatedBy(userId);
        Contest saved = contestRepository.save(contest);
        log.info("Contest {} updated by {} in tenant {}", contestId, userId, tenantId);
        return toResponse(saved);
    }

    public void deleteContest(String tenantId, String contestId, String userId) {
        Contest contest = findContestOrThrow(tenantId, contestId);
        contest.setDeleted(true);
        contest.setDeletedAt(Instant.now());
        contest.setUpdatedBy(userId);
        contestRepository.save(contest);
        log.info("Contest {} soft deleted by {} in tenant {}", contestId, userId, tenantId);
    }

    public PagedResponse<ContestResponse> listContests(String tenantId, ContestType contestType, int page, int size) {
        Pageable pageable = buildPageable(page, size, "createdAt", "desc");
        Page<Contest> resultPage = contestType == null
                ? contestRepository.findByTenantIdAndDeletedFalse(tenantId, pageable)
                : contestRepository.findByTenantIdAndContestTypeAndDeletedFalse(tenantId, contestType, pageable);

        return PagedResponse.<ContestResponse>builder()
                .content(resultPage.getContent().stream().map(this::toResponse).toList())
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .hasNext(resultPage.hasNext())
                .hasPrevious(resultPage.hasPrevious())
                .build();
    }

    public ContestResponse getContestById(String tenantId, String contestId) {
        return toResponse(findContestOrThrow(tenantId, contestId));
    }

    public BannerUploadResponse uploadBanner(String tenantId, String contestId, String userId, MultipartFile bannerFile) {
        Contest contest = findContestOrThrow(tenantId, contestId);

        if (bannerFile == null || bannerFile.isEmpty()) {
            throw new IllegalArgumentException("Banner file is required");
        }

        if (!ALLOWED_BANNER_TYPES.contains(bannerFile.getContentType())) {
            throw new IllegalArgumentException("Banner must be a PNG, JPEG, or WEBP image");
        }

        String original = bannerFile.getOriginalFilename();
        if (original == null || original.isBlank()) {
            original = "banner";
        }
        String safeFilename = original.replaceAll("[^a-zA-Z0-9._-]", "-");
        String bannerUrl = "/api/v1/contests/" + contestId + "/banner/" + System.currentTimeMillis() + "-" + safeFilename;

        contest.setBannerUrl(bannerUrl);
        contest.setUpdatedBy(userId);
        contestRepository.save(contest);

        log.info("Banner updated for contest {} by {} in tenant {}", contestId, userId, tenantId);
        return BannerUploadResponse.builder().contestId(contestId).bannerUrl(bannerUrl).build();
    }

    public Set<String> getAllowedLanguages() {
        return contestProperties.getSupportedLanguages();
    }

    public ContestRegistrationResponse registerForContest(UserPrincipal principal, String contestId) {
        Contest contest = findContestOrThrow(principal.getTenantId(), contestId);
        if (contest.getStatus() != ContestStatus.PUBLISHED) {
            throw new IllegalArgumentException("Contest registration is not available for current contest status");
        }
        if (contest.getRegistrationDeadline().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Registration deadline has passed");
        }

        Optional<ContestRegistration> existing = contestRegistrationRepository
                .findByTenantIdAndContestIdAndUserId(principal.getTenantId(), contestId, principal.getId());

        if (existing.isPresent()) {
            return toRegistrationResponse(existing.get());
        }

        ContestRegistration registration = ContestRegistration.builder()
                .tenantId(principal.getTenantId())
                .contestId(contestId)
                .userId(principal.getId())
                .userEmail(principal.getEmail())
            .userDisplayName(principal.getEmail())
                .amount(contest.getRegistrationFee())
                .status(RegistrationStatus.PENDING_PAYMENT)
                .paymentStatus("PENDING")
                .build();

        registration = contestRegistrationRepository.save(registration);
        return toRegistrationResponse(registration);
    }

    public ContestRegistrationResponse getRegistrationForUser(UserPrincipal principal, String contestId) {
        ContestRegistration registration = contestRegistrationRepository
                .findByTenantIdAndContestIdAndUserId(principal.getTenantId(), contestId, principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found for contest: " + contestId));

        return toRegistrationResponse(registration);
    }

    public ContestRegistrationResponse confirmRegistrationPayment(
            UserPrincipal principal,
            String registrationId,
            String paymentTransactionId
    ) {
        ContestRegistration registration = contestRegistrationRepository.findByIdAndTenantId(registrationId, principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found: " + registrationId));

        if (!registration.getUserId().equals(principal.getId())) {
            throw new IllegalArgumentException("You cannot confirm payment for another user");
        }

        if (registration.getStatus() == RegistrationStatus.CONFIRMED) {
            return toRegistrationResponse(registration);
        }

        PaymentSnapshot paymentSnapshot = fetchPayment(paymentTransactionId);
        if (!registration.getId().equals(paymentSnapshot.registrationId())) {
            throw new IllegalArgumentException("Payment transaction does not belong to this registration");
        }

        if (!"SUCCESS".equalsIgnoreCase(paymentSnapshot.status())) {
            throw new IllegalArgumentException("Payment is not successful yet. Current status: " + paymentSnapshot.status());
        }

        registration.setPaymentTransactionId(paymentTransactionId);
        registration.setPaymentStatus(paymentSnapshot.status().toUpperCase(Locale.ROOT));
        registration.setStatus(RegistrationStatus.CONFIRMED);
        registration.setConfirmedAt(Instant.now());

        registration = contestRegistrationRepository.save(registration);
        return toRegistrationResponse(registration);
    }

    public ContestRegistrationResponse updateRegistrationDetails(
            UserPrincipal principal,
            String registrationId,
            UpdateRegistrationDetailsRequest request
    ) {
        ContestRegistration registration = contestRegistrationRepository.findByIdAndTenantId(registrationId, principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found: " + registrationId));

        if (!registration.getUserId().equals(principal.getId())) {
            throw new IllegalArgumentException("You cannot update another user's registration details");
        }

        registration.setTeamName(request.getTeamName().trim());
        registration.setContactNumber(request.getContactNumber().trim());
        registration.setCouponCode(request.getCouponCode() == null ? null : request.getCouponCode().trim());

        registration = contestRegistrationRepository.save(registration);
        return toRegistrationResponse(registration);
    }

    private Contest findContestOrThrow(String tenantId, String contestId) {
        return contestRepository.findByIdAndTenantIdAndDeletedFalse(contestId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found: " + contestId));
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        return PageRequest.of(safePage, safeSize, sort);
    }

    private void validateTimeline(Instant registrationDeadline, Instant startTime, Instant endTime) {
        if (registrationDeadline == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Registration deadline, start time, and end time are required");
        }
        if (!registrationDeadline.isBefore(startTime)) {
            throw new IllegalArgumentException("Registration deadline must be before contest start time");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }

    private void validateLanguages(Set<String> selectedLanguages) {
        Set<String> allowed = normalizeLanguages(contestProperties.getSupportedLanguages());
        Set<String> selected = normalizeLanguages(selectedLanguages);
        if (!allowed.containsAll(selected)) {
            throw new IllegalArgumentException("Selected languages contain unsupported entries");
        }
    }

    private Set<String> normalizeLanguages(Set<String> languages) {
        Set<String> normalized = new LinkedHashSet<>();
        for (String language : languages) {
            if (language != null && !language.isBlank()) {
                normalized.add(language.trim().toLowerCase(Locale.ROOT));
            }
        }
        return normalized;
    }

    private ContestResponse toResponse(Contest contest) {
        return ContestResponse.builder()
                .id(contest.getId())
                .tenantId(contest.getTenantId())
                .title(contest.getTitle())
                .slug(contest.getSlug())
                .description(contest.getDescription())
                .rules(contest.getRules())
                .contestType(contest.getContestType())
                .supportedLanguages(contest.getSupportedLanguages())
                .bannerUrl(contest.getBannerUrl())
                .registrationDeadline(contest.getRegistrationDeadline())
                .registrationFee(contest.getRegistrationFee())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .status(contest.getStatus())
                .createdAt(contest.getCreatedAt())
                .updatedAt(contest.getUpdatedAt())
                .build();
    }

    private ContestRegistrationResponse toRegistrationResponse(ContestRegistration registration) {
        return ContestRegistrationResponse.builder()
                .id(registration.getId())
                .contestId(registration.getContestId())
                .userId(registration.getUserId())
                .userEmail(registration.getUserEmail())
                .userDisplayName(registration.getUserDisplayName())
                .teamName(registration.getTeamName())
                .contactNumber(registration.getContactNumber())
                .couponCode(registration.getCouponCode())
                .amount(registration.getAmount())
                .status(registration.getStatus())
                .paymentTransactionId(registration.getPaymentTransactionId())
                .paymentStatus(registration.getPaymentStatus())
                .confirmedAt(registration.getConfirmedAt())
                .createdAt(registration.getCreatedAt())
                .updatedAt(registration.getUpdatedAt())
                .build();
    }

    private PaymentSnapshot fetchPayment(String paymentTransactionId) {
        try {
            ResponseEntity<Map> response = restClient.get()
                    .uri("/{paymentTransactionId}", paymentTransactionId)
                    .retrieve()
                    .toEntity(Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalArgumentException("Unable to verify payment status");
            }

            Map<String, Object> root = response.getBody();
            Object dataObj = root.get("data");
            if (!(dataObj instanceof Map<?, ?> paymentData)) {
                throw new IllegalArgumentException("Payment verification response is invalid");
            }

            String registrationId = String.valueOf(paymentData.get("registrationId"));
            String status = String.valueOf(paymentData.get("status"));
            return new PaymentSnapshot(registrationId, status);
        } catch (RestClientException ex) {
            throw new IllegalArgumentException("Payment service unavailable for verification");
        }
    }

    private record PaymentSnapshot(String registrationId, String status) {}
}
