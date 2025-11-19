# Motive Crew API - Quick Reference

## Base URL
```
http://localhost:7777/api/v1
```

## Authentication
```
Authorization: Bearer <token>
```

---

## 📋 Endpoints Summary

### 🔐 Authentication (`/public`, `/api/v1/auth`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| POST | `/public/login` | ❌ | - |
| POST | `/public/signup` | ❌ | - |
| PUT | `/api/v1/auth/change-password` | ✅ | Any |
| POST | `/api/v1/auth/refresh-token` | ✅ | Any |

### 👥 Members (`/api/v1/members`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/v1/members` | ✅ | Any |
| GET | `/api/v1/members/{id}` | ✅ | Any |
| POST | `/api/v1/members` | ✅ | Admin |
| PUT | `/api/v1/members/{id}` | ✅ | Admin/Self |
| DELETE | `/api/v1/members/{id}` | ✅ | Admin |
| POST | `/api/v1/members/{id}/avatar` | ✅ | Admin/Self |

### 💰 Contributions (`/api/v1/contributions`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/v1/contributions/monthly?year=2025&month=11` | ✅ | Any |
| GET | `/api/v1/contributions/current` | ✅ | Any |
| PUT | `/api/v1/contributions/{id}/members/{memberId}/mark-paid` | ✅ | Admin |
| PUT | `/api/v1/contributions/{id}/members/{memberId}/mark-unpaid` | ✅ | Admin |
| POST | `/api/v1/contributions/{id}/payments` | ✅ | Admin |
| PUT | `/api/v1/contributions/{id}/target-amount` | ✅ | Admin |
| PUT | `/api/v1/contributions/{id}/lock` | ✅ | Admin |

### 🎉 Events (`/api/v1/events`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/v1/events` | ✅ | Any |
| GET | `/api/v1/events/{id}` | ✅ | Any |
| POST | `/api/v1/events` | ✅ | Admin |
| PUT | `/api/v1/events/{id}` | ✅ | Admin |
| DELETE | `/api/v1/events/{id}` | ✅ | Admin |
| POST | `/api/v1/events/{id}/join` | ✅ | Any |
| POST | `/api/v1/events/{id}/leave` | ✅ | Any |
| GET | `/api/v1/events/upcoming` | ✅ | Any |
| GET | `/api/v1/events/completed` | ✅ | Any |
| PUT | `/api/v1/events/{id}/status` | ✅ | Admin |

### 💸 Expenses (`/api/v1/expenses`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/v1/expenses` | ✅ | Any |
| GET | `/api/v1/expenses/{id}` | ✅ | Any |
| POST | `/api/v1/expenses` | ✅ | Admin |
| PUT | `/api/v1/expenses/{id}` | ✅ | Admin |
| DELETE | `/api/v1/expenses/{id}` | ✅ | Admin |
| GET | `/api/v1/expenses/summary` | ✅ | Any |

### 🔔 Notifications (`/api/v1/notifications`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/v1/notifications` | ✅ | Any |
| GET | `/api/v1/notifications/unread-count` | ✅ | Any |
| PUT | `/api/v1/notifications/{id}/read` | ✅ | Any |
| PUT | `/api/v1/notifications/mark-all-read` | ✅ | Any |
| DELETE | `/api/v1/notifications/{id}` | ✅ | Any |
| DELETE | `/api/v1/notifications` | ✅ | Any |
| POST | `/api/v1/notifications/announcements` | ✅ | Admin |

### 📊 Reports (`/api/v1/reports`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/v1/reports/monthly-summary?month=11&year=2025` | ✅ | Any |
| GET | `/api/v1/reports/financial-overview` | ✅ | Any |
| GET | `/api/v1/reports/export?month=11&year=2025&format=pdf` | ✅ | Admin |

### ⚙️ Settings (`/api/v1/settings`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/v1/settings/profile` | ✅ | Any |
| PUT | `/api/v1/settings/profile` | ✅ | Any |
| POST | `/api/v1/settings/profile/avatar` | ✅ | Any |
| GET | `/api/v1/settings/preferences` | ✅ | Any |
| PUT | `/api/v1/settings/preferences` | ✅ | Any |
| GET | `/api/v1/settings/admin` | ✅ | Admin |
| POST | `/api/v1/settings/admin/export-data` | ✅ | Admin |
| POST | `/api/v1/settings/admin/reset-month` | ✅ | Admin |

### 🏠 Dashboard (`/api/v1/dashboard`)
| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/v1/dashboard` | ✅ | Any |

---

## 📝 Common Request Examples

### Login
```bash
POST /public/login
{
  "username": "mohammed.hajeer@company.com",
  "eskaCoreToken": "dummy",
  "sessionId": "dummy"
}
```

### Get Members (with pagination)
```bash
GET /api/v1/members?page=0&size=25&search=Mohammed&role=admin
Authorization: Bearer <token>
```

### Create Event
```bash
POST /api/v1/events
Authorization: Bearer <token>
{
  "name": "Team Dinner",
  "type": "Food",
  "eventDate": "2025-11-25",
  "eventTime": "19:00:00",
  "location": "Restaurant",
  "estimatedCost": 80.00
}
```

### Mark Member as Paid
```bash
PUT /api/v1/contributions/1/members/1/mark-paid
Authorization: Bearer <token>
{
  "amount": 5.00,
  "paymentDate": "2025-11-15",
  "notes": "Bank transfer"
}
```

---

## 🔑 Role-Based Access

### Admin Can:
- ✅ Create/Update/Delete members
- ✅ Create/Update/Delete events
- ✅ Create/Update/Delete expenses
- ✅ Mark members as paid/unpaid
- ✅ Lock monthly collections
- ✅ Create announcements
- ✅ Export data
- ✅ Reset months

### Member Can:
- ✅ View own profile and update it
- ✅ View all members (read-only)
- ✅ View events and join/leave
- ✅ View expenses (read-only)
- ✅ View contributions (read-only)
- ✅ View notifications and mark as read
- ✅ Update preferences

---

## 📄 Response Format

**Success:**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Operation successful",
  "error": false,
  "data": { ... }
}
```

**Error:**
```json
{
  "statusCode": "MOTIVE-CREW-XXXX",
  "message": "Error description",
  "error": true,
  "data": null
}
```

---

## 🔢 Status Codes

- `200` - OK
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

---

## 📦 Pagination

**Query Params:**
- `page` - Page number (0-indexed)
- `size` - Page size
- `sort` - Sort field and direction

**Example:**
```
GET /api/v1/members?page=0&size=20&sort=name,asc
```

---

For detailed API documentation, see `API_SPECIFICATION.md`

