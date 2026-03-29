package com.jirathon.contest.model;

import com.jirathon.contest.model.enums.ContestStatus;
import com.jirathon.contest.model.enums.ContestType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "contests")
@CompoundIndexes({
        @CompoundIndex(name = "tenant_slug_unique", def = "{'tenantId': 1, 'slug': 1}", unique = true),
        @CompoundIndex(name = "tenant_type_status", def = "{'tenantId': 1, 'contestType': 1, 'status': 1}"),
        @CompoundIndex(name = "tenant_created", def = "{'tenantId': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "tenant_start", def = "{'tenantId': 1, 'startTime': 1}")
})
public class Contest {

    @Id
    private String id;

    private String tenantId;
    private String title;
    private String slug;
    private String description;
    private List<String> rules;
    private ContestType contestType;

    private Set<String> supportedLanguages = new LinkedHashSet<>();

    private String bannerUrl;

    private Instant registrationDeadline;
    private double registrationFee = 0.0;
    private Instant startTime;
    private Instant endTime;

    private ContestStatus status = ContestStatus.DRAFT;

    private boolean deleted = false;

    private Instant deletedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    public Contest() {
    }

    public Contest(String id, String tenantId, String title, String slug, String description, List<String> rules,
                   ContestType contestType, Set<String> supportedLanguages, String bannerUrl,
                   Instant registrationDeadline, double registrationFee, Instant startTime, Instant endTime,
                   ContestStatus status, boolean deleted, Instant deletedAt, Instant createdAt, Instant updatedAt,
                   String createdBy, String updatedBy) {
        this.id = id;
        this.tenantId = tenantId;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.rules = rules;
        this.contestType = contestType;
        this.supportedLanguages = supportedLanguages != null ? supportedLanguages : new LinkedHashSet<>();
        this.bannerUrl = bannerUrl;
        this.registrationDeadline = registrationDeadline;
        this.registrationFee = registrationFee;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status != null ? status : ContestStatus.DRAFT;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
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
        private Set<String> supportedLanguages = new LinkedHashSet<>();
        private String bannerUrl;
        private Instant registrationDeadline;
        private double registrationFee = 0.0;
        private Instant startTime;
        private Instant endTime;
        private ContestStatus status = ContestStatus.DRAFT;
        private boolean deleted;
        private Instant deletedAt;
        private Instant createdAt;
        private Instant updatedAt;
        private String createdBy;
        private String updatedBy;

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
        public Builder deleted(boolean deleted) { this.deleted = deleted; return this; }
        public Builder deletedAt(Instant deletedAt) { this.deletedAt = deletedAt; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }

        public Contest build() {
            return new Contest(id, tenantId, title, slug, description, rules, contestType, supportedLanguages,
                    bannerUrl, registrationDeadline, registrationFee, startTime, endTime, status, deleted,
                    deletedAt, createdAt, updatedAt, createdBy, updatedBy);
        }
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public List<String> getRules() { return rules; }
    public ContestType getContestType() { return contestType; }
    public Set<String> getSupportedLanguages() { return supportedLanguages; }
    public String getBannerUrl() { return bannerUrl; }
    public Instant getRegistrationDeadline() { return registrationDeadline; }
    public double getRegistrationFee() { return registrationFee; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public ContestStatus getStatus() { return status; }
    public boolean isDeleted() { return deleted; }
    public Instant getDeletedAt() { return deletedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }

    public void setTitle(String title) { this.title = title; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setDescription(String description) { this.description = description; }
    public void setRules(List<String> rules) { this.rules = rules; }
    public void setContestType(ContestType contestType) { this.contestType = contestType; }
    public void setSupportedLanguages(Set<String> supportedLanguages) { this.supportedLanguages = supportedLanguages; }
    public void setRegistrationDeadline(Instant registrationDeadline) { this.registrationDeadline = registrationDeadline; }
    public void setRegistrationFee(double registrationFee) { this.registrationFee = registrationFee; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public void setStatus(ContestStatus status) { this.status = status; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
