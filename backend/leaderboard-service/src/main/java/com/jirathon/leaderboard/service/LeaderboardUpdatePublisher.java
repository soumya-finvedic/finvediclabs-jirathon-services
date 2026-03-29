package com.jirathon.leaderboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jirathon.leaderboard.config.LeaderboardProperties;
import com.jirathon.leaderboard.dto.ScoreUpdateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardUpdatePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final LeaderboardProperties properties;
    private final ObjectMapper objectMapper;

    public LeaderboardUpdatePublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            LeaderboardProperties properties,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public void publish(ScoreUpdateEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(properties.getKafkaTopic(), event.userId(), payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid score update payload", e);
        }
    }
}
