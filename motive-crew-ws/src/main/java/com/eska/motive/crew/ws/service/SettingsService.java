package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.UpdatePreferencesRequest;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.entity.UserPreferences;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.UserPreferencesRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing user settings and preferences
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class SettingsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository preferencesRepository;

    /**
     * Get user profile
     */
    public User getUserProfile(Long userId) throws ResourceNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));
    }

    /**
     * Update user profile
     */
    @Transactional
    public User updateProfile(Long userId, String name, String phone, String position, String avatarUrl)
            throws ResourceNotFoundException, InternalErrorException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));

            if (name != null) {
                user.setName(name);
            }
            if (phone != null) {
                user.setPhone(phone);
            }
            if (position != null) {
                user.setPosition(position);
            }
            if (avatarUrl != null) {
                user.setAvatarUrl(avatarUrl);
            }

            return userRepository.save(user);

        } catch (Exception e) {
            log.error("Error updating profile", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Get user preferences
     */
    public UserPreferences getPreferences(User user) {
        return getOrCreatePreferences(user);
    }

    /**
     * Update user preferences
     */
    @Transactional
    public UserPreferences updatePreferences(User user, UpdatePreferencesRequest request)
            throws InternalErrorException {
        try {
            UserPreferences preferences = getOrCreatePreferences(user);

            if (request.getNotificationsEnabled() != null) {
                preferences.setNotificationsEnabled(request.getNotificationsEnabled());
            }
            if (request.getDarkMode() != null) {
                preferences.setDarkMode(request.getDarkMode());
            }
            if (request.getLanguage() != null) {
                preferences.setLanguage(request.getLanguage());
            }
            if (request.getAutoLogin() != null) {
                preferences.setAutoLogin(request.getAutoLogin());
            }
            if (request.getDefaultMonth() != null) {
                try {
                    preferences.setDefaultMonth(UserPreferences.DefaultMonth.valueOf(request.getDefaultMonth().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Keep existing value
                }
            }

            return preferencesRepository.save(preferences);

        } catch (Exception e) {
            log.error("Error updating preferences", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Get or create preferences for user
     */
    @Transactional
    public UserPreferences getOrCreatePreferences(User user) {
        Optional<UserPreferences> preferences = preferencesRepository.findByUser(user);
        
        if (preferences.isPresent()) {
            return preferences.get();
        }

        // Create default preferences
        UserPreferences newPreferences = UserPreferences.builder()
                .user(user)
                .notificationsEnabled(true)
                .darkMode(false)
                .language("en")
                .autoLogin(true)
                .defaultMonth(UserPreferences.DefaultMonth.CURRENT)
                .build();

        return preferencesRepository.save(newPreferences);
    }

    /**
     * Upload avatar for current user
     */
    @Transactional
    public String uploadAvatar(User user, MultipartFile file)
            throws ValidationException, InternalErrorException {
        try {
            if (file == null || file.isEmpty()) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/jpeg")
                    && !contentType.startsWith("image/png")
                    && !contentType.startsWith("image/jpg"))) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = "user_" + user.getId() + "_" + UUID.randomUUID() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String avatarUrl = "/uploads/avatars/" + filename;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return avatarUrl;

        } catch (ValidationException e) {
            throw e;
        } catch (IOException e) {
            log.error("Error saving avatar file", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("Error uploading avatar", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }
}

