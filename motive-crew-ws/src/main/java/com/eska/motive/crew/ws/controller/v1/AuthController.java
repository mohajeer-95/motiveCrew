package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.request.impl.LoginRequest;
import com.eska.motive.crew.contract.response.Response;
import com.eska.motive.crew.ws.dto.request.ChangePasswordRequest;
import com.eska.motive.crew.ws.dto.request.SignupRequest;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/auth")
@Log4j2
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User login
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody LoginRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        return authService.login(request);
    }

    /**
     * User signup/registration
     * POST /api/v1/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<Response> signup(@Valid @RequestBody SignupRequest request)
            throws ValidationException, InternalErrorException {
        return authService.signup(request);
    }

    /**
     * Change password
     * PUT /api/v1/auth/password
     */
    @PutMapping("/password")
    public ResponseEntity<Response> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        // Extract user ID from token (simplified - in production, use proper JWT parsing)
        // For now, we'll need to get user from token
        Long userId = getUserIdFromToken(token);
        return authService.changePassword(userId, request);
    }

    /**
     * Get current user info
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<Response> getCurrentUser(@RequestHeader("Authorization") String token)
            throws ResourceNotFoundException {
        // This would return current user info
        // For now, return success
        Response response = new Response();
        response.setStatusCode(com.eska.motive.crew.contract.StatusCode.SUCCESS.getCode());
        response.setMessage("User info retrieved");
        response.setError(false);
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromToken(String token) throws ResourceNotFoundException {
        try {
            // Extract token from "Bearer <token>"
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            com.eska.motive.crew.ws.entity.User user = authService.getCurrentUser(jwtToken);
            return user.getId();
        } catch (Exception e) {
            throw new ResourceNotFoundException(com.eska.motive.crew.contract.StatusCode.USER_NOT_FOUND);
        }
    }
}

