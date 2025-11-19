package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateEventRequest;
import com.eska.motive.crew.ws.entity.Event;
import com.eska.motive.crew.ws.entity.EventParticipant;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.EventService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event management controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/events")
@Log4j2
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private AuthService authService;

    /**
     * Get all events
     * GET /api/v1/events
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEvents(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Event.EventStatus eventStatus = null;
            Event.EventType eventType = null;
            
            if (status != null) {
                try {
                    eventStatus = Event.EventStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Invalid status
                }
            }
            
            if (type != null) {
                try {
                    eventType = Event.EventType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Invalid type
                }
            }
            
            Page<Event> events = eventService.getAllEvents(eventStatus, eventType, month, year, search, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Events retrieved successfully");
            response.put("error", false);
            response.put("data", events.stream()
                    .map(this::buildEventResponse)
                    .toList());
            response.put("totalElements", events.getTotalElements());
            response.put("totalPages", events.getTotalPages());
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (Exception e) {
            log.error("Error getting events", e);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.INTERNAL_ERROR.getCode());
            response.put("message", "Error retrieving events: " + e.getMessage());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get event by ID
     * GET /api/v1/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEventById(@PathVariable Long id)
            throws ResourceNotFoundException {
        Event event = eventService.getEventById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Event retrieved successfully");
        response.put("error", false);
        response.put("data", buildEventResponse(event));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Create event
     * POST /api/v1/events
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEvent(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateEventRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        Event event = eventService.createEvent(request, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Event created successfully");
        response.put("error", false);
        response.put("data", buildEventResponse(event));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update event
     * PUT /api/v1/events/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody CreateEventRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        Event event = eventService.updateEvent(id, request, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Event updated successfully");
        response.put("error", false);
        response.put("data", buildEventResponse(event));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Delete event
     * DELETE /api/v1/events/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        eventService.deleteEvent(id, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Event deleted successfully");
        response.put("error", false);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Join event
     * POST /api/v1/events/{id}/join
     */
    @PostMapping("/{id}/join")
    public ResponseEntity<Map<String, Object>> joinEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User user = getCurrentUser(token);
        EventParticipant participant = eventService.joinEvent(id, user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Joined event successfully");
        response.put("error", false);
        response.put("data", buildParticipantResponse(participant));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Leave event
     * POST /api/v1/events/{id}/leave
     */
    @PostMapping("/{id}/leave")
    public ResponseEntity<Map<String, Object>> leaveEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User user = getCurrentUser(token);
        eventService.leaveEvent(id, user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Left event successfully");
        response.put("error", false);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get upcoming events
     * GET /api/v1/events/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingEvents(
            @RequestParam(defaultValue = "5") int limit) {
        List<Event> events = eventService.getUpcomingEvents(limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Upcoming events retrieved");
        response.put("error", false);
        response.put("data", events.stream()
                .map(this::buildEventResponse)
                .toList());
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Update event status
     * PUT /api/v1/events/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateEventStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) BigDecimal actualCost)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        Event.EventStatus eventStatus = Event.EventStatus.valueOf(status.toUpperCase());
        Event event = eventService.updateEventStatus(id, eventStatus, actualCost, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Event status updated");
        response.put("error", false);
        response.put("data", buildEventResponse(event));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private Map<String, Object> buildEventResponse(Event event) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", event.getId());
        data.put("name", event.getName());
        data.put("type", event.getType());
        data.put("description", event.getDescription());
        data.put("eventDate", event.getEventDate());
        data.put("eventTime", event.getEventTime());
        data.put("location", event.getLocation());
        data.put("address", event.getAddress());
        data.put("estimatedCost", event.getEstimatedCost());
        data.put("actualCost", event.getActualCost());
        data.put("status", event.getStatus());
        data.put("imageUrl", event.getImageUrl());
        data.put("createdAt", event.getCreatedAt());
        data.put("updatedAt", event.getUpdatedAt());
        data.put("createdBy", buildUserSummary(event.getCreatedBy()));
        
        List<EventParticipant> participants = event.getParticipants();
        data.put("participantsCount", participants != null ? participants.size() : 0);
        data.put("participants", participants != null
                ? participants.stream().map(this::buildParticipantResponse).toList()
                : java.util.Collections.emptyList());
        return data;
    }

    private Map<String, Object> buildUserSummary(User user) {
        if (user == null) {
            return null;
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", user.getId());
        summary.put("name", user.getName());
        summary.put("email", user.getEmail());
        summary.put("phone", user.getPhone());
        summary.put("role", user.getRole());
        summary.put("position", user.getPosition());
        summary.put("avatarUrl", user.getAvatarUrl());
        return summary;
    }

    private Map<String, Object> buildParticipantResponse(EventParticipant participant) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", participant.getId());
        data.put("memberId", participant.getUser().getId());
        data.put("memberName", participant.getUser().getName());
        data.put("status", participant.getStatus());
        data.put("joinedAt", participant.getJoinedAt());
        return data;
    }
}

