package com.jirathon.user.model;

import com.jirathon.user.model.enums.Role;
import com.jirathon.user.model.enums.UserStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "users")
@CompoundIndexes({
    @CompoundIndex(name = "tenant_email_unique", def = "{'tenantId': 1, 'email': 1}", unique = true),
    @CompoundIndex(name = "tenant_status_created", def = "{'tenantId': 1, 'status': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "tenant_roles", def = "{'tenantId': 1, 'roles': 1}"),
    @CompoundIndex(name = "tenant_org", def = "{'tenantId': 1, 'organizationId': 1, 'status': 1}"),
    @CompoundIndex(name = "tenant_displayname", def = "{'tenantId': 1, 'displayName': 1}"),
    @CompoundIndex(name = "tenant_score_rank", def = "{'tenantId': 1, 'stats.totalScore': -1}")
})
public class User {

    @Id
    @Field("_id")
    private String id;
    private String tenantId;
    private String email;
    private String displayName;
    private String avatarUrl;
    private Set<Role> roles = new HashSet<>(Set.of(Role.USER));
    private String organizationId;
    private Profile profile;
    private Stats stats;
    private UserStatus status = UserStatus.ACTIVE;
    private boolean emailVerified = false;
    private Instant lastLoginAt;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private boolean deleted = false;
    private Instant deletedAt;

    public User() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, tenantId, email, displayName, avatarUrl, organizationId, createdBy, updatedBy;
        private Set<Role> roles = new HashSet<>(Set.of(Role.USER));
        private Profile profile;
        private Stats stats;
        private UserStatus status = UserStatus.ACTIVE;
        private boolean emailVerified = false;
        private Instant lastLoginAt, createdAt, updatedAt, deletedAt;
        private boolean deleted = false;

        public Builder id(String v)             { this.id = v; return this; }
        public Builder tenantId(String v)       { this.tenantId = v; return this; }
        public Builder email(String v)          { this.email = v; return this; }
        public Builder displayName(String v)    { this.displayName = v; return this; }
        public Builder avatarUrl(String v)      { this.avatarUrl = v; return this; }
        public Builder roles(Set<Role> v)       { this.roles = v; return this; }
        public Builder organizationId(String v) { this.organizationId = v; return this; }
        public Builder profile(Profile v)       { this.profile = v; return this; }
        public Builder stats(Stats v)           { this.stats = v; return this; }
        public Builder status(UserStatus v)     { this.status = v; return this; }
        public Builder emailVerified(boolean v) { this.emailVerified = v; return this; }
        public Builder lastLoginAt(Instant v)   { this.lastLoginAt = v; return this; }
        public Builder createdAt(Instant v)     { this.createdAt = v; return this; }
        public Builder updatedAt(Instant v)     { this.updatedAt = v; return this; }
        public Builder createdBy(String v)      { this.createdBy = v; return this; }
        public Builder updatedBy(String v)      { this.updatedBy = v; return this; }
        public Builder deleted(boolean v)       { this.deleted = v; return this; }
        public Builder deletedAt(Instant v)     { this.deletedAt = v; return this; }

        public User build() {
            User u = new User();
            u.id = id; u.tenantId = tenantId; u.email = email; u.displayName = displayName;
            u.avatarUrl = avatarUrl; u.roles = roles; u.organizationId = organizationId;
            u.profile = profile; u.stats = stats; u.status = status;
            u.emailVerified = emailVerified; u.lastLoginAt = lastLoginAt;
            u.createdAt = createdAt; u.updatedAt = updatedAt;
            u.createdBy = createdBy; u.updatedBy = updatedBy;
            u.deleted = deleted; u.deletedAt = deletedAt;
            return u;
        }
    }

    public String getId()              { return id; }
    public String getTenantId()        { return tenantId; }
    public String getEmail()           { return email; }
    public String getDisplayName()     { return displayName; }
    public String getAvatarUrl()       { return avatarUrl; }
    public Set<Role> getRoles()        { return roles; }
    public String getOrganizationId()  { return organizationId; }
    public Profile getProfile()        { return profile; }
    public Stats getStats()            { return stats; }
    public UserStatus getStatus()      { return status; }
    public boolean isEmailVerified()   { return emailVerified; }
    public Instant getLastLoginAt()    { return lastLoginAt; }
    public Instant getCreatedAt()      { return createdAt; }
    public Instant getUpdatedAt()      { return updatedAt; }
    public String getCreatedBy()       { return createdBy; }
    public String getUpdatedBy()       { return updatedBy; }
    public boolean isDeleted()         { return deleted; }
    public Instant getDeletedAt()      { return deletedAt; }

    public void setId(String id)                    { this.id = id; }
    public void setTenantId(String tenantId)        { this.tenantId = tenantId; }
    public void setEmail(String email)              { this.email = email; }
    public void setDisplayName(String displayName)  { this.displayName = displayName; }
    public void setAvatarUrl(String avatarUrl)      { this.avatarUrl = avatarUrl; }
    public void setRoles(Set<Role> roles)           { this.roles = roles; }
    public void setOrganizationId(String oid)       { this.organizationId = oid; }
    public void setProfile(Profile profile)         { this.profile = profile; }
    public void setStats(Stats stats)               { this.stats = stats; }
    public void setStatus(UserStatus status)        { this.status = status; }
    public void setEmailVerified(boolean emailVerified){ this.emailVerified = emailVerified; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setCreatedAt(Instant createdAt)     { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt)     { this.updatedAt = updatedAt; }
    public void setCreatedBy(String createdBy)      { this.createdBy = createdBy; }
    public void setUpdatedBy(String updatedBy)      { this.updatedBy = updatedBy; }
    public void setDeleted(boolean deleted)         { this.deleted = deleted; }
    public void setDeletedAt(Instant deletedAt)     { this.deletedAt = deletedAt; }

    public static class Profile {
        private String phone;
        private String bio;
        private List<String> skills = new ArrayList<>();
        private String timezone;
        private String language;
        private String linkedinUrl;
        private String githubUrl;
        private String city;
        private String country;

        public Profile() {}

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String phone, bio, timezone, language, linkedinUrl, githubUrl, city, country;
            private List<String> skills = new ArrayList<>();
            public Builder phone(String v)       { this.phone = v; return this; }
            public Builder bio(String v)         { this.bio = v; return this; }
            public Builder skills(List<String> v){ this.skills = v; return this; }
            public Builder timezone(String v)    { this.timezone = v; return this; }
            public Builder language(String v)    { this.language = v; return this; }
            public Builder linkedinUrl(String v) { this.linkedinUrl = v; return this; }
            public Builder githubUrl(String v)   { this.githubUrl = v; return this; }
            public Builder city(String v)        { this.city = v; return this; }
            public Builder country(String v)     { this.country = v; return this; }
            public Profile build() {
                Profile p = new Profile();
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

        public void setPhone(String phone)          { this.phone = phone; }
        public void setBio(String bio)              { this.bio = bio; }
        public void setSkills(List<String> skills)  { this.skills = skills; }
        public void setTimezone(String timezone)    { this.timezone = timezone; }
        public void setLanguage(String language)    { this.language = language; }
        public void setLinkedinUrl(String linkedinUrl){ this.linkedinUrl = linkedinUrl; }
        public void setGithubUrl(String githubUrl)  { this.githubUrl = githubUrl; }
        public void setCity(String city)            { this.city = city; }
        public void setCountry(String country)      { this.country = country; }
    }

    public static class Stats {
        private int contestsParticipated = 0;
        private double totalScore = 0.0;
        private int bestRank = 0;
        private double avgAccuracy = 0.0;
        private int contestsWon = 0;
        private int totalSubmissions = 0;

        public Stats() {}

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private int contestsParticipated = 0, bestRank = 0, contestsWon = 0, totalSubmissions = 0;
            private double totalScore = 0.0, avgAccuracy = 0.0;
            public Builder contestsParticipated(int v){ this.contestsParticipated = v; return this; }
            public Builder totalScore(double v)       { this.totalScore = v; return this; }
            public Builder bestRank(int v)            { this.bestRank = v; return this; }
            public Builder avgAccuracy(double v)      { this.avgAccuracy = v; return this; }
            public Builder contestsWon(int v)         { this.contestsWon = v; return this; }
            public Builder totalSubmissions(int v)    { this.totalSubmissions = v; return this; }
            public Stats build() {
                Stats s = new Stats();
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
}
