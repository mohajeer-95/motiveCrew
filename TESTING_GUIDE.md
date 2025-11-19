# API Testing Guide

## Issue: Testing `/api/v1/members` endpoint

The `/api/v1/members` endpoint is a **GET** request that requires **JWT authentication**. You cannot send credentials directly to it.

## Correct Testing Flow

### Step 1: Start the Application

```bash
cd /Users/eska-wireless/Wikidenia/MotiveCrew/motive-crew-ws
./gradlew bootRun
```

Wait for: `Started MotiveCrewWsApplication in X.XXX seconds`

### Step 2: Create a User (Signup) - First Time Only

**Endpoint:** `POST /api/v1/auth/signup`

**Request:**
```json
POST http://localhost:7777/api/v1/auth/signup
Content-Type: application/json

{
  "name": "Test User",
  "email": "user@example.com",
  "password": "password123",
  "phone": "0790000000",
  "position": "Developer",
  "role": "admin"
}
```

**Response:**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "User registered successfully",
  "error": false,
  "authenticationData": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

**Save the token from the response!**

### Step 3: Login (If User Already Exists)

**Endpoint:** `POST /public/login`

**Request:**
```json
POST http://localhost:7777/public/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```

**Response:**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Operation done Successfully",
  "error": false,
  "authenticationData": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

**Save the token from the response!**

### Step 4: Use the Token to Access Protected Endpoints

**Endpoint:** `GET /api/v1/members`

**Request:**
```
GET http://localhost:7777/api/v1/members
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response:**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Members retrieved successfully",
  "error": false,
  "data": [...],
  "totalElements": 0,
  "totalPages": 0,
  "currentPage": 0
}
```

## Postman Setup

### 1. Create Signup Request
- **Method:** POST
- **URL:** `http://localhost:7777/api/v1/auth/signup`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "name": "Test User",
  "email": "user@example.com",
  "password": "password123",
  "phone": "0790000000",
  "position": "Developer",
  "role": "admin"
}
```
- **Save the token from response**

### 2. Create Login Request
- **Method:** POST
- **URL:** `http://localhost:7777/public/login`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "username": "user@example.com",
  "password": "password123",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```
- **Save the token from response**

### 3. Create Get Members Request
- **Method:** GET
- **URL:** `http://localhost:7777/api/v1/members`
- **Headers:** 
  - `Authorization: Bearer <your-token-here>`
  - Replace `<your-token-here>` with the token from login/signup

## Common Errors

### Error: 401 Unauthorized
**Cause:** Missing or invalid JWT token
**Solution:** 
1. Make sure you logged in first
2. Copy the token from login response
3. Include it in Authorization header: `Bearer <token>`

### Error: Connection Refused
**Cause:** Application is not running
**Solution:** Start the application with `./gradlew bootRun`

### Error: User Not Found
**Cause:** User doesn't exist in database
**Solution:** 
1. Sign up first at `/api/v1/auth/signup`
2. Or create user directly in database

### Error: Invalid Credentials
**Cause:** Wrong password or email
**Solution:** Check your credentials or sign up with new account

## Quick Test Commands (cURL)

```bash
# 1. Signup
curl -X POST http://localhost:7777/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "user@example.com",
    "password": "password123",
    "phone": "0790000000",
    "position": "Developer",
    "role": "admin"
  }'

# 2. Login (if user exists)
curl -X POST http://localhost:7777/public/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123",
    "eskaCoreToken": "dummy",
    "sessionId": "dummy"
  }'

# 3. Get Members (replace TOKEN with actual token)
curl -X GET http://localhost:7777/api/v1/members \
  -H "Authorization: Bearer TOKEN"
```

## Important Notes

1. **Token Expiration:** Tokens expire after 24 hours. You'll need to login again.
2. **Password:** The password you use for login must match the one used during signup.
3. **Email as Username:** Use the email address as the username in login requests.
4. **Bearer Token:** Always include "Bearer " prefix before the token in Authorization header.

