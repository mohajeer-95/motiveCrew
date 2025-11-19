package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateAnnouncementRequest;
import com.eska.motive.crew.ws.entity.Announcement;
import com.eska.motive.crew.ws.entity.Notification;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.AnnouncementRepository;
import com.eska.motive.crew.ws.repository.NotificationRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing notifications
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all notifications for a user
     */
    public Page<Notification> getUserNotifications(User user, Notification.NotificationType type,
                                                   Boolean isRead, Pageable pageable) {
        return notificationRepository.findByUserAndFilters(user, type, isRead, pageable);
    }

    /**
     * Get unread notifications count
     */
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public Notification markAsRead(Long notificationId, User user)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Verify ownership
            if (!notification.getUser().getId().equals(user.getId())) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());

            return notificationRepository.save(notification);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Mark all notifications as read
     */
    @Transactional
    public int markAllAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalse(user);
        unreadNotifications.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }

    /**
     * Delete notification
     */
    @Transactional
    public void deleteNotification(Long notificationId, User user)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Verify ownership
            if (!notification.getUser().getId().equals(user.getId())) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            notificationRepository.delete(notification);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting notification", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Clear all notifications for a user
     */
    @Transactional
    public int clearAllNotifications(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged())
                .getContent();
        notificationRepository.deleteAll(notifications);
        return notifications.size();
    }

    /**
     * Create announcement (admin only)
     */
    @Transactional
    public Announcement createAnnouncement(CreateAnnouncementRequest request, User currentUser)
            throws ValidationException, InternalErrorException {
        try {
            // Only admin can create announcements
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            Announcement announcement = Announcement.builder()
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .createdBy(currentUser)
                    .isActive(true)
                    .build();

            announcement = announcementRepository.save(announcement);

            // Create notifications for all active users
            List<User> allUsers = userRepository.findAll().stream()
                    .filter(User::getIsActive)
                    .toList();

            // Extract values to make them effectively final
            final String announcementTitle = announcement.getTitle();
            final String announcementMessage = announcement.getMessage();
            final Long announcementId = announcement.getId();

            List<Notification> notifications = allUsers.stream()
                    .map(user -> Notification.builder()
                            .user(user)
                            .type(Notification.NotificationType.ANNOUNCEMENT)
                            .title(announcementTitle)
                            .message(announcementMessage)
                            .relatedId(announcementId)
                            .relatedType("announcement")
                            .isRead(false)
                            .build())
                    .toList();

            notificationRepository.saveAll(notifications);

            return announcement;

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating announcement", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Create notification for a user
     */
    @Transactional
    public Notification createNotification(User user, Notification.NotificationType type,
                                          String title, String message, Long relatedId, String relatedType) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    /**
     * Create notification for multiple users
     */
    @Transactional
    public void createNotificationsForUsers(List<User> users, Notification.NotificationType type,
                                            String title, String message, Long relatedId, String relatedType) {
        List<Notification> notifications = users.stream()
                .map(user -> Notification.builder()
                        .user(user)
                        .type(type)
                        .title(title)
                        .message(message)
                        .relatedId(relatedId)
                        .relatedType(relatedType)
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }
}

