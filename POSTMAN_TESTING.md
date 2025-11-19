# Postman Testing Guide - Motive Crew API

## Base URL
```
http://localhost:7777
```

## Available Endpoints

### 1. Health Check (No Auth Required)
**GET** `/actuator/health`

**Example:**
```
GET http://localhost:7777/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP"  // or "DOWN" if database not connected
}
```

---

### 2. Login (No Auth Required - Public Endpoint)
**POST** `/public/login`

**Headers:**
```
Content-Type: application/json
```

**Request Body (All fields required):**
```json
{
  "username": "testuser",
  "eskaCoreToken": "dummy-token",
  "sessionId": "dummy-session"
}
```

**Example Request:**
```json
{
  "username": "mohammed.hajeer@company.com",
  "eskaCoreToken": "test-token-123",
  "sessionId": "test-session-456"
}
```

**Note:** `eskaCoreToken` and `sessionId` are required fields (even if empty strings), but since `eskaCoreAuthEnable=false`, you can use dummy values.

**Expected Response (Success):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Operation done Successfully",
  "error": false,
  "authenticationData": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTc2MzMwMTcwNywiZXhwIjoxNzYzMzg4MTA3fQ..."
  }
}
```

**Note:** The token in `authenticationData.token` can be used for authenticated requests.

**Expected Response (Error):**
```json
{
  "statusCode": 400,
  "message": "Validation failed",
  "error": true,
  "data": null
}
```

---

### 3. Product/Alignment Endpoints (Requires Auth - Currently Not Implemented)
**Note:** These endpoints return `null` currently, but here's the structure:

#### Get All Alignments
**GET** `/v1/alignment/all?viewType=1&type=1&productId=123`

**Headers:**
```
Authorization: Bearer <token_from_login>
Content-Type: application/json
```

#### Add Alignment
**POST** `/v1/alignment/add`

**Headers:**
```
Authorization: Bearer <token_from_login>
Content-Type: application/json
```

**Request Body:**
```json
{
  // ProductRequest structure
}
```

#### Edit Alignment
**PUT** `/v1/alignment/edit`

**Headers:**
```
Authorization: Bearer <token_from_login>
Content-Type: application/json
```

#### Delete Alignment
**DELETE** `/v1/alignment/delete?productId=123`

**Headers:**
```
Authorization: Bearer <token_from_login>
```

---

## Postman Collection Setup

### Step 1: Create a New Request

1. Open Postman
2. Click "New" → "HTTP Request"
3. Set method to **POST**
4. Enter URL: `http://localhost:7777/public/login`

### Step 2: Configure Headers

Go to **Headers** tab and add:
```
Key: Content-Type
Value: application/json
```

### Step 3: Configure Body

1. Go to **Body** tab
2. Select **raw**
3. Select **JSON** from dropdown
4. Paste this JSON:
```json
{
  "username": "testuser",
  "password": "testpass"
}
```

### Step 4: Send Request

Click **Send** button

---

## Quick Test Scenarios

### Test 1: Health Check
```
GET http://localhost:7777/actuator/health
```
**Expected:** `{"status":"UP"}` or `{"status":"DOWN"}`

### Test 2: Login (Minimal - All Required Fields)
```
POST http://localhost:7777/public/login
Body:
{
  "username": "user1",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```

### Test 3: Login (Full Example)
```
POST http://localhost:7777/public/login
Body:
{
  "username": "mohammed.hajeer@company.com",
  "eskaCoreToken": "test-token-123",
  "sessionId": "test-session-456"
}
```

---

## Troubleshooting

### Issue: Connection Refused
- **Solution:** Make sure the application is running on port 7777
- Check: `lsof -ti:7777`

### Issue: 401 Unauthorized
- **Solution:** The `/public/login` endpoint should not require auth. Check SecurityConfig.

### Issue: 500 Internal Server Error
- **Solution:** Check application logs. May be database connection issue.

### Issue: 400 Bad Request
- **Solution:** Check request body format. Must be valid JSON.

---

## Postman Environment Variables (Optional)

Create a Postman Environment with:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | http://localhost:7777 | http://localhost:7777 |
| `token` | (empty) | (will be set after login) |

Then use in requests:
```
{{base_url}}/public/login
```

### Auto-save Token Script (Postman Tests Tab)

After login request, add this in **Tests** tab:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.authenticationData && jsonData.authenticationData.token) {
        pm.environment.set("token", jsonData.authenticationData.token);
        console.log("Token saved:", jsonData.authenticationData.token);
    }
}
```

Then use token in other requests:
```
Authorization: Bearer {{token}}
```

---

## Example cURL Commands

### Health Check
```bash
curl http://localhost:7777/actuator/health
```

### Login
```bash
curl -X POST http://localhost:7777/public/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "eskaCoreToken": "dummy-token",
    "sessionId": "dummy-session"
  }'
```

### Login with Token in Variable
```bash
TOKEN=$(curl -s -X POST http://localhost:7777/public/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","eskaCoreToken":"dummy","sessionId":"dummy"}' | jq -r '.authenticationData.token')

echo "Token: $TOKEN"
```

---

## Notes

1. **Security:** Currently, all endpoints are open (security temporarily disabled for testing)
2. **Database:** Some endpoints may fail if database is not connected, but login should work
3. **JWT Token:** The login endpoint generates a JWT token that can be used for authenticated requests
4. **Validation:** The login endpoint validates the request, so make sure to send proper JSON structure

---

## Ready to Test!

1. ✅ Application is running on `http://localhost:7777`
2. ✅ Login endpoint is available at `/public/login`
3. ✅ No authentication required for `/public/*` endpoints
4. ✅ Use Postman to test the API

**Start with the Health Check, then try the Login endpoint!** 🚀

