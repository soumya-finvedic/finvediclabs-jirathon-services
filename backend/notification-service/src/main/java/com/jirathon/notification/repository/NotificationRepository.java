package com.jirathon.notification.repository;

import com.jirathon.notification.model.Notification;
import com.jirathon.notification.model.NotificationStatus;
import com.jirathon.notification.model.NotificationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.Instant;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, NotificationStatus status);
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, NotificationType type);
    
    List<Notification> findByEmailAndStatusOrderByCreatedAtDesc(String email, NotificationStatus status);
    
    @Query("{ 'userId': ?0, 'status': { $ne: ?1 } }")
    List<Notification> findByUserIdWithoutStatus(String userId, NotificationStatus status);
    
    long countByUserIdAndStatus(String userId, NotificationStatus status);
    
    long countByUserIdAndStatusAndType(String userId, NotificationStatus status, NotificationType type);
    
    List<Notification> findByRelatedEntityIdAndType(String entityId, NotificationType type);
    
    @Query("{ 'status': ?0, 'createdAt': { $lt: ?1 } }")
    List<Notification> findOldNotifications(NotificationStatus status, Instant before);
}
