package com.jirathon.auth.repository;

import com.jirathon.auth.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    void deleteAllByUserIdAndTenantId(String userId, String tenantId);

    long countByUserIdAndTenantIdAndRevokedFalse(String userId, String tenantId);
}
