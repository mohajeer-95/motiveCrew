# Application Status

## ✅ Application is Running

**Status:** Application is running on port **7777**

**Health Check:**
```bash
curl http://localhost:7777/api/v1/health
```

**Response:**
```json
{
  "status": "UP",
  "message": "Motive Crew API is running",
  "timestamp": 1763305259319
}
```

## Database Configuration

**Database:** MySQL
- **Host:** 192.168.0.51
- **Port:** 3306
- **Database Name:** motive_crew
- **Username:** motive_crew
- **Password:** motive_crew

**Connection URL:**
```
jdbc:mysql://192.168.0.51:3306/motive_crew
```

## Quick Test Commands

### 1. Health Check (No Auth Required)
```bash
curl http://localhost:7777/api/v1/health
```

### 2. Signup (Create User)
```bash
curl -X POST http://localhost:7777/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "phone": "0790000000",
    "position": "Developer",
    "role": "admin"
  }'
```

### 3. Login (Get Token)
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

### 4. Get Members (Requires Token)
```bash
# Replace TOKEN with actual token from login response
curl http://localhost:7777/api/v1/members \
  -H "Authorization: Bearer TOKEN"
```

## Application Process

The application is running in the background. To stop it:
```bash
# Find the process
ps aux | grep bootRun

# Kill the process (replace PID with actual process ID)
kill <PID>
```

Or use:
```bash
pkill -f "MotiveCrewWsApplication"
```

## Next Steps

1. ✅ Application is running
2. ✅ Database configured (MySQL)
3. ✅ Health endpoint working
4. ⏳ Test signup/login endpoints
5. ⏳ Test protected endpoints with JWT token

