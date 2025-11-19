package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Announcement entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByIsActiveTrueOrderByCreatedAtDesc();
}

