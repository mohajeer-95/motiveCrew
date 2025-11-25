# Motive Crew Backend - Project Structure

## 📁 Complete Directory Structure

```
MotiveCrew/
├── settings.gradle                          # Multi-module project configuration
├── build.gradle                             # Root build configuration
├── README.md                                # Project documentation
│
├── motive-crew-contract/                    # Contract module (shared DTOs, enums, etc.)
│   ├── build.gradle
│   ├── settings.gradle
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── eska/
│                       └── motive/
│                           └── crew/
│                               └── contract/
│                                   ├── StatusCode.java          # Status code enums
│                                   └── ...                      # Other shared contracts
│
└── motive-crew-ws/                          # Web Service Module (Main Backend)
    ├── build.gradle                         # Gradle build configuration
    ├── gradle.properties                    # Gradle properties
    ├── gradlew                              # Gradle wrapper (Unix)
    ├── gradlew.bat                          # Gradle wrapper (Windows)
    ├── Dockerfile                           # Docker configuration
    │
    ├── gradle/
    │   └── wrapper/
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    │
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/
    │   │   │       └── eska/
    │   │   │           └── motive/
    │   │   │               └── crew/
    │   │   │                   └── ws/
    │   │   │                       ├── MotiveCrewWsApplication.java    # Main Spring Boot application
    │   │   │                       │
    │   │   │                       ├── config/                          # Configuration classes
    │   │   │                       │   ├── AppConfig.java
    │   │   │                       │   ├── ModelMapperConfig.java
    │   │   │                       │   ├── SecurityConfig.java         # Spring Security configuration
    │   │   │                       │   └── WebConfig.java              # CORS, static resources
    │   │   │                       │
    │   │   │                       ├── controller/                     # REST Controllers
    │   │   │                       │   └── v1/
    │   │   │                       │       ├── AuthController.java     # Authentication endpoints
    │   │   │                       │       ├── DashboardController.java
    │   │   │                       │       ├── EventController.java
    │   │   │                       │       ├── ExpenseController.java
    │   │   │                       │       ├── HealthController.java
    │   │   │                       │       ├── MemberController.java
    │   │   │                       │       ├── NotificationController.java
    │   │   │                       │       ├── PollController.java
    │   │   │                       │       ├── PostController.java     # Corporate/Team Feed
    │   │   │                       │       ├── ReportController.java
    │   │   │                       │       ├── SettingsController.java
    │   │   │                       │       └── TeamController.java     # Team management
    │   │   │                       │
    │   │   │                       ├── dto/                            # Data Transfer Objects
    │   │   │                       │   ├── request/                    # Request DTOs
    │   │   │                       │   │   ├── ChangePasswordRequest.java
    │   │   │                       │   │   ├── CreateAnnouncementRequest.java
    │   │   │                       │   │   ├── CreateCommentRequest.java
    │   │   │                       │   │   ├── CreateEventRequest.java
    │   │   │                       │   │   ├── CreateExpenseRequest.java
    │   │   │                       │   │   ├── CreateMemberRequest.java
    │   │   │                       │   │   ├── CreatePollRequest.java
    │   │   │                       │   │   ├── CreatePostRequest.java
    │   │   │                       │   │   ├── MarkPaymentRequest.java
    │   │   │                       │   │   ├── SignupRequest.java
    │   │   │                       │   │   ├── UpdateMemberRequest.java
    │   │   │                       │   │   ├── UpdatePreferencesRequest.java
    │   │   │                       │   │   └── VotePollRequest.java
    │   │   │                       │   └── response/                   # Response DTOs
    │   │   │                       │       └── UserDTO.java
    │   │   │                       │
    │   │   │                       ├── entity/                         # JPA Entities
    │   │   │                       │   ├── Announcement.java
    │   │   │                       │   ├── Event.java
    │   │   │                       │   ├── EventParticipant.java
    │   │   │                       │   ├── Expense.java
    │   │   │                       │   ├── MemberPayment.java
    │   │   │                       │   ├── MonthlyCollection.java
    │   │   │                       │   ├── Notification.java
    │   │   │                       │   ├── Poll.java
    │   │   │                       │   ├── PollOption.java
    │   │   │                       │   ├── PollVote.java
    │   │   │                       │   ├── Post.java                   # Corporate/Team Feed posts
    │   │   │                       │   ├── PostComment.java
    │   │   │                       │   ├── PostLike.java
    │   │   │                       │   ├── Team.java                   # Team entity
    │   │   │                       │   ├── User.java                   # User entity
    │   │   │                       │   └── UserPreferences.java
    │   │   │                       │
    │   │   │                       ├── repository/                     # JPA Repositories
    │   │   │                       │   ├── AnnouncementRepository.java
    │   │   │                       │   ├── EventParticipantRepository.java
    │   │   │                       │   ├── EventRepository.java
    │   │   │                       │   ├── ExpenseRepository.java
    │   │   │                       │   ├── MemberPaymentRepository.java
    │   │   │                       │   ├── MonthlyCollectionRepository.java
    │   │   │                       │   ├── NotificationRepository.java
    │   │   │                       │   ├── PollOptionRepository.java
    │   │   │                       │   ├── PollRepository.java
    │   │   │                       │   ├── PollVoteRepository.java
    │   │   │                       │   ├── PostCommentRepository.java
    │   │   │                       │   ├── PostLikeRepository.java
    │   │   │                       │   ├── PostRepository.java
    │   │   │                       │   ├── TeamRepository.java
    │   │   │                       │   ├── UserPreferencesRepository.java
    │   │   │                       │   └── UserRepository.java
    │   │   │                       │
    │   │   │                       ├── service/                         # Business Logic Services
    │   │   │                       │   ├── AuthService.java             # Authentication logic
    │   │   │                       │   ├── ContributionService.java
    │   │   │                       │   ├── DashboardService.java
    │   │   │                       │   ├── EventService.java
    │   │   │                       │   ├── ExpenseService.java
    │   │   │                       │   ├── LoginService.java
    │   │   │                       │   ├── MemberService.java
    │   │   │                       │   ├── NotificationService.java
    │   │   │                       │   ├── PollService.java
    │   │   │                       │   ├── PostService.java             # Corporate/Team Feed logic
    │   │   │                       │   ├── ProductService.java
    │   │   │                       │   ├── ReportService.java
    │   │   │                       │   ├── SettingsService.java
    │   │   │                       │   └── TeamService.java             # Team management logic
    │   │   │                       │
    │   │   │                       ├── exception/                       # Exception Handling
    │   │   │                       │   ├── ExceptionHandlerAdvice.java # Global exception handler
    │   │   │                       │   ├── InternalErrorException.java
    │   │   │                       │   ├── ResourceNotFoundException.java
    │   │   │                       │   └── ValidationException.java
    │   │   │                       │
    │   │   │                       ├── filter/                          # Servlet Filters
    │   │   │                       │   └── JwtAuthenticationFilter.java  # JWT token validation
    │   │   │                       │
    │   │   │                       ├── util/                            # Utility Classes
    │   │   │                       │   └── JWTUtil.java                 # JWT token utilities
    │   │   │                       │
    │   │   │                       ├── utility/                         # Additional utilities
    │   │   │                       │   └── Utility.java
    │   │   │                       │
    │   │   │                       ├── validation/                      # Validation classes
    │   │   │                       │   ├── LoginRequestValidator.java
    │   │   │                       │   └── Validator.java
    │   │   │                       │
    │   │   │                       └── enums/                           # Enumerations
    │   │   │                           └── PollStatus.java
    │   │   │
    │   │   └── resources/                                             # Configuration & Resources
    │   │       ├── application.properties                             # Main application config
    │   │       ├── application-dev.properties                         # Development profile
    │   │       ├── application-qa.properties                          # QA profile
    │   │       ├── banner.txt                                         # Spring Boot banner
    │   │       ├── db/
    │   │       │   └── migration/                                     # Database migrations
    │   │       │       ├── V1__create_tables.sql
    │   │       │       └── V2__insert_initial_data.sql
    │   │       ├── static/                                            # Static resources
    │   │       └── templates/                                         # Template files
    │   │
    │   └── test/                                                      # Test files
    │       └── java/
    │           └── com/
    │               └── eska/
    │                   └── cxm/
    │                       └── uiws/
    │                           └── CxmUiWsApplicationTests.java
    │
    └── uploads/                                                       # Uploaded files directory
        └── avatars/                                                   # User avatars
```

