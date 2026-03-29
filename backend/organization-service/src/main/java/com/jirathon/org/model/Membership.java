package com.jirathon.org.model;

import com.jirathon.org.model.enums.MembershipStatus;
import com.jirathon.org.model.enums.OrgRole;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "memberships")
@CompoundIndexes({
        @CompoundIndex(name = "tenant_org_user_unique", def = "{'tenantId': 1, 'organizationId': 1, 'userId': 1}", unique = true),
        @CompoundIndex(name = "tenant_user_status", def = "{'tenantId': 1, 'userId': 1, 'status': 1}"),
        @CompoundIndex(name = "tenant_org_status", def = "{'tenantId': 1, 'organizationId': 1, 'status': 1}"),
        @CompoundIndex(name = "tenant_org_role", def = "{'tenantId': 1, 'organizationId': 1, 'role': 1}")
})
public class Membership {

    @Id
    private String id;
    private String tenantId;
    private String organizationId;
    private String userId;
    private OrgRole role = OrgRole.MEMBER;
    private MembershipStatus status = MembershipStatus.PENDING;
    @CreatedDate
    private Instant joinedAt;
    @LastModifiedDate
    private Instant updatedAt;

    public Membership() {}

    public Membership(String id, String tenantId, String organizationId, String userId,
                      OrgRole role, MembershipStatus status, Instant joinedAt, Instant updatedAt) {
        this.id = id; this.tenantId = tenantId; this.organizationId = organizationId;
        this.userId = userId; this.role = role; this.status = status;
        this.joinedAt = joinedAt; this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, tenantId, organizationId, userId;
        private OrgRole role = OrgRole.MEMBER;
        private MembershipStatus status = MembershipStatus.PENDING;
        private Instant joinedAt, updatedAt;

        public Builder id(String v)              { this.id = v; return this; }
        public Builder tenantId(String v)        { this.tenantId = v; return this; }
        public Builder organizationId(String v)  { this.organizationId = v; return this; }
        public Builder userId(String v)          { this.userId = v; return this; }
        public Builder role(OrgRole v)           { this.role = v; return this; }
        public Builder status(MembershipStatus v){ this.status = v; return this; }
        public Builder joinedAt(Instant v)       { this.joinedAt = v; return this; }
        public Builder updatedAt(Instant v)      { this.updatedAt = v; return this; }
        public Membership build() {
            return new Membership(id, tenantId, organizationId, userId, role, status, joinedAt, updatedAt);
        }
    }

    public String getId()              { return id; }
    public String getTenantId()        { return tenantId; }
    public String getOrganizationId()  { return organizationId; }
    public String getUserId()          { return userId; }
    public OrgRole getRole()           { return role; }
    public MembershipStatus getStatus(){ return status; }
    public Instant getJoinedAt()       { return joinedAt; }
    public Instant getUpdatedAt()      { return updatedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setTenantId(String tenantId)          { this.tenantId = tenantId; }
    public void setOrganizationId(String oid)         { this.organizationId = oid; }
    public void setUserId(String userId)              { this.userId = userId; }
    public void setRole(OrgRole role)                 { this.role = role; }
    public void setStatus(MembershipStatus status)    { this.status = status; }
    public void setJoinedAt(Instant joinedAt)         { this.joinedAt = joinedAt; }
    public void setUpdatedAt(Instant updatedAt)       { this.updatedAt = updatedAt; }
}
