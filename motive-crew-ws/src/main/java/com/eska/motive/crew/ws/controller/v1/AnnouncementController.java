package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.AnnouncementCreateRequest;
import com.eska.motive.crew.ws.dto.request.AnnouncementSeenRequest;
import com.eska.motive.crew.ws.dto.request.AnnouncementUpdateRequest;
import com.eska.motive.crew.ws.dto.response.AnnouncementDTO;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AnnouncementService;
import com.eska.motive.crew.ws.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@Log4j2
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAnnouncement(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AnnouncementCreateRequest request)
            throws ValidationException, ResourceNotFoundException {
        User currentUser = getCurrentUser(token);
        AnnouncementDTO dto = announcementService.createAnnouncement(request, currentUser);
        return buildResponse(HttpStatus.CREATED, "Announcement created", dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAnnouncement(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementUpdateRequest request)
            throws ValidationException, ResourceNotFoundException {
        User currentUser = getCurrentUser(token);
        AnnouncementDTO dto = announcementService.updateAnnouncement(id, request, currentUser);
        return buildResponse(HttpStatus.OK, "Announcement updated", dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAnnouncement(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws ValidationException, ResourceNotFoundException {
        User currentUser = getCurrentUser(token);
        announcementService.deleteAnnouncement(id, currentUser);
        return buildResponse(HttpStatus.OK, "Announcement archived", null);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveAnnouncements(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long teamId) throws ResourceNotFoundException {
        getCurrentUser(token);
        List<AnnouncementDTO> announcements = announcementService.getActiveAnnouncements(teamId);
        return buildResponse(HttpStatus.OK, "Active announcements retrieved", announcements);
    }

    @GetMapping("/archive")
    public ResponseEntity<Map<String, Object>> getArchivedAnnouncements(
            @RequestHeader("Authorization") String token) throws ResourceNotFoundException {
        getCurrentUser(token);
        List<AnnouncementDTO> announcements = announcementService.getArchivedAnnouncements();
        return buildResponse(HttpStatus.OK, "Archived announcements retrieved", announcements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAnnouncement(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) throws ResourceNotFoundException {
        getCurrentUser(token);
        AnnouncementDTO dto = announcementService.getAnnouncement(id);
        return buildResponse(HttpStatus.OK, "Announcement retrieved", dto);
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<Map<String, Object>> reactivateAnnouncement(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws ResourceNotFoundException, ValidationException {
        User currentUser = getCurrentUser(token);
        AnnouncementDTO dto = announcementService.reactivateAnnouncement(id, currentUser);
        return buildResponse(HttpStatus.OK, "Announcement reactivated", dto);
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<Map<String, Object>> getPendingAnnouncement(@PathVariable Long userId)
            throws ResourceNotFoundException {
        var optional = announcementService.getPendingAnnouncementForUser(userId);
        String message = optional.isPresent() ? "Pending announcement found" : "No pending announcements";
        return buildResponse(HttpStatus.OK, message, optional.orElse(null));
    }

    @PostMapping("/{id}/seen")
    public ResponseEntity<Map<String, Object>> markAsSeen(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementSeenRequest request)
            throws ResourceNotFoundException {
        announcementService.markAnnouncementAsSeen(id, request);
        return buildResponse(HttpStatus.OK, "Announcement marked as seen", null);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", message);
        response.put("error", false);
        response.put("data", data);
        return ResponseEntity.status(status).body(response);
    }
}


