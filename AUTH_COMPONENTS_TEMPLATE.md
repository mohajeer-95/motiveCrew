# Authentication Components - Complete Template

This document contains all the authentication-related components you need to create for a new Spring Boot project.

## 📁 File Structure

```
src/main/java/com/eska/motive/crew/ws/
├── entity/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── dto/
│   └── request/
│       ├── LoginRequest.java
│       ├── SignupRequest.java
│       └── ChangePasswordRequest.java
├── util/
│   └── JWTUtil.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── ValidationException.java
│   └── InternalErrorException.java
├── service/
│   └── AuthService.java
└── controller/v1/
    └── AuthController.java
```

---

## 1. Entity: User.java

**Location:** `src/main/java/com/eska/motive/crew/ws/entity/User.java`

```java
package com.eska.motive.crew.ws.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User entity representing application users
 * 
 * @author Your Name
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private UserRole role = UserRole.MEMBER;

    @Column(length = 100)
    private String position;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (joinedDate == null) {
            joinedDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum UserRole {
        ADMIN, MEMBER
    }
}
```

---

## 2. Repository: UserRepository.java

**Location:** `src/main/java/com/eska/motive/crew/ws/repository/UserRepository.java`

```java
package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 * 
 * @author Your Name
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
```

---

## 3. DTOs

### LoginRequest.java

**Location:** `src/main/java/com/eska/motive/crew/ws/dto/request/LoginRequest.java`

```java
package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login request DTO
 * 
 * @author Your Name
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username/Email is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
```

### SignupRequest.java

**Location:** `src/main/java/com/eska/motive/crew/ws/dto/request/SignupRequest.java`

```java
package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signup request DTO
 * 
 * @author Your Name
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;

    private String role; // "admin" or "member"
}
```

### ChangePasswordRequest.java

**Location:** `src/main/java/com/eska/motive/crew/ws/dto/request/ChangePasswordRequest.java`

```java
package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Change password request DTO
 * 
 * @author Your Name
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
```

---

## 4. Utility: JWTUtil.java

**Location:** `src/main/java/com/eska/motive/crew/ws/util/JWTUtil.java`

```java
package com.eska.motive.crew.ws.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Utility for token generation and validation
 * 
 * @author Your Name
 */
@Component
public class JWTUtil {
    
    @Value("${jwt.secret:your-secret-key-here-change-this-in-production-make-it-long-and-secure}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long expirationTime;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
```

**Add to `application.properties`:**
```properties
jwt.secret=your-secret-key-here-change-this-in-production-make-it-long-and-secure
jwt.expiration=86400000
```

---

## 5. Exception Classes

### ResourceNotFoundException.java

**Location:** `src/main/java/com/eska/motive/crew/ws/exception/ResourceNotFoundException.java`

```java
package com.eska.motive.crew.ws.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found
 * 
 * @author Your Name
 */
@Getter
public class ResourceNotFoundException extends Exception {

    private static final long serialVersionUID = -6806458320186959974L;
    
    private String errorCode;

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorCode = "RESOURCE_NOT_FOUND";
    }
    
    public ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
```

### ValidationException.java

**Location:** `src/main/java/com/eska/motive/crew/ws/exception/ValidationException.java`

```java
package com.eska.motive.crew.ws.exception;

import lombok.Getter;

/**
 * Exception thrown when validation fails
 * 
 * @author Your Name
 */
@Getter
public class ValidationException extends Exception {

    private static final long serialVersionUID = 7799855377510457678L;
    
    private String errorCode;

    public ValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ValidationException(String message, Throwable throwable) {
        super(message, throwable);
        this.errorCode = "VALIDATION_ERROR";
    }
}
```

### InternalErrorException.java

**Location:** `src/main/java/com/eska/motive/crew/ws/exception/InternalErrorException.java`

```java
package com.eska.motive.crew.ws.exception;

import lombok.Getter;

/**
 * Exception thrown when an internal server error occurs
 * 
 * @author Your Name
 */
@Getter
public class InternalErrorException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String errorCode;

    public InternalErrorException(String message) {
        super(message);
        this.errorCode = "INTERNAL_ERROR";
    }
    
    public InternalErrorException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
```

---

## 6. Service: AuthService.java

**Location:** `src/main/java/com/eska/motive/crew/ws/service/AuthService.java`

```java
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
```

---

## 7. Controller: AuthController.java

**Location:** `src/main/java/com/eska/motive/crew/ws/controller/v1/AuthController.java`

```java
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
```

---

## 8. Exception Handler (Global)

**Location:** `src/main/java/com/eska/motive/crew/ws/exception/ExceptionHandlerAdvice.java`

```java
package com.eska.motive.crew.ws.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler
 * 
 * @author Your Name
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<Map<String, Object>> handleInternalError(InternalErrorException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Validation failed");
        response.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "An unexpected error occurred");
        response.put("errorCode", "INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

---

## 9. Database Schema (SQL)

**Location:** `src/main/resources/db/migration/V1__create_users_table.sql`

```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'MEMBER',
    position VARCHAR(100),
    avatar_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    joined_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 10. Application Properties

Add to `application.properties` or `application-dev.properties`:

```properties
# JWT Configuration
jwt.secret=your-secret-key-here-change-this-in-production-make-it-long-and-secure-at-least-256-bits
jwt.expiration=86400000
```

---

## ✅ Checklist

- [ ] Create User entity
- [ ] Create UserRepository
- [ ] Create LoginRequest DTO
- [ ] Create SignupRequest DTO
- [ ] Create ChangePasswordRequest DTO
- [ ] Create JWTUtil
- [ ] Create exception classes
- [ ] Create AuthService
- [ ] Create AuthController
- [ ] Create ExceptionHandlerAdvice
- [ ] Create database migration
- [ ] Add JWT properties to application.properties
- [ ] Test login endpoint
- [ ] Test signup endpoint
- [ ] Test change password endpoint

---

## 🧪 Testing Endpoints

### Login
```bash
POST http://localhost:7777/api/v1/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

### Signup
```bash
POST http://localhost:7777/api/v1/auth/signup
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "password": "password123",
  "position": "Developer",
  "role": "member"
}
```

### Change Password
```bash
PUT http://localhost:7777/api/v1/auth/password
Authorization: Bearer <token>
Content-Type: application/json

{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

### Get Current User
```bash
GET http://localhost:7777/api/v1/auth/me
Authorization: Bearer <token>
```