## 📋 Key Files Explained

### 1. **Root Level Files**

#### `settings.gradle`
- Defines multi-module project structure
- Includes `motive-crew-contract` and `motive-crew-ws` modules

#### `build.gradle` (Root)
- Root-level build configuration
- Common dependencies and plugins

### 2. **Main Application**

#### `MotiveCrewWsApplication.java`
- Main Spring Boot application class
- Entry point for the application
- `@SpringBootApplication` annotation with base package scanning

### 3. **Configuration (`config/`)**

#### `SecurityConfig.java`
- Spring Security configuration
- JWT authentication setup
- Public/private endpoint definitions
- Password encoder configuration

#### `WebConfig.java`
- CORS configuration
- Static resource handlers (uploads, etc.)
- Web MVC configuration

#### `ModelMapperConfig.java`
- ModelMapper bean configuration for DTO mapping

#### `AppConfig.java`
- General application configuration beans

### 4. **Controllers (`controller/v1/`)**

All REST controllers follow the pattern:
- `@RestController`
- `@RequestMapping("/api/v1/...")`
- Inject services
- Handle HTTP requests/responses
- Return DTOs

**Key Controllers:**
- `AuthController` - Login, signup, password management
- `PostController` - Corporate/Team feed endpoints
- `TeamController` - Team management
- `DashboardController` - Dashboard data
- `EventController` - Event management
- `ExpenseController` - Expense tracking
- `MemberController` - Member management
- `NotificationController` - Notifications
- `PollController` - Polls/voting
- `SettingsController` - User settings
- `ReportController` - Reports
- `HealthController` - Health check endpoint

