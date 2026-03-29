package com.jirathon.leaderboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScoreUpdateRequest(
        @NotBlank(message = "userId is required") String userId,
        String displayName,
        String avatarUrl,
        String organizationId,
        Double scoreDelta,
        @NotNull(message = "totalScore is required") Double totalScore
) {
}
