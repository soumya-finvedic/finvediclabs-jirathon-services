package com.jirathon.leaderboard.dto;

public record RankResponse(
        String scope,
        String organizationId,
        String userId,
        long rank,
        double score
) {
}
