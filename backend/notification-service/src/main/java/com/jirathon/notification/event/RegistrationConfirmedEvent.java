package com.jirathon.notification.event;

import java.time.Instant;

public class RegistrationConfirmedEvent {
    private String registrationId;
    private String userId;
    private String email;
    private String fullName;
    private String contestId;
    private String contestTitle;
    private Instant registrationDate;
    private Instant contestStartDate;
    private Instant timestamp;

    public RegistrationConfirmedEvent() {
    }

    public RegistrationConfirmedEvent(String registrationId, String userId, String email, String fullName,
                                      String contestId, String contestTitle, Instant registrationDate,
                                      Instant contestStartDate, Instant timestamp) {
        this.registrationId = registrationId;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.contestId = contestId;
        this.contestTitle = contestTitle;
        this.registrationDate = registrationDate;
        this.contestStartDate = contestStartDate;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Instant getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Instant getContestStartDate() {
        return contestStartDate;
    }

    public void setContestStartDate(Instant contestStartDate) {
        this.contestStartDate = contestStartDate;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static final class Builder {
        private String registrationId;
        private String userId;
        private String email;
        private String fullName;
        private String contestId;
        private String contestTitle;
        private Instant registrationDate;
        private Instant contestStartDate;
        private Instant timestamp;

        private Builder() {
        }

        public Builder registrationId(String registrationId) {
            this.registrationId = registrationId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder contestId(String contestId) {
            this.contestId = contestId;
            return this;
        }

        public Builder contestTitle(String contestTitle) {
            this.contestTitle = contestTitle;
            return this;
        }

        public Builder registrationDate(Instant registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder contestStartDate(Instant contestStartDate) {
            this.contestStartDate = contestStartDate;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public RegistrationConfirmedEvent build() {
            return new RegistrationConfirmedEvent(registrationId, userId, email, fullName, contestId,
                    contestTitle, registrationDate, contestStartDate, timestamp);
        }
    }
}
