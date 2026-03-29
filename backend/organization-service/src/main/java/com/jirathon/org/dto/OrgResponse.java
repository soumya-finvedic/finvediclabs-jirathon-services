package com.jirathon.org.dto;

import com.jirathon.org.model.Organization;
import com.jirathon.org.model.enums.OrgStatus;

import java.time.Instant;

public class OrgResponse {

    private String id, name, slug, description, logoUrl, website, createdBy;
    private int memberCount;
    private OrgStatus status;
    private SettingsResponse settings;
    private Instant createdAt;

    public OrgResponse() {}

    public OrgResponse(String id, String name, String slug, String description, String logoUrl,
                       String website, int memberCount, OrgStatus status, SettingsResponse settings,
                       Instant createdAt, String createdBy) {
        this.id = id; this.name = name; this.slug = slug; this.description = description;
        this.logoUrl = logoUrl; this.website = website; this.memberCount = memberCount;
        this.status = status; this.settings = settings; this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, name, slug, description, logoUrl, website, createdBy;
        private int memberCount;
        private OrgStatus status;
        private SettingsResponse settings;
        private Instant createdAt;
        public Builder id(String v)               { this.id = v; return this; }
        public Builder name(String v)             { this.name = v; return this; }
        public Builder slug(String v)             { this.slug = v; return this; }
        public Builder description(String v)      { this.description = v; return this; }
        public Builder logoUrl(String v)          { this.logoUrl = v; return this; }
        public Builder website(String v)          { this.website = v; return this; }
        public Builder memberCount(int v)         { this.memberCount = v; return this; }
        public Builder status(OrgStatus v)        { this.status = v; return this; }
        public Builder settings(SettingsResponse v){ this.settings = v; return this; }
        public Builder createdAt(Instant v)       { this.createdAt = v; return this; }
        public Builder createdBy(String v)        { this.createdBy = v; return this; }
        public OrgResponse build() {
            return new OrgResponse(id, name, slug, description, logoUrl, website,
                    memberCount, status, settings, createdAt, createdBy);
        }
    }

    public static class SettingsResponse {
        private boolean autoApproveMembers;
        private String defaultRole;
        private boolean allowPublicJoin;
        private int maxMembers;

        public SettingsResponse() {}

        public SettingsResponse(boolean autoApproveMembers, String defaultRole,
                                boolean allowPublicJoin, int maxMembers) {
            this.autoApproveMembers = autoApproveMembers; this.defaultRole = defaultRole;
            this.allowPublicJoin = allowPublicJoin; this.maxMembers = maxMembers;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private boolean autoApproveMembers;
            private String defaultRole;
            private boolean allowPublicJoin;
            private int maxMembers;
            public Builder autoApproveMembers(boolean v) { this.autoApproveMembers = v; return this; }
            public Builder defaultRole(String v)         { this.defaultRole = v; return this; }
            public Builder allowPublicJoin(boolean v)    { this.allowPublicJoin = v; return this; }
            public Builder maxMembers(int v)             { this.maxMembers = v; return this; }
            public SettingsResponse build() { return new SettingsResponse(autoApproveMembers, defaultRole, allowPublicJoin, maxMembers); }
        }

        public boolean isAutoApproveMembers() { return autoApproveMembers; }
        public String getDefaultRole()        { return defaultRole; }
        public boolean isAllowPublicJoin()    { return allowPublicJoin; }
        public int getMaxMembers()            { return maxMembers; }
        public void setAutoApproveMembers(boolean v) { this.autoApproveMembers = v; }
        public void setDefaultRole(String v)         { this.defaultRole = v; }
        public void setAllowPublicJoin(boolean v)    { this.allowPublicJoin = v; }
        public void setMaxMembers(int v)             { this.maxMembers = v; }
    }

    public static OrgResponse from(Organization org) {
        SettingsResponse settingsResp = null;
        if (org.getSettings() != null) {
            settingsResp = SettingsResponse.builder()
                    .autoApproveMembers(org.getSettings().isAutoApproveMembers())
                    .defaultRole(org.getSettings().getDefaultRole())
                    .allowPublicJoin(org.getSettings().isAllowPublicJoin())
                    .maxMembers(org.getSettings().getMaxMembers())
                    .build();
        }
        return OrgResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .slug(org.getSlug())
                .description(org.getDescription())
                .logoUrl(org.getLogoUrl())
                .website(org.getWebsite())
                .memberCount(org.getMemberCount())
                .status(org.getStatus())
                .settings(settingsResp)
                .createdAt(org.getCreatedAt())
                .createdBy(org.getCreatedBy())
                .build();
    }

    public String getId()             { return id; }
    public String getName()           { return name; }
    public String getSlug()           { return slug; }
    public String getDescription()    { return description; }
    public String getLogoUrl()        { return logoUrl; }
    public String getWebsite()        { return website; }
    public int getMemberCount()       { return memberCount; }
    public OrgStatus getStatus()      { return status; }
    public SettingsResponse getSettings(){ return settings; }
    public Instant getCreatedAt()     { return createdAt; }
    public String getCreatedBy()      { return createdBy; }

    public void setId(String id)                   { this.id = id; }
    public void setName(String name)               { this.name = name; }
    public void setSlug(String slug)               { this.slug = slug; }
    public void setDescription(String description) { this.description = description; }
    public void setLogoUrl(String logoUrl)         { this.logoUrl = logoUrl; }
    public void setWebsite(String website)         { this.website = website; }
    public void setMemberCount(int memberCount)    { this.memberCount = memberCount; }
    public void setStatus(OrgStatus status)        { this.status = status; }
    public void setSettings(SettingsResponse s)    { this.settings = s; }
    public void setCreatedAt(Instant createdAt)    { this.createdAt = createdAt; }
    public void setCreatedBy(String createdBy)     { this.createdBy = createdBy; }
}
