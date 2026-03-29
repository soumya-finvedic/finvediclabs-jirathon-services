package com.jirathon.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String id, tenantId, email, displayName, avatarUrl, organizationId, organizationName, status;
    private Set<String> roles;
    private boolean emailVerified;
    private ProfileResponse profile;
    private StatsResponse stats;
    private Instant lastLoginAt;
    private Instant createdAt;

    public UserResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, tenantId, email, displayName, avatarUrl, organizationId, organizationName, status;
        private Set<String> roles;
        private boolean emailVerified;
        private ProfileResponse profile;
        private StatsResponse stats;
        private Instant lastLoginAt, createdAt;

        public Builder id(String v)                { this.id = v; return this; }
        public Builder tenantId(String v)          { this.tenantId = v; return this; }
        public Builder email(String v)             { this.email = v; return this; }
        public Builder displayName(String v)       { this.displayName = v; return this; }
        public Builder avatarUrl(String v)         { this.avatarUrl = v; return this; }
        public Builder roles(Set<String> v)        { this.roles = v; return this; }
        public Builder organizationId(String v)    { this.organizationId = v; return this; }
        public Builder organizationName(String v)  { this.organizationName = v; return this; }
        public Builder emailVerified(boolean v)    { this.emailVerified = v; return this; }
        public Builder status(String v)            { this.status = v; return this; }
        public Builder profile(ProfileResponse v)  { this.profile = v; return this; }
        public Builder stats(StatsResponse v)      { this.stats = v; return this; }
        public Builder lastLoginAt(Instant v)      { this.lastLoginAt = v; return this; }
        public Builder createdAt(Instant v)        { this.createdAt = v; return this; }

        public UserResponse build() {
            UserResponse r = new UserResponse();
            r.id = id; r.tenantId = tenantId; r.email = email; r.displayName = displayName;
            r.avatarUrl = avatarUrl; r.roles = roles; r.organizationId = organizationId;
            r.organizationName = organizationName; r.emailVerified = emailVerified;
            r.status = status; r.profile = profile; r.stats = stats;
            r.lastLoginAt = lastLoginAt; r.createdAt = createdAt;
            return r;
        }
    }

    public static class ProfileResponse {
        private String phone, bio, timezone, language, linkedinUrl, githubUrl, city, country;
        private List<String> skills;

        public ProfileResponse() {}

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String phone, bio, timezone, language, linkedinUrl, githubUrl, city, country;
            private List<String> skills;
            public Builder phone(String v)       { this.phone = v; return this; }
            public Builder bio(String v)         { this.bio = v; return this; }
            public Builder skills(List<String> v){ this.skills = v; return this; }
            public Builder timezone(String v)    { this.timezone = v; return this; }
            public Builder language(String v)    { this.language = v; return this; }
            public Builder linkedinUrl(String v) { this.linkedinUrl = v; return this; }
            public Builder githubUrl(String v)   { this.githubUrl = v; return this; }
            public Builder city(String v)        { this.city = v; return this; }
            public Builder country(String v)     { this.country = v; return this; }
            public ProfileResponse build() {
                ProfileResponse p = new ProfileResponse();
                p.phone = phone; p.bio = bio; p.skills = skills; p.timezone = timezone;
                p.language = language; p.linkedinUrl = linkedinUrl; p.githubUrl = githubUrl;
                p.city = city; p.country = country;
                return p;
            }
        }

        public String getPhone()       { return phone; }
        public String getBio()         { return bio; }
        public List<String> getSkills(){ return skills; }
        public String getTimezone()    { return timezone; }
        public String getLanguage()    { return language; }
        public String getLinkedinUrl() { return linkedinUrl; }
        public String getGithubUrl()   { return githubUrl; }
        public String getCity()        { return city; }
        public String getCountry()     { return country; }
        public void setPhone(String v)          { this.phone = v; }
        public void setBio(String v)            { this.bio = v; }
        public void setSkills(List<String> v)   { this.skills = v; }
        public void setTimezone(String v)       { this.timezone = v; }
        public void setLanguage(String v)       { this.language = v; }
        public void setLinkedinUrl(String v)    { this.linkedinUrl = v; }
        public void setGithubUrl(String v)      { this.githubUrl = v; }
        public void setCity(String v)           { this.city = v; }
        public void setCountry(String v)        { this.country = v; }
    }

    public static class StatsResponse {
        private int contestsParticipated, bestRank, contestsWon, totalSubmissions;
        private double totalScore, avgAccuracy;

        public StatsResponse() {}

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private int contestsParticipated, bestRank, contestsWon, totalSubmissions;
            private double totalScore, avgAccuracy;
            public Builder contestsParticipated(int v){ this.contestsParticipated = v; return this; }
            public Builder totalScore(double v)       { this.totalScore = v; return this; }
            public Builder bestRank(int v)            { this.bestRank = v; return this; }
            public Builder avgAccuracy(double v)      { this.avgAccuracy = v; return this; }
            public Builder contestsWon(int v)         { this.contestsWon = v; return this; }
            public Builder totalSubmissions(int v)    { this.totalSubmissions = v; return this; }
            public StatsResponse build() {
                StatsResponse s = new StatsResponse();
                s.contestsParticipated = contestsParticipated; s.totalScore = totalScore;
                s.bestRank = bestRank; s.avgAccuracy = avgAccuracy;
                s.contestsWon = contestsWon; s.totalSubmissions = totalSubmissions;
                return s;
            }
        }

        public int getContestsParticipated()  { return contestsParticipated; }
        public double getTotalScore()         { return totalScore; }
        public int getBestRank()              { return bestRank; }
        public double getAvgAccuracy()        { return avgAccuracy; }
        public int getContestsWon()           { return contestsWon; }
        public int getTotalSubmissions()      { return totalSubmissions; }
        public void setContestsParticipated(int v)  { this.contestsParticipated = v; }
        public void setTotalScore(double v)         { this.totalScore = v; }
        public void setBestRank(int v)              { this.bestRank = v; }
        public void setAvgAccuracy(double v)        { this.avgAccuracy = v; }
        public void setContestsWon(int v)           { this.contestsWon = v; }
        public void setTotalSubmissions(int v)      { this.totalSubmissions = v; }
    }

    public String getId()                   { return id; }
    public String getTenantId()             { return tenantId; }
    public String getEmail()                { return email; }
    public String getDisplayName()          { return displayName; }
    public String getAvatarUrl()            { return avatarUrl; }
    public Set<String> getRoles()           { return roles; }
    public String getOrganizationId()       { return organizationId; }
    public String getOrganizationName()     { return organizationName; }
    public boolean isEmailVerified()        { return emailVerified; }
    public String getStatus()               { return status; }
    public ProfileResponse getProfile()     { return profile; }
    public StatsResponse getStats()         { return stats; }
    public Instant getLastLoginAt()         { return lastLoginAt; }
    public Instant getCreatedAt()           { return createdAt; }
    public void setId(String id)                   { this.id = id; }
    public void setTenantId(String tenantId)       { this.tenantId = tenantId; }
    public void setEmail(String email)             { this.email = email; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setAvatarUrl(String avatarUrl)     { this.avatarUrl = avatarUrl; }
    public void setRoles(Set<String> roles)        { this.roles = roles; }
    public void setOrganizationId(String oid)      { this.organizationId = oid; }
    public void setOrganizationName(String n)      { this.organizationName = n; }
    public void setEmailVerified(boolean v)        { this.emailVerified = v; }
    public void setStatus(String status)           { this.status = status; }
    public void setProfile(ProfileResponse profile){ this.profile = profile; }
    public void setStats(StatsResponse stats)      { this.stats = stats; }
    public void setLastLoginAt(Instant lastLoginAt){ this.lastLoginAt = lastLoginAt; }
    public void setCreatedAt(Instant createdAt)    { this.createdAt = createdAt; }
}
