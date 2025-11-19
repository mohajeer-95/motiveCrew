package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateEventRequest;
import com.eska.motive.crew.ws.entity.Event;
import com.eska.motive.crew.ws.entity.EventParticipant;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.EventParticipantRepository;
import com.eska.motive.crew.ws.repository.EventRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing events
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all events with filters
     */
    public Page<Event> getAllEvents(Event.EventStatus status, Event.EventType type,
                                    Integer month, Integer year, String search, Pageable pageable) {
        return eventRepository.findByFilters(status, type, month, year, search, pageable);
    }

    /**
     * Get event by ID
     */
    public Event getEventById(Long id) throws ResourceNotFoundException {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
    }

    /**
     * Create a new event
     */
    @Transactional
    public Event createEvent(CreateEventRequest request, User currentUser)
            throws ValidationException, InternalErrorException {
        try {
            // Only admin can create events
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            Event.EventType eventType;
            try {
                eventType = Event.EventType.valueOf(request.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                eventType = Event.EventType.OTHER;
            }

            Event event = Event.builder()
                    .name(request.getName())
                    .type(eventType)
                    .description(request.getDescription())
                    .eventDate(request.getEventDate())
                    .eventTime(request.getEventTime())
                    .location(request.getLocation())
                    .address(request.getAddress())
                    .estimatedCost(request.getEstimatedCost())
                    .status(Event.EventStatus.UPCOMING)
                    .imageUrl(request.getImageUrl())
                    .createdBy(currentUser)
                    .build();

            return eventRepository.save(event);

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating event", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Update event
     */
    @Transactional
    public Event updateEvent(Long id, CreateEventRequest request, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can update events
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Update fields
            if (request.getName() != null) {
                event.setName(request.getName());
            }
            if (request.getType() != null) {
                try {
                    event.setType(Event.EventType.valueOf(request.getType().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Keep existing type
                }
            }
            if (request.getDescription() != null) {
                event.setDescription(request.getDescription());
            }
            if (request.getEventDate() != null) {
                event.setEventDate(request.getEventDate());
            }
            if (request.getEventTime() != null) {
                event.setEventTime(request.getEventTime());
            }
            if (request.getLocation() != null) {
                event.setLocation(request.getLocation());
            }
            if (request.getAddress() != null) {
                event.setAddress(request.getAddress());
            }
            if (request.getEstimatedCost() != null) {
                event.setEstimatedCost(request.getEstimatedCost());
            }
            if (request.getImageUrl() != null) {
                event.setImageUrl(request.getImageUrl());
            }

            return eventRepository.save(event);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating event", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Delete event
     */
    @Transactional
    public void deleteEvent(Long id, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can delete events
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Check if event has expenses
            if (event.getExpenses() != null && !event.getExpenses().isEmpty()) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            eventRepository.delete(event);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting event", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Join an event
     */
    @Transactional
    public EventParticipant joinEvent(Long eventId, User user)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Check if event is still upcoming
            if (event.getStatus() != Event.EventStatus.UPCOMING) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            // Check if already joined
            Optional<EventParticipant> existing = participantRepository.findByEventAndUser(event, user);
            if (existing.isPresent()) {
                EventParticipant participant = existing.get();
                if (participant.getStatus() == EventParticipant.ParticipantStatus.JOINED) {
                    throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
                }
                // Rejoin
                participant.setStatus(EventParticipant.ParticipantStatus.JOINED);
                return participantRepository.save(participant);
            }

            // Create new participation
            EventParticipant participant = EventParticipant.builder()
                    .event(event)
                    .user(user)
                    .status(EventParticipant.ParticipantStatus.JOINED)
                    .build();

            return participantRepository.save(participant);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error joining event", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Leave an event
     */
    @Transactional
    public void leaveEvent(Long eventId, User user)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            EventParticipant participant = participantRepository.findByEventAndUser(event, user)
                    .orElseThrow(() -> new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR));

            if (participant.getStatus() == EventParticipant.ParticipantStatus.LEFT) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            participant.setStatus(EventParticipant.ParticipantStatus.LEFT);
            participantRepository.save(participant);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error leaving event", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Get upcoming events
     */
    public List<Event> getUpcomingEvents(int limit) {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEventDateAfterAndStatusOrderByEventDateAsc(today, Event.EventStatus.UPCOMING)
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Get completed events
     */
    public Page<Event> getCompletedEvents(Integer month, Integer year, Pageable pageable) {
        return eventRepository.findByFilters(Event.EventStatus.COMPLETED, null, month, year, null, pageable);
    }

    /**
     * Update event status
     */
    @Transactional
    public Event updateEventStatus(Long id, Event.EventStatus status, java.math.BigDecimal actualCost, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can update status
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            event.setStatus(status);
            if (actualCost != null) {
                event.setActualCost(actualCost);
            }

            return eventRepository.save(event);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating event status", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Get participants count for an event
     */
    public long getParticipantsCount(Event event) {
        return participantRepository.countByEventAndStatus(event, EventParticipant.ParticipantStatus.JOINED);
    }
}

