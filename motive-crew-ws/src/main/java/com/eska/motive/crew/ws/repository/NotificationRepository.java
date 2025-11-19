package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Notification;
import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Notification entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user = :user AND " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:isRead IS NULL OR n.isRead = :isRead) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserAndFilters(
            @Param("user") User user,
            @Param("type") Notification.NotificationType type,
            @Param("isRead") Boolean isRead,
            Pageable pageable
    );

    long countByUserAndIsReadFalse(User user);

    List<Notification> findByUserAndIsReadFalse(User user);

    void deleteByUser(User user);
}

