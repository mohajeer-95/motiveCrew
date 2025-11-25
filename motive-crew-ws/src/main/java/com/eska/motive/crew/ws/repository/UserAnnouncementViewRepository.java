package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.UserAnnouncementView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for tracking seen announcements per user.
 */
@Repository
public interface UserAnnouncementViewRepository extends JpaRepository<UserAnnouncementView, Long> {

    boolean existsByUser_IdAndAnnouncement_Id(Long userId, Long announcementId);
}


