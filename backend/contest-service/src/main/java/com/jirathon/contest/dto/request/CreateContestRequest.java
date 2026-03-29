package com.jirathon.contest.dto.request;

import com.jirathon.contest.model.enums.ContestType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class CreateContestRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 120, message = "Title must be at most 120 characters")
    private String title;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase and hyphen-separated")
    @Size(max = 80, message = "Slug must be at most 80 characters")
    private String slug;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    @NotEmpty(message = "Rules are required")
    @Size(max = 100, message = "Rules must contain at most 100 entries")
    private List<@NotBlank(message = "Rule cannot be blank") @Size(max = 300, message = "Each rule must be at most 300 characters") String> rules;

    @NotNull(message = "Contest type is required")
    private ContestType contestType;

    @NotEmpty(message = "At least one supported language is required")
    private Set<@NotBlank(message = "Language cannot be blank") @Pattern(regexp = "^[a-zA-Z0-9+#_.-]{1,30}$", message = "Language has invalid format") String> supportedLanguages;

    @NotNull(message = "Registration deadline is required")
    @Future(message = "Registration deadline must be in the future")
    private Instant registrationDeadline;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private Instant startTime;

    @NotNull(message = "Registration fee is required")
    @PositiveOrZero(message = "Registration fee cannot be negative")
    private Double registrationFee;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private Instant endTime;

    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public List<String> getRules() { return rules; }
    public ContestType getContestType() { return contestType; }
    public Set<String> getSupportedLanguages() { return supportedLanguages; }
    public Instant getRegistrationDeadline() { return registrationDeadline; }
    public Instant getStartTime() { return startTime; }
    public Double getRegistrationFee() { return registrationFee; }
    public Instant getEndTime() { return endTime; }

    public void setTitle(String title) { this.title = title; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setDescription(String description) { this.description = description; }
    public void setRules(List<String> rules) { this.rules = rules; }
    public void setContestType(ContestType contestType) { this.contestType = contestType; }
    public void setSupportedLanguages(Set<String> supportedLanguages) { this.supportedLanguages = supportedLanguages; }
    public void setRegistrationDeadline(Instant registrationDeadline) { this.registrationDeadline = registrationDeadline; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public void setRegistrationFee(Double registrationFee) { this.registrationFee = registrationFee; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
}
