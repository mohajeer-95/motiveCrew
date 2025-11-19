# Postman Login Test Guide

## Login Endpoint

**URL:** `http://localhost:7777/public/login`  
**Method:** `POST`  
**Authentication:** Not required (public endpoint)

## Postman Setup

### Step 1: Create New Request
1. Open Postman
2. Click **"New"** → **"HTTP Request"**
3. Set method to **POST**

### Step 2: Enter URL
```
http://localhost:7777/public/login
```

### Step 3: Set Headers
Click on **"Headers"** tab and add:
- **Key:** `Content-Type`
- **Value:** `application/json`

### Step 4: Set Body
1. Click on **"Body"** tab
2. Select **"raw"**
3. Select **"JSON"** from dropdown
4. Paste this JSON:

```json
{
  "username": "test@example.com",
  "password": "password123",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```

### Step 5: Send Request
Click **"Send"** button

## Expected Response

### Success Response (200 OK)
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Operation done Successfully",
  "error": false,
  "authenticationData": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzYzMzA1MzE1LCJleHAiOjE3NjMzOTE3MTV9.BduZdW5QW0Jsz0sswS7ULF6qlpOqmV5KMxEiKssOD2vFQQRzCIJsXbE6cPDqy5i2ysj44TgzWe8hpdwHKsToYw"
  }
}
```

### Error Response (404 Not Found)
```json
{
  "statusCode": "MOTIVE-CREW-0001",
  "message": "User not found",
  "error": true
}
```

## Save the Token

**Important:** Copy the token from `authenticationData.token` - you'll need it for other API calls!

## Using the Token

After login, use the token in other requests:

1. Create a new request (e.g., GET members)
2. Go to **"Headers"** tab
3. Add:
   - **Key:** `Authorization`
   - **Value:** `Bearer <paste-your-token-here>`

Example:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzYzMzA1MzE1LCJleHAiOjE3NjMzOTE3MTV9.BduZdW5QW0Jsz0sswS7ULF6qlpOqmV5KMxEiKssOD2vFQQRzCIJsXbE6cPDqy5i2ysj44TgzWe8hpdwHKsToYw
```

## Quick Test

### Test with cURL
```bash
curl -X POST http://localhost:7777/public/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "password123",
    "eskaCoreToken": "dummy",
    "sessionId": "dummy"
  }'
```

## Notes

- **username:** Use the email address of the user
- **password:** The password (currently not validated in login, but required in request)
- **eskaCoreToken:** Required field (use "dummy" for testing)
- **sessionId:** Required field (use "dummy" for testing)

## Troubleshooting

### Connection Refused
- Make sure the application is running: `./gradlew bootRun`

### User Not Found
- User must exist in database
- Use signup endpoint first: `POST /api/v1/auth/signup`

### 401 Unauthorized
- Check if username (email) is correct
- Make sure user is active in database

