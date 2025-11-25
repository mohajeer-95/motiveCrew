package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.ws.dto.request.ChangePasswordRequest;
import com.eska.motive.crew.ws.dto.request.LoginRequest;
import com.eska.motive.crew.ws.dto.request.SignupRequest;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.UserRepository;
import com.eska.motive.crew.ws.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication service for login, signup, and password management
 * 
 * @author Your Name
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate user and generate JWT token
     */
    public ResponseEntity<Map<String, Object>> login(LoginRequest loginRequest)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Find user by email (username)
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getUsername());
            
            if (userOpt.isEmpty()) {
                throw new ValidationException("Invalid username or password", "INVALID_CREDENTIALS");
            }

            User user = userOpt.get();
            
            if (!user.getIsActive() || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                throw new ValidationException("Invalid username or password", "INVALID_CREDENTIALS");
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail());
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
            ));
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during login", e);
            throw new InternalErrorException("An error occurred during login");
        }
    }

    /**
     * Register a new user
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> signup(SignupRequest request)
            throws ValidationException, InternalErrorException {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ValidationException("Email already exists", "EMAIL_EXISTS");
            }

            // Create user
            User.UserRole role = "admin".equalsIgnoreCase(request.getRole()) 
                    ? User.UserRole.ADMIN 
                    : User.UserRole.MEMBER;

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .position(request.getPosition())
                    .isActive(true)
                    .joinedDate(LocalDate.now())
                    .build();

            user = userRepository.save(user);

            // Generate token
            String token = jwtUtil.generateToken(user.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "User registered successfully");
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
            ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during signup", e);
            throw new InternalErrorException("An error occurred during signup");
        }
    }

    /**
     * Change user password
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> changePassword(Long userId, ChangePasswordRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER_NOT_FOUND"));

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new ValidationException("Current password is incorrect", "INVALID_PASSWORD");
            }

            // Verify new passwords match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new ValidationException("New passwords do not match", "PASSWORD_MISMATCH");
            }

            // Update password
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Password changed successfully");
            
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error changing password", e);
            throw new InternalErrorException("An error occurred while changing password");
        }
    }

    /**
     * Get current user from token
     */
    public User getCurrentUser(String token) throws ResourceNotFoundException {
        String email = jwtUtil.getUsernameFromToken(token);
        if (email == null) {
            throw new ResourceNotFoundException("Invalid token", "INVALID_TOKEN");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER_NOT_FOUND"));
    }
}


