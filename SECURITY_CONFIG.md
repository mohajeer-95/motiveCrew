# Security Configuration Documentation

## Overview

The application now uses JWT (JSON Web Token) based authentication with Spring Security. All endpoints are secured except for public endpoints like login, signup, and health checks.

## Security Features

### 1. **JWT Authentication Filter**
- Validates JWT tokens from the `Authorization` header
- Extracts user information from the token
- Loads user from database and sets Spring Security context
- Checks if user is active before allowing access

### 2. **Security Configuration**
- **Stateless Session Management**: Uses JWT tokens (no server-side sessions)
- **CSRF Disabled**: Not needed for stateless JWT authentication
- **Public Endpoints**: Login, signup, health checks
- **Protected Endpoints**: All other endpoints require valid JWT token

## Public Endpoints (No Authentication Required)

The following endpoints are publicly accessible:

- `/public/**` - Public login endpoint
- `/api/v1/health/**` - Health check endpoints
- `/api/v1/auth/signup` - User registration
- `/actuator/**` - Spring Boot Actuator
- `/error` - Error pages
- `/swagger-ui/**` - Swagger UI (if enabled)
- `/v3/api-docs/**` - OpenAPI documentation (if enabled)

## Protected Endpoints (Require JWT Token)

All other endpoints require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

## How Authentication Works

### 1. **User Login**
```
POST /public/login
Body: { "username": "user@example.com", "password": "password" }
Response: { "authenticationData": { "token": "eyJhbGc..." } }
```

### 2. **Using the Token**
Include the token in subsequent requests:
```
GET /api/v1/members
Headers: Authorization: Bearer eyJhbGc...
```

### 3. **Token Validation Flow**
1. Client sends request with `Authorization: Bearer <token>`
2. `JwtAuthenticationFilter` intercepts the request
3. Extracts and validates the JWT token
4. Extracts username (email) from token
5. Loads user from database
6. Checks if user is active
7. Sets authentication context with user details and roles
8. Request proceeds to controller

## User Roles

Users have roles that are set in the authentication context:
- `ROLE_ADMIN` - Admin users
- `ROLE_MEMBER` - Regular members

These roles can be used for method-level security (if needed in the future).

## Security Context

After successful authentication, the `SecurityContext` contains:
- **Principal**: User entity object
- **Authorities**: User roles (ROLE_ADMIN or ROLE_MEMBER)
- **Authenticated**: true

You can access the current user in controllers:
```java
@Autowired
private AuthService authService;

@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
    User user = authService.getCurrentUser(token);
    // Use user...
}
```

## Error Handling

### Invalid Token
- Returns 401 Unauthorized
- Security context is cleared
- Request is rejected

### Inactive User
- Token is valid but user is inactive
- Security context is cleared
- Request is rejected

### Missing Token
- For protected endpoints: Returns 401 Unauthorized
- For public endpoints: Request proceeds normally

## Testing Authentication

### 1. Test Public Endpoint (No Token)
```bash
curl http://localhost:7777/api/v1/health
# Should work - returns 200 OK
```

### 2. Test Protected Endpoint (No Token)
```bash
curl http://localhost:7777/api/v1/members
# Should fail - returns 401 Unauthorized
```

### 3. Test Protected Endpoint (With Token)
```bash
# First, login to get token
curl -X POST http://localhost:7777/public/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password"}'

# Use the token from response
curl http://localhost:7777/api/v1/members \
  -H "Authorization: Bearer <your-token>"
# Should work - returns 200 OK with data
```

## Configuration Details

### SecurityConfig
- **Session Creation Policy**: STATELESS (no server-side sessions)
- **CSRF**: Disabled (not needed for JWT)
- **Form Login**: Disabled (using JWT instead)
- **HTTP Basic**: Disabled (using JWT instead)

### JwtAuthenticationFilter
- Extends `OncePerRequestFilter` (runs once per request)
- Validates JWT token signature and expiration
- Loads user from database
- Sets Spring Security authentication context
- Handles errors gracefully

## Security Best Practices

1. **Token Expiration**: Tokens expire after 24 hours (configurable in JWTUtil)
2. **Password Hashing**: Uses BCryptPasswordEncoder for password storage
3. **User Validation**: Checks if user is active before authentication
4. **Stateless**: No server-side session storage
5. **HTTPS**: In production, always use HTTPS to protect tokens in transit

## Future Enhancements

- Token refresh mechanism
- Role-based method security
- Rate limiting
- Token blacklisting (for logout)
- Remember me functionality

