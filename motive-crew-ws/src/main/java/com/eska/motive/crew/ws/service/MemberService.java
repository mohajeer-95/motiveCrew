package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateMemberRequest;
import com.eska.motive.crew.ws.dto.request.UpdateMemberRequest;
import com.eska.motive.crew.ws.dto.response.UserDTO;
import com.eska.motive.crew.ws.entity.MemberPayment;
import com.eska.motive.crew.ws.entity.MonthlyCollection;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.entity.UserPreferences;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.MemberPaymentRepository;
import com.eska.motive.crew.ws.repository.MonthlyCollectionRepository;
import com.eska.motive.crew.ws.repository.UserPreferencesRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Member service for managing team members
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class MemberService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository preferencesRepository;

    @Autowired
    private MonthlyCollectionRepository collectionRepository;

    @Autowired
    private MemberPaymentRepository paymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all members with pagination and filters
     */
    public Page<UserDTO> getAllMembers(String search, User.UserRole role, Boolean isActive, Pageable pageable) {
        Page<User> users = userRepository.findByFilters(search, role, isActive, pageable);
        return users.map(this::convertToDTO);
    }

    /**
     * Get member by ID
     */
    public UserDTO getMemberById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));
        return convertToDTO(user);
    }

    /**
     * Create a new member
     */
    @Transactional
    public UserDTO createMember(CreateMemberRequest request)
            throws ValidationException, InternalErrorException {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            // Determine role
            User.UserRole role = "admin".equalsIgnoreCase(request.getRole())
                    ? User.UserRole.ADMIN
                    : User.UserRole.MEMBER;

            // Create user
            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .passwordHash(passwordEncoder.encode(
                            request.getPassword() != null ? request.getPassword() : "defaultPassword123"))
                    .role(role)
                    .position(request.getPosition())
                    .isActive(true)
                    .joinedDate(LocalDate.now())
                    .build();

            user = userRepository.save(user);

            // Create default preferences
            UserPreferences preferences = UserPreferences.builder()
                    .user(user)
                    .notificationsEnabled(true)
                    .darkMode(false)
                    .language("en")
                    .autoLogin(true)
                    .defaultMonth(UserPreferences.DefaultMonth.CURRENT)
                    .build();
            preferencesRepository.save(preferences);

            return convertToDTO(user);

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating member", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Update member information
     */
    @Transactional
    public UserDTO updateMember(Long id, UpdateMemberRequest request, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));

            // Check permissions: admin can update anyone, members can only update themselves
            if (currentUser.getRole() != User.UserRole.ADMIN && !user.getId().equals(currentUser.getId())) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            // Update fields
            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getPosition() != null) {
                user.setPosition(request.getPosition());
            }
            if (request.getAvatarUrl() != null) {
                user.setAvatarUrl(request.getAvatarUrl());
            }

            user = userRepository.save(user);
            return convertToDTO(user);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating member", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Delete member (soft delete - set isActive to false)
     */
    @Transactional
    public void deleteMember(Long id, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can delete
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));

            // Prevent deleting the last admin
            if (user.getRole() == User.UserRole.ADMIN) {
                long adminCount = userRepository.countByRole(User.UserRole.ADMIN);
                if (adminCount <= 1) {
                    throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
                }
            }

            // Soft delete
            user.setIsActive(false);
            userRepository.save(user);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting member", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Upload avatar for a member
     */
    @Transactional
    public String uploadAvatar(Long memberId, MultipartFile file, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Find member
            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));

            // Check permissions: admin can update anyone, members can only update themselves
            if (currentUser.getRole() != User.UserRole.ADMIN && !member.getId().equals(currentUser.getId())) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            // Validate file
            if (file == null || file.isEmpty()) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            // Check file size (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/jpeg") && 
                                       !contentType.startsWith("image/png") && 
                                       !contentType.startsWith("image/jpg"))) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            // Create upload directory if it doesn't exist
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = "member_" + memberId + "_" + UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Generate URL (for now, using relative path - in production, use full URL)
            String avatarUrl = "/uploads/avatars/" + filename;

            // Update member's avatar URL
            member.setAvatarUrl(avatarUrl);
            userRepository.save(member);

            return avatarUrl;

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (IOException e) {
            log.error("Error saving avatar file", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("Error uploading avatar", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        // Get current month payment status
        LocalDate now = LocalDate.now();
        Optional<MonthlyCollection> currentCollection = collectionRepository
                .findByYearAndMonth(now.getYear(), now.getMonthValue());

        String currentMonthStatus = "pending";
        LocalDate lastPaymentDate = null;
        if (currentCollection.isPresent()) {
            Optional<MemberPayment> payment = paymentRepository
                    .findByUserAndCollection(user, currentCollection.get());
            if (payment.isPresent() && payment.get().getStatus() == MemberPayment.PaymentStatus.PAID) {
                currentMonthStatus = "paid";
                lastPaymentDate = payment.get().getPaymentDate();
            }
        }

        // Count events joined
        int eventsJoined = user.getEventParticipants() != null
                ? (int) user.getEventParticipants().stream()
                        .filter(p -> p.getStatus() == com.eska.motive.crew.ws.entity.EventParticipant.ParticipantStatus.JOINED)
                        .count()
                : 0;

        // Calculate total contribution
        java.math.BigDecimal totalContribution = user.getPayments() != null
                ? user.getPayments().stream()
                        .filter(p -> p.getStatus() == MemberPayment.PaymentStatus.PAID)
                        .map(MemberPayment::getAmount)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                : java.math.BigDecimal.ZERO;

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .position(user.getPosition())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .joinedDate(user.getJoinedDate())
                .currentMonthStatus(currentMonthStatus)
                .lastPaymentDate(lastPaymentDate)
                .eventsJoined(eventsJoined)
                .totalContribution(totalContribution)
                .build();
    }
}

