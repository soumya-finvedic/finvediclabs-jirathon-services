package com.jirathon.org.service;

import com.jirathon.org.dto.*;
import com.jirathon.org.exception.ForbiddenException;
import com.jirathon.org.exception.ResourceNotFoundException;
import com.jirathon.org.model.Membership;
import com.jirathon.org.model.Organization;
import com.jirathon.org.model.enums.MembershipStatus;
import com.jirathon.org.model.enums.OrgRole;
import com.jirathon.org.model.enums.OrgStatus;
import com.jirathon.org.repository.MembershipRepository;
import com.jirathon.org.repository.OrganizationRepository;
import com.jirathon.org.security.TenantContext;
import com.jirathon.org.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;

    public OrganizationService(OrganizationRepository organizationRepository,
                               MembershipRepository membershipRepository) {
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
    }

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    // ── Organization CRUD ──────────────────────────────────────────

    @Transactional
    public OrgResponse createOrganization(CreateOrganizationRequest request, UserPrincipal principal) {
        String tenantId = TenantContext.getTenantId();
        String slug = generateSlug(request.getName(), tenantId);

        Organization.Settings settings = Organization.Settings.builder()
                .autoApproveMembers(request.getAutoApproveMembers() != null ? request.getAutoApproveMembers() : false)
                .allowPublicJoin(request.getAllowPublicJoin() != null ? request.getAllowPublicJoin() : true)
                .maxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 500)
                .defaultRole("MEMBER")
                .build();

        Organization org = Organization.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .website(request.getWebsite())
                .memberCount(1)
                .settings(settings)
                .status(OrgStatus.ACTIVE)
                .createdBy(principal.getId())
                .build();

        org = organizationRepository.save(org);

        // Creator becomes OWNER
        Membership ownerMembership = Membership.builder()
                .tenantId(tenantId)
                .organizationId(org.getId())
                .userId(principal.getId())
                .role(OrgRole.OWNER)
                .status(MembershipStatus.ACTIVE)
                .build();
        membershipRepository.save(ownerMembership);

        log.info("Organization created: {} by user: {} in tenant: {}", org.getId(), principal.getId(), tenantId);
        return OrgResponse.from(org);
    }

    public OrgResponse getOrganization(String orgId) {
        Organization org = findOrganization(orgId);
        return OrgResponse.from(org);
    }

    public OrgResponse getOrganizationBySlug(String slug) {
        String tenantId = TenantContext.getTenantId();
        Organization org = organizationRepository.findByTenantIdAndSlugAndDeletedFalse(tenantId, slug)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with slug: " + slug));
        return OrgResponse.from(org);
    }

    public OrgResponse updateOrganization(String orgId, UpdateOrganizationRequest request, UserPrincipal principal) {
        Organization org = findOrganization(orgId);
        ensureOrgAdmin(org.getId(), principal);

        if (request.getName() != null) {
            org.setName(request.getName());
            String newSlug = generateSlug(request.getName(), org.getTenantId());
            org.setSlug(newSlug);
        }
        if (request.getDescription() != null) org.setDescription(request.getDescription());
        if (request.getLogoUrl() != null) org.setLogoUrl(request.getLogoUrl());
        if (request.getWebsite() != null) org.setWebsite(request.getWebsite());

        if (org.getSettings() == null) {
            org.setSettings(Organization.Settings.builder().build());
        }
        if (request.getAutoApproveMembers() != null) {
            org.getSettings().setAutoApproveMembers(request.getAutoApproveMembers());
        }
        if (request.getAllowPublicJoin() != null) {
            org.getSettings().setAllowPublicJoin(request.getAllowPublicJoin());
        }
        if (request.getMaxMembers() != null) {
            org.getSettings().setMaxMembers(request.getMaxMembers());
        }

        org = organizationRepository.save(org);
        log.info("Organization updated: {} by user: {}", orgId, principal.getId());
        return OrgResponse.from(org);
    }

    @Transactional
    public void deleteOrganization(String orgId, UserPrincipal principal) {
        Organization org = findOrganization(orgId);
        ensureOrgOwner(org.getId(), principal);

        org.setDeleted(true);
        org.setStatus(OrgStatus.ARCHIVED);
        organizationRepository.save(org);
        log.info("Organization soft-deleted: {} by user: {}", orgId, principal.getId());
    }

    // ── Listing / Search ───────────────────────────────────────────

    public PagedResponse<OrgResponse> listOrganizations(int page, int size) {
        String tenantId = TenantContext.getTenantId();
        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 50);

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Organization> orgPage = organizationRepository.findByTenantIdAndDeletedFalse(tenantId, pageable);

        List<OrgResponse> content = orgPage.getContent().stream()
                .map(OrgResponse::from)
                .collect(Collectors.toList());

        return PagedResponse.of(content, page, size, orgPage.getTotalElements());
    }

    public PagedResponse<OrgResponse> searchOrganizations(String query, int page, int size) {
        String tenantId = TenantContext.getTenantId();
        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 50);

        String safePattern = "(?i)" + Pattern.quote(query);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Organization> orgPage = organizationRepository
                .findByTenantIdAndNameRegexAndDeletedFalse(tenantId, safePattern, pageable);

        List<OrgResponse> content = orgPage.getContent().stream()
                .map(OrgResponse::from)
                .collect(Collectors.toList());

        return PagedResponse.of(content, page, size, orgPage.getTotalElements());
    }

    public List<OrgResponse> getMyOrganizations(UserPrincipal principal) {
        String tenantId = TenantContext.getTenantId();
        List<Membership> memberships = membershipRepository
                .findByTenantIdAndUserIdAndStatus(tenantId, principal.getId(), MembershipStatus.ACTIVE);

        List<String> orgIds = memberships.stream()
                .map(Membership::getOrganizationId)
                .collect(Collectors.toList());

        if (orgIds.isEmpty()) {
            return List.of();
        }

        return organizationRepository.findByTenantIdAndIdIn(tenantId, orgIds).stream()
                .filter(o -> !o.isDeleted())
                .map(OrgResponse::from)
                .collect(Collectors.toList());
    }

    // ── Membership Management ──────────────────────────────────────

    @Transactional
    public MemberResponse joinOrganization(String orgId, UserPrincipal principal) {
        String tenantId = TenantContext.getTenantId();
        Organization org = findOrganization(orgId);

        if (!org.getSettings().isAllowPublicJoin()) {
            throw new ForbiddenException("This organization does not allow public joins");
        }

        // Check if already a member
        membershipRepository.findByTenantIdAndOrganizationIdAndUserId(tenantId, orgId, principal.getId())
                .ifPresent(m -> {
                    if (m.getStatus() == MembershipStatus.ACTIVE) {
                        throw new IllegalStateException("Already a member of this organization");
                    }
                    if (m.getStatus() == MembershipStatus.PENDING) {
                        throw new IllegalStateException("Join request already pending");
                    }
                });

        // Check max members
        long activeCount = membershipRepository.countByTenantIdAndOrganizationIdAndStatus(
                tenantId, orgId, MembershipStatus.ACTIVE);
        if (activeCount >= org.getSettings().getMaxMembers()) {
            throw new IllegalStateException("Organization has reached maximum member capacity");
        }

        MembershipStatus status = org.getSettings().isAutoApproveMembers()
                ? MembershipStatus.ACTIVE
                : MembershipStatus.PENDING;

        Membership membership = Membership.builder()
                .tenantId(tenantId)
                .organizationId(orgId)
                .userId(principal.getId())
                .role(OrgRole.valueOf(org.getSettings().getDefaultRole()))
                .status(status)
                .build();
        membership = membershipRepository.save(membership);

        if (status == MembershipStatus.ACTIVE) {
            org.setMemberCount(org.getMemberCount() + 1);
            organizationRepository.save(org);
        }

        log.info("User {} joined org {} with status {}", principal.getId(), orgId, status);
        return MemberResponse.from(membership);
    }

    @Transactional
    public void leaveOrganization(String orgId, UserPrincipal principal) {
        String tenantId = TenantContext.getTenantId();
        Membership membership = membershipRepository
                .findByTenantIdAndOrganizationIdAndUserId(tenantId, orgId, principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("You are not a member of this organization"));

        if (membership.getRole() == OrgRole.OWNER) {
            // Ensure there's another owner before allowing leave
            List<Membership> owners = membershipRepository
                    .findByTenantIdAndOrganizationIdAndRoleAndStatus(tenantId, orgId, OrgRole.OWNER, MembershipStatus.ACTIVE);
            if (owners.size() <= 1) {
                throw new ForbiddenException("Cannot leave organization as the sole owner. Transfer ownership first.");
            }
        }

        boolean wasActive = membership.getStatus() == MembershipStatus.ACTIVE;
        membership.setStatus(MembershipStatus.REMOVED);
        membershipRepository.save(membership);

        if (wasActive) {
            Organization org = findOrganization(orgId);
            org.setMemberCount(Math.max(0, org.getMemberCount() - 1));
            organizationRepository.save(org);
        }

        log.info("User {} left org {}", principal.getId(), orgId);
    }

    @Transactional
    public MemberResponse approveMember(String orgId, String userId, UserPrincipal principal) {
        ensureOrgAdmin(orgId, principal);
        String tenantId = TenantContext.getTenantId();

        Membership membership = membershipRepository
                .findByTenantIdAndOrganizationIdAndUserId(tenantId, orgId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        if (membership.getStatus() != MembershipStatus.PENDING) {
            throw new IllegalStateException("Membership is not in pending state");
        }

        membership.setStatus(MembershipStatus.ACTIVE);
        membership = membershipRepository.save(membership);

        Organization org = findOrganization(orgId);
        org.setMemberCount(org.getMemberCount() + 1);
        organizationRepository.save(org);

        log.info("User {} approved in org {} by {}", userId, orgId, principal.getId());
        return MemberResponse.from(membership);
    }

    @Transactional
    public void removeMember(String orgId, String userId, UserPrincipal principal) {
        ensureOrgAdmin(orgId, principal);
        String tenantId = TenantContext.getTenantId();

        Membership membership = membershipRepository
                .findByTenantIdAndOrganizationIdAndUserId(tenantId, orgId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        if (membership.getRole() == OrgRole.OWNER) {
            throw new ForbiddenException("Cannot remove an owner. Transfer ownership first.");
        }

        boolean wasActive = membership.getStatus() == MembershipStatus.ACTIVE;
        membership.setStatus(MembershipStatus.REMOVED);
        membershipRepository.save(membership);

        if (wasActive) {
            Organization org = findOrganization(orgId);
            org.setMemberCount(Math.max(0, org.getMemberCount() - 1));
            organizationRepository.save(org);
        }

        log.info("User {} removed from org {} by {}", userId, orgId, principal.getId());
    }

    @Transactional
    public MemberResponse updateMemberRole(String orgId, UpdateMemberRoleRequest request, UserPrincipal principal) {
        ensureOrgOwner(orgId, principal);
        String tenantId = TenantContext.getTenantId();

        Membership membership = membershipRepository
                .findByTenantIdAndOrganizationIdAndUserId(tenantId, orgId, request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("Can only change roles for active members");
        }

        membership.setRole(request.getRole());
        membership = membershipRepository.save(membership);

        log.info("User {} role updated to {} in org {} by {}", request.getUserId(), request.getRole(), orgId, principal.getId());
        return MemberResponse.from(membership);
    }

    public PagedResponse<MemberResponse> listMembers(String orgId, MembershipStatus status,
                                                       OrgRole role, int page, int size) {
        String tenantId = TenantContext.getTenantId();
        // Ensure org exists
        findOrganization(orgId);

        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 50);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "joinedAt"));

        Page<Membership> memberPage;
        if (role != null) {
            memberPage = membershipRepository.findByTenantIdAndOrganizationIdAndStatusAndRole(
                    tenantId, orgId, status != null ? status : MembershipStatus.ACTIVE, role, pageable);
        } else {
            memberPage = membershipRepository.findByTenantIdAndOrganizationIdAndStatus(
                    tenantId, orgId, status != null ? status : MembershipStatus.ACTIVE, pageable);
        }

        List<MemberResponse> content = memberPage.getContent().stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());

        return PagedResponse.of(content, page, size, memberPage.getTotalElements());
    }

    // ── Admin Operations ───────────────────────────────────────────

    public PagedResponse<OrgResponse> adminListOrganizations(OrgStatus status, int page, int size) {
        String tenantId = TenantContext.getTenantId();
        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 50);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Organization> orgPage;
        if (status != null) {
            orgPage = organizationRepository.findByTenantIdAndStatusAndDeletedFalse(tenantId, status, pageable);
        } else {
            orgPage = organizationRepository.findByTenantIdAndDeletedFalse(tenantId, pageable);
        }

        List<OrgResponse> content = orgPage.getContent().stream()
                .map(OrgResponse::from)
                .collect(Collectors.toList());

        return PagedResponse.of(content, page, size, orgPage.getTotalElements());
    }

    public OrgResponse adminUpdateStatus(String orgId, OrgStatus status) {
        String tenantId = TenantContext.getTenantId();
        Organization org = organizationRepository.findByTenantIdAndIdAndDeletedFalse(tenantId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        org.setStatus(status);
        org = organizationRepository.save(org);
        log.info("Organization {} status updated to {} by admin", orgId, status);
        return OrgResponse.from(org);
    }

    // ── Helpers ────────────────────────────────────────────────────

    private Organization findOrganization(String orgId) {
        String tenantId = TenantContext.getTenantId();
        return organizationRepository.findByTenantIdAndIdAndDeletedFalse(tenantId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
    }

    private void ensureOrgAdmin(String orgId, UserPrincipal principal) {
        // Platform admins bypass org-level checks
        if (principal.getRoles().contains("ADMIN") || principal.getRoles().contains("SUPER_ADMIN")) {
            return;
        }
        String tenantId = TenantContext.getTenantId();
        Membership membership = membershipRepository
                .findByTenantIdAndOrganizationIdAndUserId(tenantId, orgId, principal.getId())
                .orElseThrow(() -> new ForbiddenException("You are not a member of this organization"));

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new ForbiddenException("Your membership is not active");
        }
        if (membership.getRole() != OrgRole.OWNER && membership.getRole() != OrgRole.ADMIN) {
            throw new ForbiddenException("You must be an org admin or owner to perform this action");
        }
    }

    private void ensureOrgOwner(String orgId, UserPrincipal principal) {
        if (principal.getRoles().contains("SUPER_ADMIN")) {
            return;
        }
        String tenantId = TenantContext.getTenantId();
        Membership membership = membershipRepository
                .findByTenantIdAndOrganizationIdAndUserId(tenantId, orgId, principal.getId())
                .orElseThrow(() -> new ForbiddenException("You are not a member of this organization"));

        if (membership.getStatus() != MembershipStatus.ACTIVE || membership.getRole() != OrgRole.OWNER) {
            throw new ForbiddenException("Only the organization owner can perform this action");
        }
    }

    private String generateSlug(String name, String tenantId) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        String slug = WHITESPACE.matcher(normalized).replaceAll("-");
        slug = NON_LATIN.matcher(slug).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-").replaceAll("^-|-$", "");

        // Ensure uniqueness
        String baseSlug = slug;
        int counter = 1;
        while (organizationRepository.existsByTenantIdAndSlugAndDeletedFalse(tenantId, slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }
}
