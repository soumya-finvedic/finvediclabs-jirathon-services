package com.jirathon.notification.dto.request;

import jakarta.validation.constraints.NotBlank;

public class NotificationPreferenceRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    private boolean emailNotificationsEnabled = true;

    private boolean paymentNotificationsEnabled = true;

    private boolean contestNotificationsEnabled = true;

    private boolean scoreUpdateNotificationsEnabled = true;

    private boolean leaderboardNotificationsEnabled = true;

    private boolean systemAlertNotificationsEnabled = true;

    private String preferredEmail;

    private String timezone;

    private boolean unsubscribedAll;

    public NotificationPreferenceRequest() {
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
}
