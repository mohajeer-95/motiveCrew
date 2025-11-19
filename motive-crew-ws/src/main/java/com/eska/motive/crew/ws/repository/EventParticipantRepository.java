package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.Event;
import com.eska.motive.crew.ws.entity.EventParticipant;
import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for EventParticipant entity
 * 
 * @author Motive Crew Team
 */
@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    Optional<EventParticipant> findByEventAndUser(Event event, User user);

    List<EventParticipant> findByEvent(Event event);

    List<EventParticipant> findByUser(User user);

    long countByEventAndStatus(Event event, EventParticipant.ParticipantStatus status);
}

