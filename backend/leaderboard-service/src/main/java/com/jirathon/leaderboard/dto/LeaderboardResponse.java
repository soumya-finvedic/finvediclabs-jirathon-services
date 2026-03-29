package com.jirathon.leaderboard.dto;

import java.util.List;

public record LeaderboardResponse(
        String scope,
        String organizationId,
        int offset,
        int limit,
        long totalParticipants,
        List<LeaderboardEntryResponse> entries
) {
}
