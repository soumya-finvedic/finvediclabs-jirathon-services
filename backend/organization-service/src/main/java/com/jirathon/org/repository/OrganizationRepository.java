package com.jirathon.org.repository;

import com.jirathon.org.model.Organization;
import com.jirathon.org.model.enums.OrgStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends MongoRepository<Organization, String> {

    Page<Organization> findByTenantIdAndDeletedFalse(String tenantId, Pageable pageable);

    Page<Organization> findByTenantIdAndStatusAndDeletedFalse(String tenantId, OrgStatus status, Pageable pageable);

    Optional<Organization> findByTenantIdAndIdAndDeletedFalse(String tenantId, String id);

    Optional<Organization> findByTenantIdAndSlugAndDeletedFalse(String tenantId, String slug);

    boolean existsByTenantIdAndSlugAndDeletedFalse(String tenantId, String slug);

    Page<Organization> findByTenantIdAndNameRegexAndDeletedFalse(String tenantId, String namePattern, Pageable pageable);

    long countByTenantIdAndDeletedFalse(String tenantId);

    long countByTenantIdAndStatusAndDeletedFalse(String tenantId, OrgStatus status);

    List<Organization> findByTenantIdAndIdIn(String tenantId, List<String> ids);
}
