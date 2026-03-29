package com.jirathon.org.dto;

import com.jirathon.org.model.Membership;
import com.jirathon.org.model.enums.MembershipStatus;
import com.jirathon.org.model.enums.OrgRole;

import java.time.Instant;

public class MemberResponse {

    private String id;
    private String organizationId;
    private String userId;
    private OrgRole role;
    private MembershipStatus status;
    private Instant joinedAt;

    public MemberResponse() {}

    public MemberResponse(String id, String organizationId, String userId,
                          OrgRole role, MembershipStatus status, Instant joinedAt) {
        this.id = id; this.organizationId = organizationId; this.userId = userId;
        this.role = role; this.status = status; this.joinedAt = joinedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, organizationId, userId;
        private OrgRole role;
        private MembershipStatus status;
        private Instant joinedAt;
        public Builder id(String v)              { this.id = v; return this; }
        public Builder organizationId(String v)  { this.organizationId = v; return this; }
        public Builder userId(String v)          { this.userId = v; return this; }
        public Builder role(OrgRole v)           { this.role = v; return this; }
        public Builder status(MembershipStatus v){ this.status = v; return this; }
        public Builder joinedAt(Instant v)       { this.joinedAt = v; return this; }
        public MemberResponse build() { return new MemberResponse(id, organizationId, userId, role, status, joinedAt); }
    }

    public static MemberResponse from(Membership membership) {
        return MemberResponse.builder()
                .id(membership.getId())
                .organizationId(membership.getOrganizationId())
                .userId(membership.getUserId())
                .role(membership.getRole())
                .status(membership.getStatus())
                .joinedAt(membership.getJoinedAt())
                .build();
    }

    public String getId()              { return id; }
    public String getOrganizationId()  { return organizationId; }
    public String getUserId()          { return userId; }
    public OrgRole getRole()           { return role; }
    public MembershipStatus getStatus(){ return status; }
    public Instant getJoinedAt()       { return joinedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setOrganizationId(String oid)         { this.organizationId = oid; }
    public void setUserId(String userId)              { this.userId = userId; }
    public void setRole(OrgRole role)                 { this.role = role; }
    public void setStatus(MembershipStatus status)    { this.status = status; }
    public void setJoinedAt(Instant joinedAt)         { this.joinedAt = joinedAt; }
}
