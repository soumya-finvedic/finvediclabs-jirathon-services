package com.jirathon.leaderboard.dto;

public record ScoreUpdateEvent(
        String userId,
        String displayName,
        String avatarUrl,
        String organizationId,
        Double scoreDelta,
        Double totalScore,
        Long occurredAt
) {
}
