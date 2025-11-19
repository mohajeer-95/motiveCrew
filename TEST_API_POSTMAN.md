# Testing API with Postman

## Quick Test Endpoint

I've created a simple health check endpoint that you can test immediately without any database setup.

### 1. Health Check Endpoint

**Endpoint:** `GET /api/v1/health`

**URL:** `http://localhost:7777/api/v1/health`

**Method:** GET

**Headers:** None required

**Expected Response:**
```json
{
  "status": "UP",
  "message": "Motive Crew API is running",
  "timestamp": 1234567890123
}
```

**Status Code:** 200 OK

---

### 2. Health Check (Response Format)

**Endpoint:** `GET /api/v1/health/check`

**URL:** `http://localhost:7777/api/v1/health/check`

**Method:** GET

**Headers:** None required

**Expected Response:**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "API is healthy and running",
  "error": false
}
```

**Status Code:** 200 OK

---

## Postman Setup Instructions

### Step 1: Create a New Request
1. Open Postman
2. Click "New" → "HTTP Request"
3. Set method to **GET**

### Step 2: Enter the URL
```
http://localhost:7777/api/v1/health
```

### Step 3: Send the Request
1. Click "Send"
2. You should see a 200 OK response with JSON data

---

## Alternative: Using cURL

If you prefer command line:

```bash
# Health check endpoint
curl -X GET http://localhost:7777/api/v1/health

# Health check with Response format
curl -X GET http://localhost:7777/api/v1/health/check
```

---

## Verify Application is Running

Before testing, make sure the application is running:

1. Check if the application started successfully:
   ```bash
   cd /Users/eska-wireless/Wikidenia/MotiveCrew/motive-crew-ws
   ./gradlew bootRun
   ```

2. Look for this message in the logs:
   ```
   Started MotiveCrewWsApplication in X.XXX seconds
   ```

3. The application runs on port **7777** (as configured in `application-dev.properties`)

---

## Troubleshooting

### If you get "Connection refused":
- Make sure the application is running
- Check if port 7777 is available
- Verify the server started without errors

### If you get 404 Not Found:
- Check the URL path is correct: `/api/v1/health`
- Make sure there are no typos

### If you get 500 Internal Server Error:
- Check the application logs for errors
- Verify database connection (if using database endpoints)

---

## Next Steps

Once this endpoint works, you can test other endpoints:
- `/public/login` - Login endpoint
- `/api/v1/members` - Member endpoints (requires database)

---

## Server Information

- **Port:** 7777
- **Base URL:** `http://localhost:7777`
- **Health Endpoint:** `/api/v1/health`
- **Public Endpoints:** `/public/**`

