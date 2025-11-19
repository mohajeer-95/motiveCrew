package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Event entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:type IS NULL OR e.type = :type) AND " +
           "(:month IS NULL OR MONTH(e.eventDate) = :month) AND " +
           "(:year IS NULL OR YEAR(e.eventDate) = :year) AND " +
           "(:search IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Event> findByFilters(
            @Param("status") Event.EventStatus status,
            @Param("type") Event.EventType type,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("search") String search,
            Pageable pageable
    );

    List<Event> findByEventDateAfterAndStatusOrderByEventDateAsc(LocalDate date, Event.EventStatus status);

    List<Event> findByEventDateBeforeAndStatusOrderByEventDateDesc(LocalDate date, Event.EventStatus status);

    List<Event> findByStatusOrderByEventDateDesc(Event.EventStatus status);
}

