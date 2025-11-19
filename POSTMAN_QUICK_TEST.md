# Quick Postman Test Guide

## The Problem
You tried to send credentials to `/api/v1/members`, but:
- It's a **GET** endpoint (not POST)
- It requires **JWT token** in Authorization header (not credentials)

## Solution: Two-Step Process

### Step 1: Get JWT Token (Login or Signup)

#### Option A: Signup (Create New User)
```
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

**Copy the token from response:**
```json
{
  "authenticationData": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzM..."
  }
}
```

#### Option B: Login (If User Exists)
```
POST http://localhost:7777/public/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```

**Copy the token from response**

### Step 2: Use Token to Access Members

```
GET http://localhost:7777/api/v1/members
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzM...
```

**Important:** 
- Method: **GET** (not POST)
- Header: `Authorization: Bearer <your-token>`
- No body needed

## Postman Setup

### Request 1: Signup
1. Create new request
2. Method: **POST**
3. URL: `http://localhost:7777/api/v1/auth/signup`
4. Headers: `Content-Type: application/json`
5. Body (raw JSON):
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
6. Send → Copy the token from `authenticationData.token`

### Request 2: Get Members
1. Create new request
2. Method: **GET**
3. URL: `http://localhost:7777/api/v1/members`
4. Headers: 
   - `Authorization: Bearer <paste-token-here>`
5. No body needed
6. Send

## Common Errors

### 401 Unauthorized
- **Cause:** Missing or invalid token
- **Fix:** Make sure you:
  1. Logged in/signed up first
  2. Copied the token correctly
  3. Included "Bearer " prefix

### Connection Refused
- **Cause:** Server not running
- **Fix:** Start server: `./gradlew bootRun`

### User Not Found
- **Cause:** User doesn't exist
- **Fix:** Sign up first at `/api/v1/auth/signup`

