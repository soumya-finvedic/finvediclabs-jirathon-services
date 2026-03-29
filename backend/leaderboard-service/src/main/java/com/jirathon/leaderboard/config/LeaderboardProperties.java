package com.jirathon.leaderboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leaderboard")
public class LeaderboardProperties {

    private String kafkaTopic = "leaderboard-score-events";
    private String websocketTopGlobal = "/topic/leaderboards/global";
    private String websocketTopOrgPrefix = "/topic/leaderboards/org.";

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getWebsocketTopGlobal() {
        return websocketTopGlobal;
    }

    public void setWebsocketTopGlobal(String websocketTopGlobal) {
        this.websocketTopGlobal = websocketTopGlobal;
    }

    public String getWebsocketTopOrgPrefix() {
        return websocketTopOrgPrefix;
    }

    public void setWebsocketTopOrgPrefix(String websocketTopOrgPrefix) {
        this.websocketTopOrgPrefix = websocketTopOrgPrefix;
    }
}
