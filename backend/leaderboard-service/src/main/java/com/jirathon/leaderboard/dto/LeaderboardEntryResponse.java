package com.jirathon.leaderboard.dto;

public record LeaderboardEntryResponse(
        String userId,
        String displayName,
        String avatarUrl,
        String organizationId,
        double score,
        long rank
) {
}
