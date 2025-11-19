# Testing with Real Users

## Users Inserted ✅

You've successfully inserted all team members into the database!

## Test Login with Real Users

### Admin Users (Can access all endpoints)

#### 1. Mohammed Hajeer (Admin)
```json
POST http://localhost:7777/public/login
Content-Type: application/json

{
  "username": "mohammed.hajeer@company.com",
  "password": "password123",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```

#### 2. Ashraf Matar (Admin)
```json
POST http://localhost:7777/public/login
Content-Type: application/json

{
  "username": "ashraf.matar@company.com",
  "password": "password123",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```

### Member Users (Regular access)

#### Example: Dana Sawalha
```json
POST http://localhost:7777/public/login
Content-Type: application/json

{
  "username": "dana.sawalha@company.com",
  "password": "password123",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```

## All Team Member Emails

You can login with any of these emails (password: `password123`):

**Admins:**
- mohammed.hajeer@company.com
- ashraf.matar@company.com

**Members:**
- firas.kamal@company.com
- ayat.hamdan@company.com
- ahmad.shlool@company.com
- munis.alawneh@company.com
- hamzeh.radaideh@company.com
- mohanned.sadiq@company.com
- hisham.almasri@company.com
- hisham.alzuraiqi@company.com
- dana.sawalha@company.com
- mesan.qawasmeh@company.com
- ahmad.juhaini@company.com
- khaled.taamneh@company.com
- reneh.madanat@company.com
- remah.alramahi@company.com
- alaa.altuhl@company.com
- farah.almasri@company.com
- nawal.zahran@company.com
- ibrahim.mansour@company.com
- mohammad.majdoub@company.com

## Next Steps

1. **Test Login** - Use any email above to login
2. **Get Token** - Copy the token from response
3. **Test Protected Endpoints** - Use token to access:
   - GET `/api/v1/members` - See all 21 members
   - GET `/api/v1/dashboard` - Dashboard data
   - GET `/api/v1/contributions/current` - Current month collection
   - etc.

## Postman Collection Setup

1. **Login Request:**
   - POST `http://localhost:7777/public/login`
   - Body: Use any user email above

2. **Get Members Request:**
   - GET `http://localhost:7777/api/v1/members`
   - Header: `Authorization: Bearer <token>`

3. **Get Dashboard:**
   - GET `http://localhost:7777/api/v1/dashboard`
   - Header: `Authorization: Bearer <token>`

## Password Note

**Default Password:** `password123` (BCrypt hashed)

If you want to change passwords:
- Use the signup endpoint to create new users
- Or use the change password API (after login)
- Or update directly in database (remember to hash with BCrypt)

## Verify Users in Database

Run in DBeaver:
```sql
SELECT id, name, email, role, is_active FROM users;
```

Should show all 21 team members!

