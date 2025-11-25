package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Announcement entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByIsActiveTrueOrderByCreatedAtDesc();

    @Query("""
            select a from Announcement a
            where a.isActive = false
               or (a.expiresAt is not null and a.expiresAt < :now)
            order by a.createdAt desc
            """)
    List<Announcement> findArchivedAnnouncements(@Param("now") LocalDateTime now);

    @Query("""
            select a from Announcement a
            where a.isActive = true
              and (a.expiresAt is null or a.expiresAt >= :now)
              and (
                    a.audience = com.eska.motive.crew.ws.enums.AnnouncementAudience.COMPANY
                    or (a.audience = com.eska.motive.crew.ws.enums.AnnouncementAudience.TEAM
                        and :teamId is not null and a.team.id = :teamId)
                  )
              and not exists (
                    select 1 from UserAnnouncementView uav
                    where uav.user.id = :userId and uav.announcement.id = a.id
              )
            order by a.createdAt desc
            """)
    List<Announcement> findPendingAnnouncementsForUser(@Param("userId") Long userId,
                                                       @Param("teamId") Long teamId,
                                                       @Param("now") LocalDateTime now);
}

