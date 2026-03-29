package com.jirathon.leaderboard.controller;

import com.jirathon.leaderboard.dto.ApiResponse;
import com.jirathon.leaderboard.dto.LeaderboardResponse;
import com.jirathon.leaderboard.dto.RankResponse;
import com.jirathon.leaderboard.dto.ScoreUpdateEvent;
import com.jirathon.leaderboard.dto.ScoreUpdateRequest;
import com.jirathon.leaderboard.service.LeaderboardService;
import com.jirathon.leaderboard.service.LeaderboardUpdatePublisher;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final LeaderboardUpdatePublisher updatePublisher;

    public LeaderboardController(LeaderboardService leaderboardService, LeaderboardUpdatePublisher updatePublisher) {
        this.leaderboardService = leaderboardService;
        this.updatePublisher = updatePublisher;
    }

    @GetMapping("/leaderboards/global")
    public ResponseEntity<ApiResponse<LeaderboardResponse>> getGlobalLeaderboard(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit
    ) {
        int safeOffset = Math.max(0, offset);
        int safeLimit = Math.min(Math.max(1, limit), 100);
        LeaderboardResponse response = leaderboardService.getGlobalLeaderboard(safeOffset, safeLimit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/leaderboards/organizations/{organizationId}")
    public ResponseEntity<ApiResponse<LeaderboardResponse>> getOrganizationLeaderboard(
            @PathVariable String organizationId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit
    ) {
        int safeOffset = Math.max(0, offset);
        int safeLimit = Math.min(Math.max(1, limit), 100);
        LeaderboardResponse response = leaderboardService.getOrganizationLeaderboard(organizationId, safeOffset, safeLimit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/leaderboards/global/users/{userId}/rank")
    public ResponseEntity<ApiResponse<RankResponse>> getGlobalRank(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(leaderboardService.getGlobalRank(userId)));
    }

    @GetMapping("/leaderboards/organizations/{organizationId}/users/{userId}/rank")
    public ResponseEntity<ApiResponse<RankResponse>> getOrganizationRank(
            @PathVariable String organizationId,
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(leaderboardService.getOrganizationRank(organizationId, userId)));
    }

    @PostMapping("/admin/leaderboards/score-events")
    public ResponseEntity<ApiResponse<Void>> publishScoreUpdate(@Valid @RequestBody ScoreUpdateRequest request) {
        ScoreUpdateEvent event = new ScoreUpdateEvent(
                request.userId(),
                request.displayName(),
                request.avatarUrl(),
                request.organizationId(),
                request.scoreDelta(),
                request.totalScore(),
                System.currentTimeMillis()
        );
        updatePublisher.publish(event);
        return ResponseEntity.ok(ApiResponse.success("Event published", null));
    }
}