### 5. **Services (`service/`)**

Business logic layer:
- `@Service` annotation
- Inject repositories
- Implement business rules
- Handle transactions (`@Transactional`)
- Throw custom exceptions

**Key Services:**
- `AuthService` - Authentication, JWT generation
- `PostService` - Feed post logic
- `TeamService` - Team operations
- `DashboardService` - Dashboard aggregations
- `EventService` - Event management
- `ExpenseService` - Expense calculations
- `MemberService` - Member operations
- `NotificationService` - Notification logic
- `PollService` - Poll operations
- `SettingsService` - User preferences

### 6. **Repositories (`repository/`)**

Data access layer:
- Extend `JpaRepository<Entity, ID>`
- Custom query methods
- `@Repository` annotation (optional, Spring auto-detects)

**Key Repositories:**
- `UserRepository` - User queries
- `PostRepository` - Post queries (corporate/team feed)
- `TeamRepository` - Team queries
- `EventRepository` - Event queries
- `ExpenseRepository` - Expense queries
- And more...

### 7. **Entities (`entity/`)**

JPA entities:
- `@Entity` annotation
- `@Table(name = "...")`
- `@Column` annotations
- Relationships (`@ManyToOne`, `@OneToMany`, etc.)
- Lombok annotations (`@Data`, `@Builder`, etc.)

**Key Entities:**
- `User` - User information
- `Team` - Team information
- `Post` - Feed posts
- `PostLike` - Post likes
- `PostComment` - Post comments
- `Event` - Events
- `Expense` - Expenses
- `Poll` - Polls
- And more...

### 8. **DTOs (`dto/`)**

