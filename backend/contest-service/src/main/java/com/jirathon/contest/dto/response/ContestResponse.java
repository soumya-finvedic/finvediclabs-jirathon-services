package com.jirathon.contest.dto.response;

import com.jirathon.contest.model.enums.ContestStatus;
import com.jirathon.contest.model.enums.ContestType;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class ContestResponse {

    private String id;
    private String tenantId;
    private String title;
    private String slug;
    private String description;
    private List<String> rules;
    private ContestType contestType;
    private Set<String> supportedLanguages;
    private String bannerUrl;
    private Instant registrationDeadline;
    private double registrationFee;
    private Instant startTime;
    private Instant endTime;
    private ContestStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public ContestResponse() {
    }

    public ContestResponse(
            String id,
            String tenantId,
            String title,
            String slug,
            String description,
            List<String> rules,
            ContestType contestType,
            Set<String> supportedLanguages,
            String bannerUrl,
            Instant registrationDeadline,
            double registrationFee,
            Instant startTime,
            Instant endTime,
            ContestStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.rules = rules;
        this.contestType = contestType;
        this.supportedLanguages = supportedLanguages;
        this.bannerUrl = bannerUrl;
        this.registrationDeadline = registrationDeadline;
        this.registrationFee = registrationFee;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String tenantId;
        private String title;
        private String slug;
        private String description;
        private List<String> rules;
        private ContestType contestType;
        private Set<String> supportedLanguages;
        private String bannerUrl;
        private Instant registrationDeadline;
        private double registrationFee;
        private Instant startTime;
        private Instant endTime;
        private ContestStatus status;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder rules(List<String> rules) { this.rules = rules; return this; }
        public Builder contestType(ContestType contestType) { this.contestType = contestType; return this; }
        public Builder supportedLanguages(Set<String> supportedLanguages) { this.supportedLanguages = supportedLanguages; return this; }
        public Builder bannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; return this; }
        public Builder registrationDeadline(Instant registrationDeadline) { this.registrationDeadline = registrationDeadline; return this; }
        public Builder registrationFee(double registrationFee) { this.registrationFee = registrationFee; return this; }
        public Builder startTime(Instant startTime) { this.startTime = startTime; return this; }
        public Builder endTime(Instant endTime) { this.endTime = endTime; return this; }
        public Builder status(ContestStatus status) { this.status = status; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public ContestResponse build() {
            return new ContestResponse(id, tenantId, title, slug, description, rules, contestType,
                    supportedLanguages, bannerUrl, registrationDeadline, registrationFee,
                    startTime, endTime, status, createdAt, updatedAt);
        }
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRules() {
        return rules;
    }

    public ContestType getContestType() {
        return contestType;
    }

    public Set<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public Instant getRegistrationDeadline() {
        return registrationDeadline;
    }

    public double getRegistrationFee() {
        return registrationFee;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public ContestStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
