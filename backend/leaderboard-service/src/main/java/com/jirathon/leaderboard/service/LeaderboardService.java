package com.jirathon.leaderboard.service;

import com.jirathon.leaderboard.dto.LeaderboardEntryResponse;
import com.jirathon.leaderboard.dto.LeaderboardResponse;
import com.jirathon.leaderboard.dto.RankResponse;
import com.jirathon.leaderboard.dto.ScoreUpdateEvent;
import com.jirathon.leaderboard.repository.LeaderboardRedisRepository;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LeaderboardService {

    private final LeaderboardRedisRepository redisRepository;

    public LeaderboardService(LeaderboardRedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public void applyScoreUpdate(ScoreUpdateEvent event) {
        if (event.totalScore() != null) {
            redisRepository.upsertScore(event.userId(), event.organizationId(), event.totalScore());
        } else {
            double delta = event.scoreDelta() == null ? 0.0 : event.scoreDelta();
            redisRepository.incrementScore(event.userId(), event.organizationId(), delta);
        }

        redisRepository.saveUserProfile(
                event.userId(),
                event.displayName(),
                event.avatarUrl(),
                event.organizationId()
        );
    }

    public LeaderboardResponse getGlobalLeaderboard(int offset, int limit) {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisRepository.getTopGlobal(offset, limit);
        List<LeaderboardEntryResponse> entries = toEntries(tuples, offset + 1);
        return new LeaderboardResponse(
                "GLOBAL",
                null,
                offset,
                limit,
                redisRepository.countGlobal(),
                entries
        );
    }

    public LeaderboardResponse getOrganizationLeaderboard(String organizationId, int offset, int limit) {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisRepository.getTopOrganization(organizationId, offset, limit);
        List<LeaderboardEntryResponse> entries = toEntries(tuples, offset + 1);
        return new LeaderboardResponse(
                "ORGANIZATION",
                organizationId,
                offset,
                limit,
                redisRepository.countOrganization(organizationId),
                entries
        );
    }

    public RankResponse getGlobalRank(String userId) {
        return new RankResponse(
                "GLOBAL",
                null,
                userId,
                redisRepository.getGlobalRank(userId),
                redisRepository.getGlobalScore(userId)
        );
    }

    public RankResponse getOrganizationRank(String organizationId, String userId) {
        return new RankResponse(
                "ORGANIZATION",
                organizationId,
                userId,
                redisRepository.getOrganizationRank(organizationId, userId),
                redisRepository.getOrganizationScore(organizationId, userId)
        );
    }

    private List<LeaderboardEntryResponse> toEntries(Set<ZSetOperations.TypedTuple<String>> tuples, int firstRank) {
        List<LeaderboardEntryResponse> list = new ArrayList<>();
        long currentRank = firstRank;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String userId = tuple.getValue();
            if (userId == null) {
                continue;
            }
            Map<String, String> profile = redisRepository.getUserProfile(userId);
            list.add(new LeaderboardEntryResponse(
                    userId,
                    profile.getOrDefault("displayName", userId),
                    profile.get("avatarUrl"),
                    profile.get("organizationId"),
                    tuple.getScore() == null ? 0.0 : tuple.getScore(),
                    currentRank
            ));
            currentRank++;
        }
        return list;
    }
}
