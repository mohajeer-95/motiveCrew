package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.contract.response.Response;
import com.eska.motive.crew.ws.dto.request.CreateMemberRequest;
import com.eska.motive.crew.ws.dto.request.UpdateMemberRequest;
import com.eska.motive.crew.ws.dto.response.UserDTO;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Member management controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/members")
@Log4j2
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthService authService;

    /**
     * Get all members
     * GET /api/v1/members
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMembers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            User.UserRole userRole = null;
            if (role != null) {
                try {
                    userRole = User.UserRole.valueOf(role.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Invalid role, keep as null
                }
            }
            
            Page<UserDTO> members = memberService.getAllMembers(search, userRole, isActive, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Members retrieved successfully");
            response.put("error", false);
            response.put("data", members.getContent());
            response.put("totalElements", members.getTotalElements());
            response.put("totalPages", members.getTotalPages());
            response.put("currentPage", members.getNumber());
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (Exception e) {
            log.error("Error getting members", e);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.INTERNAL_ERROR.getCode());
            response.put("message", "Error retrieving members: " + e.getMessage());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get member by ID
     * GET /api/v1/members/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMemberById(@PathVariable Long id) {
        try {
            UserDTO member = memberService.getMemberById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Member retrieved successfully");
            response.put("error", false);
            response.put("data", member);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (ResourceNotFoundException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.USER_NOT_FOUND.getCode());
            response.put("message", StatusCode.USER_NOT_FOUND.getDescription());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            log.error("Error getting member", e);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.INTERNAL_ERROR.getCode());
            response.put("message", "Error retrieving member: " + e.getMessage());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create new member
     * POST /api/v1/members
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMember(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody CreateMemberRequest request)
            throws ValidationException, InternalErrorException {
        try {
            User currentUser = getCurrentUser(token);
            UserDTO member = memberService.createMember(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Member created successfully");
            response.put("error", false);
            response.put("data", member);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ValidationException | InternalErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating member", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Update member
     * PUT /api/v1/members/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMember(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            User currentUser = getCurrentUser(token);
            UserDTO member = memberService.updateMember(id, request, currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Member updated successfully");
            response.put("error", false);
            response.put("data", member);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (ResourceNotFoundException | ValidationException | InternalErrorException e) {
            throw e;
        }
    }

    /**
     * Delete member
     * DELETE /api/v1/members/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteMember(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            User currentUser = getCurrentUser(token);
            memberService.deleteMember(id, currentUser);
            
            Response response = new Response();
            response.setStatusCode(StatusCode.SUCCESS.getCode());
            response.setMessage("Member deleted successfully");
            response.setError(false);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (ResourceNotFoundException | ValidationException | InternalErrorException e) {
            throw e;
        }
    }

    /**
     * Upload member avatar
     * POST /api/v1/members/{id}/avatar
     */
    @PostMapping("/{id}/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            User currentUser = getCurrentUser(token);
            String avatarUrl = memberService.uploadAvatar(id, file, currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Avatar uploaded successfully");
            response.put("error", false);
            Map<String, Object> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);
            response.put("data", data);
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (ResourceNotFoundException | ValidationException | InternalErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error uploading avatar", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        if (token == null || token.isEmpty()) {
            throw new ResourceNotFoundException(StatusCode.USER_NOT_FOUND);
        }
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }
}
