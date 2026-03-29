package com.jirathon.org.model;

import com.jirathon.org.model.enums.OrgStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "organizations")
@CompoundIndexes({
        @CompoundIndex(name = "tenant_slug_unique", def = "{'tenantId': 1, 'slug': 1}", unique = true),
        @CompoundIndex(name = "tenant_name", def = "{'tenantId': 1, 'name': 1}"),
        @CompoundIndex(name = "tenant_status", def = "{'tenantId': 1, 'status': 1}"),
        @CompoundIndex(name = "tenant_created", def = "{'tenantId': 1, 'createdAt': -1}")
})
public class Organization {

    @Id
    private String id;
    private String tenantId;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private String website;
    private int memberCount;
    private Settings settings;
    private OrgStatus status = OrgStatus.ACTIVE;
    private boolean deleted = false;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private String createdBy;

    public Organization() {}

    public Organization(String id, String tenantId, String name, String slug, String description,
                        String logoUrl, String website, int memberCount, Settings settings,
                        OrgStatus status, boolean deleted, Instant createdAt, Instant updatedAt,
                        String createdBy) {
        this.id = id; this.tenantId = tenantId; this.name = name; this.slug = slug;
        this.description = description; this.logoUrl = logoUrl; this.website = website;
        this.memberCount = memberCount; this.settings = settings; this.status = status;
        this.deleted = deleted; this.createdAt = createdAt; this.updatedAt = updatedAt;
        this.createdBy = createdBy;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, tenantId, name, slug, description, logoUrl, website, createdBy;
        private int memberCount;
        private Settings settings;
        private OrgStatus status = OrgStatus.ACTIVE;
        private boolean deleted = false;
        private Instant createdAt, updatedAt;

        public Builder id(String v)          { this.id = v; return this; }
        public Builder tenantId(String v)    { this.tenantId = v; return this; }
        public Builder name(String v)        { this.name = v; return this; }
        public Builder slug(String v)        { this.slug = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder logoUrl(String v)     { this.logoUrl = v; return this; }
        public Builder website(String v)     { this.website = v; return this; }
        public Builder memberCount(int v)    { this.memberCount = v; return this; }
        public Builder settings(Settings v)  { this.settings = v; return this; }
        public Builder status(OrgStatus v)   { this.status = v; return this; }
        public Builder deleted(boolean v)    { this.deleted = v; return this; }
        public Builder createdAt(Instant v)  { this.createdAt = v; return this; }
        public Builder updatedAt(Instant v)  { this.updatedAt = v; return this; }
        public Builder createdBy(String v)   { this.createdBy = v; return this; }
        public Organization build() {
            return new Organization(id, tenantId, name, slug, description, logoUrl, website,
                    memberCount, settings, status, deleted, createdAt, updatedAt, createdBy);
        }
    }

    public String getId()            { return id; }
    public String getTenantId()      { return tenantId; }
    public String getName()          { return name; }
    public String getSlug()          { return slug; }
    public String getDescription()   { return description; }
    public String getLogoUrl()       { return logoUrl; }
    public String getWebsite()       { return website; }
    public int getMemberCount()      { return memberCount; }
    public Settings getSettings()    { return settings; }
    public OrgStatus getStatus()     { return status; }
    public boolean isDeleted()       { return deleted; }
    public Instant getCreatedAt()    { return createdAt; }
    public Instant getUpdatedAt()    { return updatedAt; }
    public String getCreatedBy()     { return createdBy; }

    public void setId(String id)                  { this.id = id; }
    public void setTenantId(String tenantId)      { this.tenantId = tenantId; }
    public void setName(String name)              { this.name = name; }
    public void setSlug(String slug)              { this.slug = slug; }
    public void setDescription(String description){ this.description = description; }
    public void setLogoUrl(String logoUrl)        { this.logoUrl = logoUrl; }
    public void setWebsite(String website)        { this.website = website; }
    public void setMemberCount(int memberCount)   { this.memberCount = memberCount; }
    public void setSettings(Settings settings)    { this.settings = settings; }
    public void setStatus(OrgStatus status)       { this.status = status; }
    public void setDeleted(boolean deleted)       { this.deleted = deleted; }
    public void setCreatedAt(Instant createdAt)   { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt)   { this.updatedAt = updatedAt; }
    public void setCreatedBy(String createdBy)    { this.createdBy = createdBy; }

    public static class Settings {
        private boolean autoApproveMembers = false;
        private String defaultRole = "MEMBER";
        private boolean allowPublicJoin = true;
        private int maxMembers = 500;

        public Settings() {}

        public Settings(boolean autoApproveMembers, String defaultRole,
                        boolean allowPublicJoin, int maxMembers) {
            this.autoApproveMembers = autoApproveMembers;
            this.defaultRole = defaultRole;
            this.allowPublicJoin = allowPublicJoin;
            this.maxMembers = maxMembers;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private boolean autoApproveMembers = false;
            private String defaultRole = "MEMBER";
            private boolean allowPublicJoin = true;
            private int maxMembers = 500;

            public Builder autoApproveMembers(boolean v) { this.autoApproveMembers = v; return this; }
            public Builder defaultRole(String v)         { this.defaultRole = v; return this; }
            public Builder allowPublicJoin(boolean v)    { this.allowPublicJoin = v; return this; }
            public Builder maxMembers(int v)             { this.maxMembers = v; return this; }
            public Settings build() { return new Settings(autoApproveMembers, defaultRole, allowPublicJoin, maxMembers); }
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
}
