package com.jirathon.notification.event;

import java.time.Instant;

public class ContestStartEvent {
    private String contestId;
    private String contestTitle;
    private String contestDescription;
    private Long durationMinutes;
    private Instant startTime;
    private String organizationId;
    private String createdBy;
    private Instant timestamp;

    public ContestStartEvent() {
    }

    public ContestStartEvent(String contestId, String contestTitle, String contestDescription,
                             Long durationMinutes, Instant startTime, String organizationId,
                             String createdBy, Instant timestamp) {
        this.contestId = contestId;
        this.contestTitle = contestTitle;
        this.contestDescription = contestDescription;
        this.durationMinutes = durationMinutes;
        this.startTime = startTime;
        this.organizationId = organizationId;
        this.createdBy = createdBy;
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

    public String getContestDescription() {
        return contestDescription;
    }

    public void setContestDescription(String contestDescription) {
        this.contestDescription = contestDescription;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
        private String contestDescription;
        private Long durationMinutes;
        private Instant startTime;
        private String organizationId;
        private String createdBy;
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

        public Builder contestDescription(String contestDescription) {
            this.contestDescription = contestDescription;
            return this;
        }

        public Builder durationMinutes(Long durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ContestStartEvent build() {
            return new ContestStartEvent(contestId, contestTitle, contestDescription, durationMinutes,
                    startTime, organizationId, createdBy, timestamp);
        }
    }
}
