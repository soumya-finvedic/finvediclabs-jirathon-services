package com.jirathon.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notification_preferences")
public class NotificationPreference {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private boolean emailNotificationsEnabled;

    private boolean paymentNotificationsEnabled;

    private boolean contestNotificationsEnabled;

    private boolean scoreUpdateNotificationsEnabled;

    private boolean leaderboardNotificationsEnabled;

    private boolean systemAlertNotificationsEnabled;

    private String preferredEmail;

    private String timezone;

    private boolean unsubscribedAll;

    private Instant createdAt;

    private Instant updatedAt;

    public NotificationPreference() {
    }

    public NotificationPreference(String id, String userId, boolean emailNotificationsEnabled,
                                  boolean paymentNotificationsEnabled, boolean contestNotificationsEnabled,
                                  boolean scoreUpdateNotificationsEnabled, boolean leaderboardNotificationsEnabled,
                                  boolean systemAlertNotificationsEnabled, String preferredEmail,
                                  String timezone, boolean unsubscribedAll, Instant createdAt,
                                  Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.paymentNotificationsEnabled = paymentNotificationsEnabled;
        this.contestNotificationsEnabled = contestNotificationsEnabled;
        this.scoreUpdateNotificationsEnabled = scoreUpdateNotificationsEnabled;
        this.leaderboardNotificationsEnabled = leaderboardNotificationsEnabled;
        this.systemAlertNotificationsEnabled = systemAlertNotificationsEnabled;
        this.preferredEmail = preferredEmail;
        this.timezone = timezone;
        this.unsubscribedAll = unsubscribedAll;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public boolean isPaymentNotificationsEnabled() {
        return paymentNotificationsEnabled;
    }

    public void setPaymentNotificationsEnabled(boolean paymentNotificationsEnabled) {
        this.paymentNotificationsEnabled = paymentNotificationsEnabled;
    }

    public boolean isContestNotificationsEnabled() {
        return contestNotificationsEnabled;
    }

    public void setContestNotificationsEnabled(boolean contestNotificationsEnabled) {
        this.contestNotificationsEnabled = contestNotificationsEnabled;
    }

    public boolean isScoreUpdateNotificationsEnabled() {
        return scoreUpdateNotificationsEnabled;
    }

    public void setScoreUpdateNotificationsEnabled(boolean scoreUpdateNotificationsEnabled) {
        this.scoreUpdateNotificationsEnabled = scoreUpdateNotificationsEnabled;
    }

    public boolean isLeaderboardNotificationsEnabled() {
        return leaderboardNotificationsEnabled;
    }

    public void setLeaderboardNotificationsEnabled(boolean leaderboardNotificationsEnabled) {
        this.leaderboardNotificationsEnabled = leaderboardNotificationsEnabled;
    }

    public boolean isSystemAlertNotificationsEnabled() {
        return systemAlertNotificationsEnabled;
    }

    public void setSystemAlertNotificationsEnabled(boolean systemAlertNotificationsEnabled) {
        this.systemAlertNotificationsEnabled = systemAlertNotificationsEnabled;
    }

    public String getPreferredEmail() {
        return preferredEmail;
    }

    public void setPreferredEmail(String preferredEmail) {
        this.preferredEmail = preferredEmail;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isUnsubscribedAll() {
        return unsubscribedAll;
    }

    public void setUnsubscribedAll(boolean unsubscribedAll) {
        this.unsubscribedAll = unsubscribedAll;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static final class Builder {
        private String id;
        private String userId;
        private boolean emailNotificationsEnabled;
        private boolean paymentNotificationsEnabled;
        private boolean contestNotificationsEnabled;
        private boolean scoreUpdateNotificationsEnabled;
        private boolean leaderboardNotificationsEnabled;
        private boolean systemAlertNotificationsEnabled;
        private String preferredEmail;
        private String timezone;
        private boolean unsubscribedAll;
        private Instant createdAt;
        private Instant updatedAt;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder emailNotificationsEnabled(boolean emailNotificationsEnabled) {
            this.emailNotificationsEnabled = emailNotificationsEnabled;
            return this;
        }

        public Builder paymentNotificationsEnabled(boolean paymentNotificationsEnabled) {
            this.paymentNotificationsEnabled = paymentNotificationsEnabled;
            return this;
        }

        public Builder contestNotificationsEnabled(boolean contestNotificationsEnabled) {
            this.contestNotificationsEnabled = contestNotificationsEnabled;
            return this;
        }

        public Builder scoreUpdateNotificationsEnabled(boolean scoreUpdateNotificationsEnabled) {
            this.scoreUpdateNotificationsEnabled = scoreUpdateNotificationsEnabled;
            return this;
        }

        public Builder leaderboardNotificationsEnabled(boolean leaderboardNotificationsEnabled) {
            this.leaderboardNotificationsEnabled = leaderboardNotificationsEnabled;
            return this;
        }

        public Builder systemAlertNotificationsEnabled(boolean systemAlertNotificationsEnabled) {
            this.systemAlertNotificationsEnabled = systemAlertNotificationsEnabled;
            return this;
        }

        public Builder preferredEmail(String preferredEmail) {
            this.preferredEmail = preferredEmail;
            return this;
        }

        public Builder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public Builder unsubscribedAll(boolean unsubscribedAll) {
            this.unsubscribedAll = unsubscribedAll;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public NotificationPreference build() {
            return new NotificationPreference(id, userId, emailNotificationsEnabled,
                    paymentNotificationsEnabled, contestNotificationsEnabled,
                    scoreUpdateNotificationsEnabled, leaderboardNotificationsEnabled,
                    systemAlertNotificationsEnabled, preferredEmail, timezone,
                    unsubscribedAll, createdAt, updatedAt);
        }
    }
}
