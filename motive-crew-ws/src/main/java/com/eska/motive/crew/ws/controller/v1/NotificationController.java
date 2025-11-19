package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateAnnouncementRequest;
import com.eska.motive.crew.ws.entity.Announcement;
import com.eska.motive.crew.ws.entity.Notification;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Notification controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Log4j2
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthService authService;

    /**
     * Get user notifications
     * GET /api/v1/notifications
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            User user = getCurrentUser(token);
            Pageable pageable = PageRequest.of(page, size);
            Notification.NotificationType notificationType = null;
            
            if (type != null) {
                try {
                    notificationType = Notification.NotificationType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Invalid type
                }
            }
            
            Page<Notification> notifications = notificationService.getUserNotifications(user, notificationType, isRead, pageable);
            long unreadCount = notificationService.getUnreadCount(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Notifications retrieved successfully");
            response.put("error", false);
            response.put("data", notifications.stream()
                    .map(this::buildNotificationResponse)
                    .toList());
            response.put("totalElements", notifications.getTotalElements());
            response.put("totalPages", notifications.getTotalPages());
            response.put("unreadCount", unreadCount);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (Exception e) {
            log.error("Error getting notifications", e);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.INTERNAL_ERROR.getCode());
            response.put("message", "Error retrieving notifications: " + e.getMessage());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get unread count
     * GET /api/v1/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@RequestHeader("Authorization") String token)
            throws ResourceNotFoundException {
        User user = getCurrentUser(token);
        long count = notificationService.getUnreadCount(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Unread count retrieved");
        response.put("error", false);
        response.put("data", Map.of("unreadCount", count));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Mark notification as read
     * PUT /api/v1/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User user = getCurrentUser(token);
        Notification notification = notificationService.markAsRead(id, user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Notification marked as read");
        response.put("error", false);
        response.put("data", buildNotificationResponse(notification));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Mark all as read
     * PUT /api/v1/notifications/read-all
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@RequestHeader("Authorization") String token)
            throws ResourceNotFoundException {
        User user = getCurrentUser(token);
        int count = notificationService.markAllAsRead(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", count + " notifications marked as read");
        response.put("error", false);
        response.put("data", Map.of("markedCount", count));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Delete notification
     * DELETE /api/v1/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User user = getCurrentUser(token);
        notificationService.deleteNotification(id, user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Notification deleted successfully");
        response.put("error", false);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Clear all notifications
     * DELETE /api/v1/notifications
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearAllNotifications(@RequestHeader("Authorization") String token)
            throws ResourceNotFoundException {
        User user = getCurrentUser(token);
        int count = notificationService.clearAllNotifications(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", count + " notifications cleared");
        response.put("error", false);
        response.put("data", Map.of("clearedCount", count));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Create announcement (admin only)
     * POST /api/v1/notifications/announcements
     */
    @PostMapping("/announcements")
    public ResponseEntity<Map<String, Object>> createAnnouncement(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateAnnouncementRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        Announcement announcement = notificationService.createAnnouncement(request, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Announcement created successfully");
        response.put("error", false);
        response.put("data", buildAnnouncementResponse(announcement));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private Map<String, Object> buildNotificationResponse(Notification notification) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", notification.getId());
        data.put("title", notification.getTitle());
        data.put("message", notification.getMessage());
        data.put("type", notification.getType());
        data.put("relatedId", notification.getRelatedId());
        data.put("relatedType", notification.getRelatedType());
        data.put("isRead", notification.getIsRead());
        data.put("createdAt", notification.getCreatedAt());
        data.put("readAt", notification.getReadAt());
        data.put("user", buildUserSummary(notification.getUser()));
        return data;
    }

    private Map<String, Object> buildAnnouncementResponse(Announcement announcement) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", announcement.getId());
        data.put("title", announcement.getTitle());
        data.put("message", announcement.getMessage());
        data.put("isActive", announcement.getIsActive());
        data.put("createdAt", announcement.getCreatedAt());
        data.put("createdBy", buildUserSummary(announcement.getCreatedBy()));
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
}

