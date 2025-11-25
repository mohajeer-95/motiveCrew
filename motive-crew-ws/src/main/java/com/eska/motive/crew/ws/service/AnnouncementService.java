package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.AnnouncementCreateRequest;
import com.eska.motive.crew.ws.dto.request.AnnouncementSeenRequest;
import com.eska.motive.crew.ws.dto.request.AnnouncementUpdateRequest;
import com.eska.motive.crew.ws.dto.response.AnnouncementDTO;
import com.eska.motive.crew.ws.entity.Announcement;
import com.eska.motive.crew.ws.entity.Team;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.entity.UserAnnouncementView;
import com.eska.motive.crew.ws.enums.AnnouncementAudience;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.mapper.AnnouncementMapper;
import com.eska.motive.crew.ws.repository.AnnouncementRepository;
import com.eska.motive.crew.ws.repository.TeamRepository;
import com.eska.motive.crew.ws.repository.UserAnnouncementViewRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserAnnouncementViewRepository userAnnouncementViewRepository;
    private final AnnouncementMapper announcementMapper;

    @Transactional
    public AnnouncementDTO createAnnouncement(AnnouncementCreateRequest request, User currentUser)
            throws ValidationException, ResourceNotFoundException {
        ensureAdmin(currentUser);
        Announcement announcement = new Announcement();
        applyCommonFields(announcement, request.getTitle(), request.getMessage(), request.getImageUrl(),
                request.getAudience(), request.getTeamId(), request.getExpiresAt());
        announcement.setCreatedBy(currentUser);
        announcement.setIsActive(true);
        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toDto(saved);
    }

    @Transactional
    public AnnouncementDTO updateAnnouncement(Long id, AnnouncementUpdateRequest request, User currentUser)
            throws ValidationException, ResourceNotFoundException {
        ensureAdmin(currentUser);
        Announcement announcement = findAnnouncement(id);
        applyCommonFields(announcement, request.getTitle(), request.getMessage(), request.getImageUrl(),
                request.getAudience(), request.getTeamId(), request.getExpiresAt());
        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toDto(saved);
    }

    @Transactional
    public void deleteAnnouncement(Long id, User currentUser)
            throws ResourceNotFoundException, ValidationException {
        ensureAdmin(currentUser);
        Announcement announcement = findAnnouncement(id);
        announcement.setIsActive(false);
        announcementRepository.save(announcement);
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getActiveAnnouncements(Long teamId) {
        LocalDateTime now = LocalDateTime.now();
        return announcementRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .filter(a -> a.getExpiresAt() == null || !a.getExpiresAt().isBefore(now))
                .filter(a -> teamId == null
                        || a.getAudience() == AnnouncementAudience.COMPANY
                        || (a.getAudience() == AnnouncementAudience.TEAM
                        && a.getTeam() != null && teamId.equals(a.getTeam().getId())))
                .map(announcementMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getArchivedAnnouncements() {
        return announcementRepository.findArchivedAnnouncements(LocalDateTime.now())
                .stream()
                .map(announcementMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnnouncementDTO getAnnouncement(Long id) throws ResourceNotFoundException {
        return announcementMapper.toDto(findAnnouncement(id));
    }

    @Transactional
    public AnnouncementDTO reactivateAnnouncement(Long id, User currentUser)
            throws ResourceNotFoundException, ValidationException {
        ensureAdmin(currentUser);
        Announcement announcement = findAnnouncement(id);
        announcement.setIsActive(true);
        announcement.setExpiresAt(null);
        Announcement saved = announcementRepository.save(announcement);
        return announcementMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<AnnouncementDTO> getPendingAnnouncementForUser(Long userId)
            throws ResourceNotFoundException {
        User user = findUser(userId);
        Long teamId = user.getTeam() != null ? user.getTeam().getId() : null;
        List<Announcement> announcements = announcementRepository
                .findPendingAnnouncementsForUser(user.getId(), teamId, LocalDateTime.now());
        return announcements.stream()
                .findFirst()
                .map(announcementMapper::toDto);
    }

    @Transactional
    public void markAnnouncementAsSeen(Long announcementId, AnnouncementSeenRequest request)
            throws ResourceNotFoundException {
        User user = findUser(request.getUserId());
        Announcement announcement = findAnnouncement(announcementId);

        boolean alreadySeen = userAnnouncementViewRepository
                .existsByUser_IdAndAnnouncement_Id(user.getId(), announcement.getId());
        if (!alreadySeen) {
            UserAnnouncementView view = UserAnnouncementView.builder()
                    .announcement(announcement)
                    .user(user)
                    .build();
            userAnnouncementViewRepository.save(view);
        }
    }

    private void applyCommonFields(Announcement announcement,
                                   String title,
                                   String message,
                                   String imageUrl,
                                   String audienceValue,
                                   Long teamId,
                                   LocalDateTime expiresAt)
            throws ValidationException, ResourceNotFoundException {
        AnnouncementAudience audience = toAudience(audienceValue);
        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setImageUrl(imageUrl);
        announcement.setAudience(audience);
        announcement.setExpiresAt(expiresAt);

        if (audience == AnnouncementAudience.TEAM) {
            if (teamId == null) {
                throw new ValidationException("teamId is required for TEAM audience");
            }
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found", StatusCode.NOT_FOUND));
            announcement.setTeam(team);
        } else {
            announcement.setTeam(null);
        }
    }

    private void ensureAdmin(User user) throws ValidationException {
        if (user.getRole() != User.UserRole.ADMIN) {
            throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
        }
    }

    private Announcement findAnnouncement(Long id) throws ResourceNotFoundException {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found", StatusCode.NOT_FOUND));
    }

    private User findUser(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", StatusCode.NOT_FOUND));
    }

    private AnnouncementAudience toAudience(String value) throws ValidationException {
        try {
            return AnnouncementAudience.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Invalid audience value");
        }
    }
}


