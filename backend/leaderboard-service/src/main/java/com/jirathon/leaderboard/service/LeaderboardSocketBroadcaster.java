package com.jirathon.leaderboard.service;

import com.jirathon.leaderboard.config.LeaderboardProperties;
import com.jirathon.leaderboard.dto.LeaderboardResponse;
import com.jirathon.leaderboard.dto.ScoreUpdateEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardSocketBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final LeaderboardService leaderboardService;
    private final LeaderboardProperties properties;

    public LeaderboardSocketBroadcaster(
            SimpMessagingTemplate messagingTemplate,
            LeaderboardService leaderboardService,
            LeaderboardProperties properties
    ) {
        this.messagingTemplate = messagingTemplate;
        this.leaderboardService = leaderboardService;
        this.properties = properties;
    }

    public void broadcastAfterUpdate(ScoreUpdateEvent event) {
        LeaderboardResponse global = leaderboardService.getGlobalLeaderboard(0, 20);
        messagingTemplate.convertAndSend(properties.getWebsocketTopGlobal(), global);

        if (event.organizationId() != null && !event.organizationId().isBlank()) {
            LeaderboardResponse org = leaderboardService.getOrganizationLeaderboard(event.organizationId(), 0, 20);
            messagingTemplate.convertAndSend(properties.getWebsocketTopOrgPrefix() + event.organizationId(), org);
        }
    }
}
