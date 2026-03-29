package com.jirathon.leaderboard.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class LeaderboardRedisRepository {

    private static final String GLOBAL_KEY = "leaderboard:global";
    private static final String ORG_KEY_PREFIX = "leaderboard:org:";
    private static final String USER_KEY_PREFIX = "leaderboard:user:";

    private final StringRedisTemplate redisTemplate;

    public LeaderboardRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void upsertScore(String userId, String organizationId, double score) {
        redisTemplate.opsForZSet().add(GLOBAL_KEY, userId, score);
        if (organizationId != null && !organizationId.isBlank()) {
            redisTemplate.opsForZSet().add(ORG_KEY_PREFIX + organizationId, userId, score);
        }
    }

    public void incrementScore(String userId, String organizationId, double scoreDelta) {
        redisTemplate.opsForZSet().incrementScore(GLOBAL_KEY, userId, scoreDelta);
        if (organizationId != null && !organizationId.isBlank()) {
            redisTemplate.opsForZSet().incrementScore(ORG_KEY_PREFIX + organizationId, userId, scoreDelta);
        }
    }

    public Set<ZSetOperations.TypedTuple<String>> getTopGlobal(int offset, int limit) {
        int end = offset + limit - 1;
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(GLOBAL_KEY, offset, end);
        return tuples == null ? Collections.emptySet() : tuples;
    }

    public Set<ZSetOperations.TypedTuple<String>> getTopOrganization(String organizationId, int offset, int limit) {
        int end = offset + limit - 1;
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(ORG_KEY_PREFIX + organizationId, offset, end);
        return tuples == null ? Collections.emptySet() : tuples;
    }

    public long getGlobalRank(String userId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(GLOBAL_KEY, userId);
        return rank == null ? -1 : rank + 1;
    }

    public long getOrganizationRank(String organizationId, String userId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(ORG_KEY_PREFIX + organizationId, userId);
        return rank == null ? -1 : rank + 1;
    }

    public double getGlobalScore(String userId) {
        Double score = redisTemplate.opsForZSet().score(GLOBAL_KEY, userId);
        return score == null ? 0.0 : score;
    }

    public double getOrganizationScore(String organizationId, String userId) {
        Double score = redisTemplate.opsForZSet().score(ORG_KEY_PREFIX + organizationId, userId);
        return score == null ? 0.0 : score;
    }

    public long countGlobal() {
        Long count = redisTemplate.opsForZSet().zCard(GLOBAL_KEY);
        return count == null ? 0L : count;
    }

    public long countOrganization(String organizationId) {
        Long count = redisTemplate.opsForZSet().zCard(ORG_KEY_PREFIX + organizationId);
        return count == null ? 0L : count;
    }

    public void saveUserProfile(String userId, String displayName, String avatarUrl, String organizationId) {
        String key = USER_KEY_PREFIX + userId;
        Map<String, String> data = new HashMap<>();
        if (displayName != null) data.put("displayName", displayName);
        if (avatarUrl != null) data.put("avatarUrl", avatarUrl);
        if (organizationId != null) data.put("organizationId", organizationId);

        if (!data.isEmpty()) {
            redisTemplate.opsForHash().putAll(key, data);
        }
    }

    public Map<String, String> getUserProfile(String userId) {
        String key = USER_KEY_PREFIX + userId;
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);
        if (raw == null || raw.isEmpty()) {
            return Map.of();
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> e : raw.entrySet()) {
            result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
        }
        return result;
    }

    public Set<String> getGlobalTopUserIds(int offset, int limit) {
        int end = offset + limit - 1;
        Set<String> users = redisTemplate.opsForZSet().reverseRange(GLOBAL_KEY, offset, end);
        return users == null ? new LinkedHashSet<>() : users;
    }

    public Set<String> getOrgTopUserIds(String organizationId, int offset, int limit) {
        int end = offset + limit - 1;
        Set<String> users = redisTemplate.opsForZSet().reverseRange(ORG_KEY_PREFIX + organizationId, offset, end);
        return users == null ? new LinkedHashSet<>() : users;
    }

    public Optional<String> getUserOrganization(String userId) {
        Object org = redisTemplate.opsForHash().get(USER_KEY_PREFIX + userId, "organizationId");
        return org == null ? Optional.empty() : Optional.of(String.valueOf(org));
    }
}
