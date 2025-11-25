package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.ws.dto.request.ChangePasswordRequest;
import com.eska.motive.crew.ws.dto.request.LoginRequest;
import com.eska.motive.crew.ws.dto.request.SignupRequest;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller
 * 
 * @author Your Name
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * User login
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        return authService.login(request);
    }

    /**
     * User signup/registration
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest request)
            throws ValidationException, InternalErrorException {
        return authService.signup(request);
    }

    /**
     * Change password
     * PUT /api/v1/auth/password
     */
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        // Extract user ID from token
        Long userId = getUserIdFromToken(token);
        return authService.changePassword(userId, request);
    }

    /**
     * Get current user info
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token)
            throws ResourceNotFoundException {
        User user = authService.getCurrentUser(extractToken(token));
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("user", Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole().name(),
            "position", user.getPosition() != null ? user.getPosition() : "",
            "isActive", user.getIsActive()
        ));
        
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromToken(String token) throws ResourceNotFoundException {
        try {
            String jwtToken = extractToken(token);
            User user = authService.getCurrentUser(jwtToken);
            return user.getId();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Invalid token", "INVALID_TOKEN");
        }
    }

    private String extractToken(String authHeader) {
        return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
    }
}


