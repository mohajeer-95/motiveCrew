# Setup New Project - Structure Guide

## Quick Start: Replicate Motive Crew Backend Structure

### Step 1: Create Spring Boot Project

**Option A: Using Spring Initializr**
1. Go to https://start.spring.io/
2. Select:
   - **Project:** Gradle Project
   - **Language:** Java
   - **Spring Boot:** 3.4.0
   - **Packaging:** Jar
   - **Java:** 17
   - **Group:** com.eska.motive.crew
   - **Artifact:** motive-crew-ws
   - **Name:** motive-crew-ws
   - **Package name:** com.eska.motive.crew.ws

3. **Dependencies:**
   - Spring Web
   - Spring Data JPA
   - Spring Security
   - MySQL Driver
   - Lombok
   - Spring Boot Actuator

4. Download and extract

**Option B: Using IDE (IntelliJ IDEA)**
1. File → New → Project
2. Spring Initializr
3. Follow same settings as above

### Step 2: Create Package Structure

Create the following packages in `src/main/java/com/eska/motive/crew/ws/`:

```
com.eska.motive.crew.ws
├── config
├── controller
│   └── v1
├── dto
│   ├── request
│   └── response
├── entity
├── repository
├── service
├── exception
├── filter
├── util
├── utility
├── validation
└── enums
```

### Step 3: Add Dependencies to `build.gradle`

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'com.mysql:mysql-connector-j:8.3.0'
    
    implementation 'org.modelmapper:modelmapper:3.1.1'
    
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    implementation 'io.jsonwebtoken:jjwt:0.12.6'
    
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

### Step 4: Create Configuration Files

#### `src/main/resources/application.properties`
```properties
spring.application.name=motive-crew-ws
spring.profiles.active=dev
```

#### `src/main/resources/application-dev.properties`
```properties
server.port=7777
server.address=0.0.0.0
server.error.include-binding-errors=always

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/your_database?allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Disable chunked encoding
server.http2.enabled=false
server.compression.enabled=false
```

### Step 5: Create Core Configuration Classes

#### `config/SecurityConfig.java`
```java
package com.eska.motive.crew.ws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/api/v1/health/**").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### `config/WebConfig.java`
```java
package com.eska.motive.crew.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
```

### Step 6: Create Exception Handling

#### `exception/ResourceNotFoundException.java`
```java
package com.eska.motive.crew.ws.exception;

import com.eska.motive.crew.contract.StatusCode;

public class ResourceNotFoundException extends RuntimeException {
    private final StatusCode statusCode;
    
    public ResourceNotFoundException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public StatusCode getStatusCode() {
        return statusCode;
    }
}
```

#### `exception/ExceptionHandlerAdvice.java`
```java
package com.eska.motive.crew.ws.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }
    
    // Add more exception handlers as needed
}
```

### Step 7: Create Sample Entity, Repository, Service, Controller

#### `entity/User.java`
```java
package com.eska.motive.crew.ws.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

#### `repository/UserRepository.java`
```java
package com.eska.motive.crew.ws.repository;

import com.eska.motive.crew.ws.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

#### `service/UserService.java`
```java
package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    // Add more methods as needed
}
```

#### `controller/v1/UserController.java`
```java
package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
```

### Step 8: Create Health Check Controller

#### `controller/v1/HealthController.java`
```java
package com.eska.motive.crew.ws.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "motive-crew-ws");
        return ResponseEntity.ok(response);
    }
}
```

### Step 9: Create Database Migration Directory

Create `src/main/resources/db/migration/` and add your SQL migration files:
- `V1__create_tables.sql`
- `V2__insert_initial_data.sql`

### Step 10: Create Uploads Directory

Create `uploads/` directory at the root of the project for file uploads.

### Step 11: Run the Application

```bash
./gradlew bootRun
```

Or in IDE, run `MotiveCrewWsApplication.java`

### Step 12: Test

```bash
curl http://localhost:7777/api/v1/health
```

## Next Steps

1. **Add JWT Authentication**
   - Create `JWTUtil.java` in `util/`
   - Create `JwtAuthenticationFilter.java` in `filter/`
   - Update `SecurityConfig.java`

2. **Add More Entities**
   - Create entities in `entity/`
   - Create repositories in `repository/`
   - Create services in `service/`
   - Create controllers in `controller/v1/`

3. **Add DTOs**
   - Create request DTOs in `dto/request/`
   - Create response DTOs in `dto/response/`

4. **Add Validation**
   - Add validation annotations to DTOs
   - Create custom validators in `validation/`

5. **Add Exception Handling**
   - Extend `ExceptionHandlerAdvice`
   - Add more custom exceptions

## File Naming Conventions

- **Controllers:** `*Controller.java` (e.g., `UserController.java`)
- **Services:** `*Service.java` (e.g., `UserService.java`)
- **Repositories:** `*Repository.java` (e.g., `UserRepository.java`)
- **Entities:** PascalCase (e.g., `User.java`)
- **DTOs:** `*Request.java`, `*DTO.java` (e.g., `CreateUserRequest.java`)

## Package Naming Conventions

- Base package: `com.eska.motive.crew.ws`
- Controllers: `com.eska.motive.crew.ws.controller.v1`
- Services: `com.eska.motive.crew.ws.service`
- Repositories: `com.eska.motive.crew.ws.repository`
- Entities: `com.eska.motive.crew.ws.entity`
- DTOs: `com.eska.motive.crew.ws.dto.request` / `dto.response`
- Config: `com.eska.motive.crew.ws.config`
- Exception: `com.eska.motive.crew.ws.exception`
- Filter: `com.eska.motive.crew.ws.filter`
- Util: `com.eska.motive.crew.ws.util`

## Common Annotations

- **Controller:** `@RestController`, `@RequestMapping("/api/v1/...")`
- **Service:** `@Service`, `@Transactional`
- **Repository:** `@Repository` (optional)
- **Entity:** `@Entity`, `@Table(name = "...")`, `@Id`, `@GeneratedValue`
- **DTO:** `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **Validation:** `@NotNull`, `@NotBlank`, `@Email`, `@Size`, etc.

## Tips

1. Use Lombok to reduce boilerplate code
2. Use `@RequiredArgsConstructor` for dependency injection
3. Use `@Transactional` on service methods that modify data
4. Use `@Builder` pattern for entities and DTOs
5. Keep controllers thin - delegate to services
6. Use DTOs for API input/output, not entities directly
7. Use proper HTTP status codes
8. Add proper error handling
9. Use pagination for list endpoints
10. Add logging for debugging


