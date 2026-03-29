package com.jirathon.user.repository;

import com.jirathon.user.model.User;
import com.jirathon.user.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    //Optional<User> findByIdAndTenantIdAndDeletedFalse(String id, String tenantId);
    @Query("{ '_id': ObjectId(?0), 'tenantId': ?1, 'deleted': false }")
    Optional<User> findByIdAndTenantIdAndDeletedFalse(String id, String tenantId);

    Optional<User> findByTenantIdAndEmailAndDeletedFalse(String tenantId, String email);

    Page<User> findByTenantIdAndDeletedFalse(String tenantId, Pageable pageable);

    Page<User> findByTenantIdAndStatusAndDeletedFalse(String tenantId, UserStatus status, Pageable pageable);

    Page<User> findByTenantIdAndOrganizationIdAndDeletedFalse(String tenantId, String organizationId, Pageable pageable);

    Page<User> findByTenantIdAndRolesContainingAndDeletedFalse(String tenantId, String role, Pageable pageable);

    List<User> findByTenantIdAndIdInAndDeletedFalse(String tenantId, List<String> ids);

    Page<User> findByTenantIdAndDisplayNameRegexAndDeletedFalse(String tenantId, String regex, Pageable pageable);

    long countByTenantIdAndOrganizationIdAndDeletedFalse(String tenantId, String organizationId);

    long countByTenantIdAndStatusAndDeletedFalse(String tenantId, UserStatus status);
}
