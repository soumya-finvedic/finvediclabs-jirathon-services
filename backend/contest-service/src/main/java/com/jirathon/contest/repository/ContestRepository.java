package com.jirathon.contest.repository;

import com.jirathon.contest.model.Contest;
import com.jirathon.contest.model.enums.ContestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContestRepository extends MongoRepository<Contest, String> {

    Optional<Contest> findByIdAndTenantIdAndDeletedFalse(String id, String tenantId);

    Optional<Contest> findByTenantIdAndSlugAndDeletedFalse(String tenantId, String slug);

    Page<Contest> findByTenantIdAndDeletedFalse(String tenantId, Pageable pageable);

    Page<Contest> findByTenantIdAndContestTypeAndDeletedFalse(String tenantId, ContestType contestType, Pageable pageable);
}
