package com.jirathon.auth.repository;

import com.jirathon.auth.model.User;
import com.jirathon.auth.model.enums.UserStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByTenantIdAndEmailAndDeletedFalse(String tenantId, String email);

    Optional<User> findByTenantIdAndOauthProviderAndOauthProviderIdAndDeletedFalse(
            String tenantId,
            com.jirathon.auth.model.enums.OAuthProvider oauthProvider,
            String oauthProviderId
    );

    Optional<User> findByIdAndTenantIdAndDeletedFalse(String id, String tenantId);

    boolean existsByTenantIdAndEmailAndDeletedFalse(String tenantId, String email);

    boolean existsByTenantIdAndUsernameAndDeletedFalse(String tenantId, String username);

    long countByTenantIdAndStatusAndDeletedFalse(String tenantId, UserStatus status);
}
