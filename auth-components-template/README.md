# Authentication Components - Ready to Use

This folder contains all authentication-related components for a Spring Boot project.

## 📁 Structure

```
auth-components-template/
├── src/main/java/com/eska/motive/crew/ws/
│   ├── entity/
│   │   └── User.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── dto/request/
│   │   ├── LoginRequest.java
│   │   ├── SignupRequest.java
│   │   └── ChangePasswordRequest.java
│   ├── util/
│   │   └── JWTUtil.java
│   ├── exception/
│   │   ├── ResourceNotFoundException.java
│   │   ├── ValidationException.java
│   │   ├── InternalErrorException.java
│   │   └── ExceptionHandlerAdvice.java
│   ├── service/
│   │   └── AuthService.java
│   └── controller/v1/
│       └── AuthController.java
└── src/main/resources/db/migration/
    └── V1__create_users_table.sql
```

## 🚀 How to Use

1. **Copy files to your project:**
   ```bash
   # Copy all Java files to your project's src/main/java/com/eska/motive/crew/ws/ directory
   cp -r src/main/java/com/eska/motive/crew/ws/* /path/to/your/project/src/main/java/com/eska/motive/crew/ws/
   ```

2. **Add JWT configuration to `application.properties`:**
   ```properties
   jwt.secret=your-secret-key-here-change-this-in-production-make-it-long-and-secure-at-least-256-bits
   jwt.expiration=86400000
   ```

3. **Ensure dependencies in `build.gradle`:**
   ```gradle
   dependencies {
       implementation 'org.springframework.boot:spring-boot-starter-security'
       implementation 'org.springframework.boot:spring-boot-starter-web'
       implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
       implementation 'io.jsonwebtoken:jjwt:0.12.6'
       compileOnly 'org.projectlombok:lombok'
       annotationProcessor 'org.projectlombok:lombok'
   }
   ```

4. **Configure SecurityConfig** (if not already done):
   - See `SETUP_NEW_PROJECT.md` for SecurityConfig example

5. **Run database migration:**
   - Copy `V1__create_users_table.sql` to your migration folder
   - Or run it manually in your database

## 📝 API Endpoints

### Login
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

### Signup
```
POST /api/v1/auth/signup
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
```
PUT /api/v1/auth/password
Authorization: Bearer <token>
Content-Type: application/json

{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

### Get Current User
```
GET /api/v1/auth/me
Authorization: Bearer <token>
```

## ✅ Features

- ✅ User registration (signup)
- ✅ User login with JWT token
- ✅ Password change
- ✅ Get current user info
- ✅ Email validation
- ✅ Password encryption (BCrypt)
- ✅ JWT token generation and validation
- ✅ Global exception handling
- ✅ Input validation

## 🔒 Security Notes

1. **Change JWT Secret:** Update `jwt.secret` in `application.properties` with a strong, random secret key
2. **Password Requirements:** Currently minimum 6 characters - adjust as needed
3. **Token Expiration:** Default is 24 hours - adjust in `application.properties`
4. **HTTPS:** Use HTTPS in production
5. **CORS:** Configure CORS properly for production

## 📚 Next Steps

1. Add email verification
2. Add password reset functionality
3. Add refresh token mechanism
4. Add rate limiting
5. Add logging and monitoring
6. Add unit tests


