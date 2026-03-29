package com.jirathon.org.repository;

import com.jirathon.org.model.Membership;
import com.jirathon.org.model.enums.MembershipStatus;
import com.jirathon.org.model.enums.OrgRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends MongoRepository<Membership, String> {

    Optional<Membership> findByTenantIdAndOrganizationIdAndUserId(String tenantId, String orgId, String userId);

    Page<Membership> findByTenantIdAndOrganizationIdAndStatus(String tenantId, String orgId,
                                                               MembershipStatus status, Pageable pageable);

    Page<Membership> findByTenantIdAndOrganizationIdAndStatusAndRole(String tenantId, String orgId,
                                                                      MembershipStatus status, OrgRole role,
                                                                      Pageable pageable);

    List<Membership> findByTenantIdAndUserIdAndStatus(String tenantId, String userId, MembershipStatus status);

    long countByTenantIdAndOrganizationIdAndStatus(String tenantId, String orgId, MembershipStatus status);

    boolean existsByTenantIdAndOrganizationIdAndUserIdAndStatus(String tenantId, String orgId,
                                                                  String userId, MembershipStatus status);

    List<Membership> findByTenantIdAndOrganizationIdAndRoleAndStatus(String tenantId, String orgId,
                                                                       OrgRole role, MembershipStatus status);
}
