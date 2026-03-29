package com.jirathon.auth.repository;

import com.jirathon.auth.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

    // Find by raw token (not hashed) and must not be used
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    void deleteAllByTenantIdAndUserId(String tenantId, String userId);
}
