package com.jirathon.auth.repository;

import com.jirathon.auth.model.OtpToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends MongoRepository<OtpToken, String> {

    Optional<OtpToken> findFirstByTenantIdAndEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(
            String tenantId, String email, String purpose
    );

    void deleteAllByTenantIdAndEmailAndPurpose(String tenantId, String email, String purpose);
}
