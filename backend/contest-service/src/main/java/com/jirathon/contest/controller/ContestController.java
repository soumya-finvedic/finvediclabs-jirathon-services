package com.jirathon.contest.controller;

import com.jirathon.contest.dto.request.ConfirmRegistrationPaymentRequest;
import com.jirathon.contest.dto.request.UpdateRegistrationDetailsRequest;
import com.jirathon.contest.dto.response.ApiResponse;
import com.jirathon.contest.dto.response.ContestRegistrationResponse;
import com.jirathon.contest.dto.response.ContestResponse;
import com.jirathon.contest.dto.response.PagedResponse;
import com.jirathon.contest.model.enums.ContestType;
import com.jirathon.contest.security.UserPrincipal;
import com.jirathon.contest.service.ContestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contests")
public class ContestController {

    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ContestResponse>>> listContests(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) ContestType contestType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<ContestResponse> response = contestService.listContests(
                principal.getTenantId(), contestType, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{contestId}")
    public ResponseEntity<ApiResponse<ContestResponse>> getContest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String contestId
    ) {
        ContestResponse response = contestService.getContestById(principal.getTenantId(), contestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{contestId}/register")
    public ResponseEntity<ApiResponse<ContestRegistrationResponse>> registerForContest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String contestId
    ) {
        ContestRegistrationResponse response = contestService.registerForContest(principal, contestId);
        return ResponseEntity.ok(ApiResponse.success("Contest registration initiated", response));
    }

    @GetMapping("/{contestId}/registration/me")
    public ResponseEntity<ApiResponse<ContestRegistrationResponse>> getMyRegistration(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String contestId
    ) {
        ContestRegistrationResponse response = contestService.getRegistrationForUser(principal, contestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/registrations/{registrationId}/confirm-payment")
    public ResponseEntity<ApiResponse<ContestRegistrationResponse>> confirmRegistrationPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String registrationId,
            @Valid @RequestBody ConfirmRegistrationPaymentRequest request
    ) {
        ContestRegistrationResponse response = contestService.confirmRegistrationPayment(
                principal,
                registrationId,
                request.getPaymentTransactionId()
        );
        return ResponseEntity.ok(ApiResponse.success("Registration confirmed", response));
    }

    @PutMapping("/registrations/{registrationId}/details")
    public ResponseEntity<ApiResponse<ContestRegistrationResponse>> updateRegistrationDetails(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String registrationId,
            @Valid @RequestBody UpdateRegistrationDetailsRequest request
    ) {
        ContestRegistrationResponse response = contestService.updateRegistrationDetails(principal, registrationId, request);
        return ResponseEntity.ok(ApiResponse.success("Registration details updated", response));
    }
}