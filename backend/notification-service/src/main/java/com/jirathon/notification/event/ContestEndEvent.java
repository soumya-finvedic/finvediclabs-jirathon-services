package com.jirathon.notification.event;

import java.time.Instant;

public class ContestEndEvent {
    private String contestId;
    private String contestTitle;
    private Instant endTime;
    private Long totalParticipants;
    private String organizationId;
    private Instant timestamp;

    public ContestEndEvent() {
    }

    public ContestEndEvent(String contestId, String contestTitle, Instant endTime,
                           Long totalParticipants, String organizationId, Instant timestamp) {
        this.contestId = contestId;
        this.contestTitle = contestTitle;
        this.endTime = endTime;
        this.totalParticipants = totalParticipants;
        this.organizationId = organizationId;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getContestTitle() {
        return contestTitle;
    }

    public void setContestTitle(String contestTitle) {
        this.contestTitle = contestTitle;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Long getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Long totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static final class Builder {
        private String contestId;
        private String contestTitle;
        private Instant endTime;
        private Long totalParticipants;
        private String organizationId;
        private Instant timestamp;

        private Builder() {
        }

        public Builder contestId(String contestId) {
            this.contestId = contestId;
            return this;
        }

        public Builder contestTitle(String contestTitle) {
            this.contestTitle = contestTitle;
            return this;
        }

        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder totalParticipants(Long totalParticipants) {
            this.totalParticipants = totalParticipants;
            return this;
        }

        public Builder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ContestEndEvent build() {
            return new ContestEndEvent(contestId, contestTitle, endTime, totalParticipants,
                    organizationId, timestamp);
        }
    }
}
