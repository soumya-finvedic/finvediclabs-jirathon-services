package com.jirathon.contest.repository;

import com.jirathon.contest.model.ContestRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ContestRegistrationRepository extends MongoRepository<ContestRegistration, String> {

    Optional<ContestRegistration> findByIdAndTenantId(String id, String tenantId);

    Optional<ContestRegistration> findByTenantIdAndContestIdAndUserId(String tenantId, String contestId, String userId);
}