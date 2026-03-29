package com.jirathon.execution.controller;

import com.jirathon.execution.dto.request.ExecutionRequest;
import com.jirathon.execution.dto.response.ApiResponse;
import com.jirathon.execution.dto.response.ExecutionResponse;
import com.jirathon.execution.security.UserPrincipal;
import com.jirathon.execution.service.ExecutionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/executions")
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ExecutionResponse>> run(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ExecutionRequest request
    ) {
        ExecutionResponse response = executionService.execute(request);
        return ResponseEntity.ok(ApiResponse.success("Execution complete", response));
    }

    @GetMapping("/languages")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Set<String>>> supportedLanguages() {
        return ResponseEntity.ok(ApiResponse.success(executionService.getSupportedLanguages()));
    }
}
