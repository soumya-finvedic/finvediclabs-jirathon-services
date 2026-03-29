package com.jirathon.leaderboard.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jirathon.leaderboard.config.LeaderboardProperties;
import com.jirathon.leaderboard.dto.ScoreUpdateEvent;
import com.jirathon.leaderboard.service.LeaderboardService;
import com.jirathon.leaderboard.service.LeaderboardSocketBroadcaster;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScoreUpdateConsumer {

    private final ObjectMapper objectMapper;
    private final LeaderboardService leaderboardService;
    private final LeaderboardSocketBroadcaster socketBroadcaster;

    public ScoreUpdateConsumer(
            ObjectMapper objectMapper,
            LeaderboardService leaderboardService,
            LeaderboardSocketBroadcaster socketBroadcaster
    ) {
        this.objectMapper = objectMapper;
        this.leaderboardService = leaderboardService;
        this.socketBroadcaster = socketBroadcaster;
    }

    @KafkaListener(topics = "${leaderboard.kafka-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String payload) throws Exception {
        ScoreUpdateEvent event = objectMapper.readValue(payload, ScoreUpdateEvent.class);
        leaderboardService.applyScoreUpdate(event);
        socketBroadcaster.broadcastAfterUpdate(event);
    }
}