Data Transfer Objects:
- Request DTOs in `dto/request/`
- Response DTOs in `dto/response/`
- Used for API input/output
- Validation annotations

### 9. **Exception Handling (`exception/`)**

- `ExceptionHandlerAdvice` - Global `@ControllerAdvice`
- Custom exceptions:
  - `ResourceNotFoundException`
  - `ValidationException`
  - `InternalErrorException`

### 10. **Filters (`filter/`)**

- `JwtAuthenticationFilter` - Validates JWT tokens
- Extends `OncePerRequestFilter`
- Sets Spring Security context

### 11. **Utilities (`util/`, `utility/`)**

- `JWTUtil` - JWT token generation/validation
- `Utility` - General utilities

### 12. **Resources (`resources/`)**

#### `application.properties`
- Main configuration
- Sets active profile: `spring.profiles.active=dev`

#### `application-dev.properties`
- Development environment config
- Database connection
- Server port and address
- JPA settings
- CORS settings

#### `application-qa.properties`
- QA environment configuration

#### `db/migration/`
- Flyway/Liquibase migration scripts
- `V1__create_tables.sql` - Initial schema
- `V2__insert_initial_data.sql` - Seed data

## 🔧 Build Configuration

### `build.gradle` (motive-crew-ws)

**Key Dependencies:**
- Spring Boot 3.4.0
- Spring Security
- Spring Data JPA
- MySQL Connector
- Lombok
- JWT (jjwt)
- ModelMapper
- Spring Boot Actuator

**Java Version:** 17

**Plugins:**
- `spring-boot`
- `java`
- `io.spring.dependency-management`
- `maven-publish`

## 📦 Project Structure Pattern

### Standard Layered Architecture:

```
Controller (REST API)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Entity (Database)
```

### Request Flow:

1. **Client** → REST Controller
2. **Controller** → Service (validates, processes)
3. **Service** → Repository (queries database)
4. **Repository** → Database
5. **Response** flows back through layers

### Package Naming Convention:

```
com.eska.motive.crew.ws
├── config          # Configuration classes
├── controller      # REST controllers
├── service         # Business logic
├── repository      # Data access
├── entity          # JPA entities
├── dto             # Data transfer objects
├── exception       # Exception handling
├── filter          # Servlet filters
├── util            # Utilities
└── validation      # Validation logic
```

## 🚀 How to Replicate This Structure

1. **Create Spring Boot Project**
   - Use Spring Initializr or IDE
   - Java 17, Spring Boot 3.4.0
   - Dependencies: Web, JPA, Security, MySQL

2. **Set Up Multi-Module Structure** (if needed)
   - Create `settings.gradle`
   - Create contract module for shared code

3. **Create Package Structure**
   ```
   com.eska.motive.crew.ws
   ├── config
   ├── controller/v1
   ├── service
   ├── repository
   ├── entity
   ├── dto/request
   ├── dto/response
   ├── exception
   ├── filter
   ├── util
   └── validation
   ```

4. **Add Configuration Files**
   - `application.properties`
   - `application-dev.properties`
   - `SecurityConfig.java`
   - `WebConfig.java`

5. **Implement Layers**
   - Entities first
   - Repositories
   - Services
   - Controllers
   - DTOs

6. **Add Security**
   - JWT filter
   - Security config
   - JWT utilities

7. **Add Exception Handling**
   - Global exception handler
   - Custom exceptions

## 📝 Notes

- All controllers are versioned under `/api/v1/`
- JWT authentication is used for protected endpoints
- Lombok is used extensively for reducing boilerplate
- ModelMapper is used for entity-to-DTO conversion
- Database migrations are in `resources/db/migration/`
- Uploaded files are stored in `uploads/` directory
- CORS is configured to allow all origins (development)

## 🔗 Related Files

- See `README.md` for setup instructions
- See `API_SPECIFICATION.md` for API documentation
- See `QUICK_START.md` for quick start guide


