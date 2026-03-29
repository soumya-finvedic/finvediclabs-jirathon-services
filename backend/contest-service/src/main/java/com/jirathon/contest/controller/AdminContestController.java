package com.jirathon.contest.controller;

import com.jirathon.contest.dto.request.CreateContestRequest;
import com.jirathon.contest.dto.request.UpdateContestRequest;
import com.jirathon.contest.dto.response.ApiResponse;
import com.jirathon.contest.dto.response.BannerUploadResponse;
import com.jirathon.contest.dto.response.ContestResponse;
import com.jirathon.contest.dto.response.PagedResponse;
import com.jirathon.contest.model.enums.ContestType;
import com.jirathon.contest.security.UserPrincipal;
import com.jirathon.contest.service.ContestService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin/contests")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminContestController {

    private final ContestService contestService;

    public AdminContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContestResponse>> createContest(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateContestRequest request
    ) {
        ContestResponse response = contestService.createContest(principal.getTenantId(), principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Contest created", response));
    }

    @PutMapping("/{contestId}")
    public ResponseEntity<ApiResponse<ContestResponse>> updateContest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String contestId,
            @Valid @RequestBody UpdateContestRequest request
    ) {
        ContestResponse response = contestService.updateContest(
                principal.getTenantId(), contestId, principal.getId(), request
        );
        return ResponseEntity.ok(ApiResponse.success("Contest updated", response));
    }

    @DeleteMapping("/{contestId}")
    public ResponseEntity<ApiResponse<Void>> deleteContest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String contestId
    ) {
        contestService.deleteContest(principal.getTenantId(), contestId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Contest deleted"));
    }

    @PostMapping(value = "/{contestId}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BannerUploadResponse>> uploadBanner(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String contestId,
            @RequestPart("file") MultipartFile file
    ) {
        BannerUploadResponse response = contestService.uploadBanner(
                principal.getTenantId(), contestId, principal.getId(), file
        );
        return ResponseEntity.ok(ApiResponse.success("Banner uploaded", response));
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

    @GetMapping("/meta/supported-languages")
    public ResponseEntity<ApiResponse<Set<String>>> getSupportedLanguages() {
        return ResponseEntity.ok(ApiResponse.success(contestService.getAllowedLanguages()));
    }
}
