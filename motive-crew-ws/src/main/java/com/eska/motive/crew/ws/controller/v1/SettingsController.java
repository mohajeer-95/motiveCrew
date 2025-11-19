package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.UpdatePreferencesRequest;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.entity.UserPreferences;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.SettingsService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Settings and profile controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/settings")
@Log4j2
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private AuthService authService;

    /**
     * Get user profile
     * GET /api/v1/settings/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String token)
            throws ResourceNotFoundException {
        User user = getCurrentUser(token);
        User profile = settingsService.getUserProfile(user.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Profile retrieved successfully");
        response.put("error", false);
        response.put("data", buildUserProfileResponse(profile));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Update user profile
     * PUT /api/v1/settings/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String avatarUrl)
            throws ResourceNotFoundException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        User updatedUser = settingsService.updateProfile(currentUser.getId(), name, phone, position, avatarUrl);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Profile updated successfully");
        response.put("error", false);
        response.put("data", buildUserProfileResponse(updatedUser));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get user preferences
     * GET /api/v1/settings/preferences
     */
    @GetMapping("/preferences")
    public ResponseEntity<Map<String, Object>> getPreferences(@RequestHeader("Authorization") String token)
            throws ResourceNotFoundException {
        User user = getCurrentUser(token);
        UserPreferences preferences = settingsService.getPreferences(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Preferences retrieved successfully");
        response.put("error", false);
        response.put("data", buildPreferencesResponse(preferences));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Upload profile avatar
     * POST /api/v1/settings/profile/avatar
     */
    @PostMapping("/profile/avatar")
    public ResponseEntity<Map<String, Object>> uploadProfileAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file)
            throws ResourceNotFoundException, InternalErrorException, ValidationException {
        User currentUser = getCurrentUser(token);
        String avatarUrl = settingsService.uploadAvatar(currentUser, file);

        Map<String, Object> data = new HashMap<>();
        data.put("avatarUrl", avatarUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Avatar uploaded successfully");
        response.put("error", false);
        response.put("data", data);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Update user preferences
     * PUT /api/v1/settings/preferences
     */
    @PutMapping("/preferences")
    public ResponseEntity<Map<String, Object>> updatePreferences(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdatePreferencesRequest request)
            throws ResourceNotFoundException, InternalErrorException {
        User user = getCurrentUser(token);
        UserPreferences preferences = settingsService.updatePreferences(user, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Preferences updated successfully");
        response.put("error", false);
        response.put("data", buildPreferencesResponse(preferences));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private Map<String, Object> buildUserProfileResponse(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("role", user.getRole());
        data.put("position", user.getPosition());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("joinedDate", user.getJoinedDate());
        data.put("isActive", user.getIsActive());
        data.put("createdAt", user.getCreatedAt());
        data.put("updatedAt", user.getUpdatedAt());
        return data;
    }

    private Map<String, Object> buildPreferencesResponse(UserPreferences preferences) {
        Map<String, Object> data = new HashMap<>();
        data.put("notificationsEnabled", preferences.getNotificationsEnabled());
        data.put("darkMode", preferences.getDarkMode());
        data.put("language", preferences.getLanguage());
        data.put("autoLogin", preferences.getAutoLogin());
        data.put("defaultMonth", preferences.getDefaultMonth());
        data.put("createdAt", preferences.getCreatedAt());
        data.put("updatedAt", preferences.getUpdatedAt());
        return data;
    }
}

