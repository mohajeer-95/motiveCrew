package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.contract.dto.AuthenticationDTO;
import com.eska.motive.crew.contract.request.impl.LoginRequest;
import com.eska.motive.crew.contract.response.Response;
import com.eska.motive.crew.contract.response.impl.LoginResponse;
import com.eska.motive.crew.ws.dto.request.ChangePasswordRequest;
import com.eska.motive.crew.ws.dto.request.SignupRequest;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.entity.UserPreferences;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.UserRepository;
import com.eska.motive.crew.ws.repository.UserPreferencesRepository;
import com.eska.motive.crew.ws.repository.TeamRepository;
import com.eska.motive.crew.ws.util.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Authentication service for login, signup, and password management
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository preferencesRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticate user and generate JWT token
     */
    public ResponseEntity<Response> login(LoginRequest loginRequest)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Find user by email (username)
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getUsername());
            
            if (userOpt.isEmpty()) {
                throw new ValidationException(StatusCode.INVALID_USER_NAME_PASS);
            }

            User user = userOpt.get();
            
            if (!user.getIsActive() || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                throw new ValidationException(StatusCode.INVALID_USER_NAME_PASS);
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail());
            
            // Build response
            AuthenticationDTO authDTO = AuthenticationDTO.builder()
                    .token(token)
                    .build();
            
            LoginResponse response = new LoginResponse(authDTO);
            response.setStatusCode(StatusCode.SUCCESS.getCode());
            response.setMessage(StatusCode.SUCCESS.getDescription());
            response.setError(false);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during login", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Register a new user
     */
    @Transactional
    public ResponseEntity<Response> signup(SignupRequest request)
            throws ValidationException, InternalErrorException {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            // Create user
            User.UserRole role = "admin".equalsIgnoreCase(request.getRole()) 
                    ? User.UserRole.ADMIN 
                    : User.UserRole.MEMBER;

            // Get team if teamId is provided
            com.eska.motive.crew.ws.entity.Team team = null;
            if (request.getTeamId() != null) {
                team = teamRepository.findById(request.getTeamId())
                        .orElse(null); // Team not found, but don't fail signup
            }

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .position(request.getPosition())
                    .team(team)
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

            // Generate token
            String token = jwtUtil.generateToken(user.getEmail());
            
            AuthenticationDTO authDTO = AuthenticationDTO.builder()
                    .token(token)
                    .build();
            
            LoginResponse response = new LoginResponse(authDTO);
            response.setStatusCode(StatusCode.SUCCESS.getCode());
            response.setMessage("User registered successfully");
            response.setError(false);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during signup", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Change user password
     */
    @Transactional
    public ResponseEntity<Response> changePassword(Long userId, ChangePasswordRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            // Verify new passwords match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new ValidationException(StatusCode.GENERAL_FIELD_VALIDATION_ERROR);
            }

            // Update password
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            Response response = new Response();
            response.setStatusCode(StatusCode.SUCCESS.getCode());
            response.setMessage("Password changed successfully");
            response.setError(false);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error changing password", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Get current user from token
     */
    public User getCurrentUser(String token) throws ResourceNotFoundException {
        String email = jwtUtil.getUsernameFromToken(token);
        if (email == null) {
            throw new ResourceNotFoundException(StatusCode.USER_NOT_FOUND);
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));
    }
}

