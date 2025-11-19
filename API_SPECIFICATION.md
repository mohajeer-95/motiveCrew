# Motive Crew - Complete REST API Specification

## Base URL
```
http://localhost:7777/api/v1
```

## Authentication
All endpoints (except `/public/*`) require JWT Bearer token in Authorization header:
```
Authorization: Bearer <token>
```

## Response Format
All responses follow this structure:
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Operation done Successfully",
  "error": false,
  "data": { ... }
}
```

## Error Response Format
```json
{
  "statusCode": "MOTIVE-CREW-XXXX",
  "message": "Error description",
  "error": true,
  "data": null
}
```

---

# 1. Authentication Module (`/api/v1/auth`)

## 1.1 Login
**POST** `/public/login`

**Description:** Authenticate user and receive JWT token

**Request Body:**
```json
{
  "username": "mohammed.hajeer@company.com",
  "eskaCoreToken": "optional-token",
  "sessionId": "optional-session"
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Login successful",
  "error": false,
  "authenticationData": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "name": "Mohammed Hajeer",
      "email": "mohammed.hajeer@company.com",
      "role": "admin",
      "position": "React Native Developer"
    }
  }
}
```

**Error Responses:**
- `400` - Validation error (missing username, etc.)
- `401` - Invalid credentials
- `500` - Internal server error

---

## 1.2 Signup
**POST** `/public/signup`

**Description:** Register a new member (admin only in production, or public for initial setup)

**Request Body:**
```json
{
  "name": "New Member",
  "email": "newmember@company.com",
  "phone": "0790000000",
  "password": "securePassword123",
  "position": "Developer",
  "role": "member"
}
```

**Response (201 Created):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "User registered successfully",
  "error": false,
  "data": {
    "id": 22,
    "name": "New Member",
    "email": "newmember@company.com",
    "role": "member"
  }
}
```

**Error Responses:**
- `400` - Validation error or email already exists
- `403` - Unauthorized (if signup requires admin)
- `500` - Internal server error

---

## 1.3 Change Password
**PUT** `/api/v1/auth/change-password`

**Description:** Change user's password

**Authentication:** Required

**Request Body:**
```json
{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Password changed successfully",
  "error": false,
  "data": null
}
```

**Error Responses:**
- `400` - Validation error or passwords don't match
- `401` - Invalid current password
- `401` - Unauthorized (missing/invalid token)

---

## 1.4 Refresh Token
**POST** `/api/v1/auth/refresh-token`

**Description:** Refresh JWT token

**Authentication:** Required

**Request Body:**
```json
{
  "refreshToken": "refresh_token_here"
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Token refreshed successfully",
  "error": false,
  "data": {
    "token": "new_jwt_token",
    "refreshToken": "new_refresh_token"
  }
}
```

---

# 2. Members Module (`/api/v1/members`)

## 2.1 Get All Members
**GET** `/api/v1/members`

**Description:** Get list of all team members with pagination

**Authentication:** Required

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 25) - Page size
- `search` (optional) - Search by name or email
- `role` (optional) - Filter by role: `admin` or `member`
- `status` (optional) - Filter by payment status: `paid` or `pending`
- `sort` (optional, default: `name,asc`) - Sort field and direction

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Members retrieved successfully",
  "error": false,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Mohammed Hajeer",
        "email": "mohammed.hajeer@company.com",
        "phone": "0790000001",
        "role": "admin",
        "position": "React Native Developer",
        "avatarUrl": null,
        "isActive": true,
        "joinedDate": "2024-03-10",
        "currentMonthStatus": "paid",
        "lastPaymentDate": "2025-11-10",
        "eventsJoined": 8,
        "totalContribution": 40
      }
    ],
    "page": {
      "number": 0,
      "size": 25,
      "totalElements": 21,
      "totalPages": 1
    }
  }
}
```

---

## 2.2 Get Member by ID
**GET** `/api/v1/members/{memberId}`

**Description:** Get detailed information about a specific member

**Authentication:** Required

**Path Variables:**
- `memberId` - Member ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Member retrieved successfully",
  "error": false,
  "data": {
    "id": 1,
    "name": "Mohammed Hajeer",
    "email": "mohammed.hajeer@company.com",
    "phone": "0790000001",
    "role": "admin",
    "position": "React Native Developer",
    "avatarUrl": null,
    "isActive": true,
    "joinedDate": "2024-03-10",
    "paymentHistory": [
      {
        "month": "November 2025",
        "status": "paid",
        "amount": 5.00,
        "paymentDate": "2025-11-10"
      }
    ],
    "eventsParticipated": [
      {
        "eventId": 1,
        "eventName": "Team Lunch",
        "eventDate": "2025-11-14",
        "status": "joined"
      }
    ],
    "totalContribution": 40,
    "eventsJoined": 8
  }
}
```

**Error Responses:**
- `404` - Member not found
- `401` - Unauthorized

---

## 2.3 Create Member
**POST** `/api/v1/members`

**Description:** Add a new team member

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Request Body:**
```json
{
  "name": "New Member",
  "email": "newmember@company.com",
  "phone": "0790000000",
  "position": "Developer(JAVA)",
  "role": "member",
  "password": "defaultPassword123"
}
```

**Response (201 Created):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Member created successfully",
  "error": false,
  "data": {
    "id": 22,
    "name": "New Member",
    "email": "newmember@company.com",
    "role": "member",
    "position": "Developer(JAVA)"
  }
}
```

**Error Responses:**
- `400` - Validation error or email already exists
- `403` - Forbidden (not admin)
- `401` - Unauthorized

---

## 2.4 Update Member
**PUT** `/api/v1/members/{memberId}`

**Description:** Update member information

**Authentication:** Required

**Authorization:** Admin can update any member, members can update only themselves

**Path Variables:**
- `memberId` - Member ID

**Request Body:**
```json
{
  "name": "Updated Name",
  "phone": "0791111111",
  "position": "Senior Developer",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Member updated successfully",
  "error": false,
  "data": {
    "id": 1,
    "name": "Updated Name",
    "email": "mohammed.hajeer@company.com",
    "phone": "0791111111",
    "position": "Senior Developer"
  }
}
```

**Error Responses:**
- `400` - Validation error
- `403` - Forbidden (member trying to update another member)
- `404` - Member not found
- `401` - Unauthorized

---

## 2.5 Delete Member
**DELETE** `/api/v1/members/{memberId}`

**Description:** Delete a member (soft delete - sets isActive to false)

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `memberId` - Member ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Member deleted successfully",
  "error": false,
  "data": null
}
```

**Error Responses:**
- `403` - Forbidden (not admin)
- `404` - Member not found
- `400` - Cannot delete admin or last admin
- `401` - Unauthorized

---

## 2.6 Upload Member Avatar
**POST** `/api/v1/members/{memberId}/avatar`

**Description:** Upload profile picture for a member

**Authentication:** Required

**Authorization:** Admin or member themselves

**Path Variables:**
- `memberId` - Member ID

**Request:** Multipart form data
- `file` - Image file (jpg, png, max 5MB)

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Avatar uploaded successfully",
  "error": false,
  "data": {
    "avatarUrl": "https://storage.example.com/avatars/member_1.jpg"
  }
}
```

---

# 3. Monthly Contributions Module (`/api/v1/contributions`)

## 3.1 Get Monthly Collection
**GET** `/api/v1/contributions/monthly`

**Description:** Get monthly collection data for a specific month

**Authentication:** Required

**Query Parameters:**
- `year` (required) - Year (e.g., 2025)
- `month` (required) - Month (1-12)

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Monthly collection retrieved successfully",
  "error": false,
  "data": {
    "id": 1,
    "year": 2025,
    "month": 11,
    "monthName": "November 2025",
    "targetAmount": 5.00,
    "totalMembers": 21,
    "membersPaid": 18,
    "membersPending": 3,
    "totalCollected": 90.00,
    "goalAmount": 105.00,
    "remainingAmount": 15.00,
    "progressPercentage": 85.71,
    "isLocked": false,
    "lockedAt": null,
    "payments": [
      {
        "id": 1,
        "memberId": 1,
        "memberName": "Mohammed Hajeer",
        "amount": 5.00,
        "status": "paid",
        "paymentDate": "2025-11-10",
        "notes": null
      }
    ]
  }
}
```

---

## 3.2 Get Current Month Collection
**GET** `/api/v1/contributions/current`

**Description:** Get current month's collection data

**Authentication:** Required

**Response (200 OK):** Same structure as 3.1

---

## 3.3 Mark Member as Paid
**PUT** `/api/v1/contributions/{collectionId}/members/{memberId}/mark-paid`

**Description:** Mark a member as paid for a specific month

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `collectionId` - Monthly collection ID
- `memberId` - Member ID

**Request Body:**
```json
{
  "amount": 5.00,
  "paymentDate": "2025-11-15",
  "notes": "Paid via bank transfer"
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Member marked as paid successfully",
  "error": false,
  "data": {
    "id": 1,
    "memberId": 1,
    "memberName": "Mohammed Hajeer",
    "amount": 5.00,
    "status": "paid",
    "paymentDate": "2025-11-15"
  }
}
```

**Error Responses:**
- `400` - Collection is locked or invalid amount
- `403` - Forbidden (not admin)
- `404` - Collection or member not found
- `401` - Unauthorized

---

## 3.4 Mark Member as Unpaid
**PUT** `/api/v1/contributions/{collectionId}/members/{memberId}/mark-unpaid`

**Description:** Mark a member as unpaid (remove payment record)

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `collectionId` - Monthly collection ID
- `memberId` - Member ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Member marked as unpaid successfully",
  "error": false,
  "data": {
    "id": 1,
    "memberId": 1,
    "status": "pending"
  }
}
```

**Error Responses:**
- `400` - Collection is locked
- `403` - Forbidden (not admin)
- `404` - Payment record not found
- `401` - Unauthorized

---

## 3.5 Add Manual Payment
**POST** `/api/v1/contributions/{collectionId}/payments`

**Description:** Add a manual payment record for a member

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `collectionId` - Monthly collection ID

**Request Body:**
```json
{
  "memberId": 1,
  "amount": 5.00,
  "paymentDate": "2025-11-15",
  "notes": "Cash payment received"
}
```

**Response (201 Created):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Payment added successfully",
  "error": false,
  "data": {
    "id": 25,
    "memberId": 1,
    "memberName": "Mohammed Hajeer",
    "amount": 5.00,
    "status": "paid",
    "paymentDate": "2025-11-15",
    "notes": "Cash payment received"
  }
}
```

---

## 3.6 Update Target Amount
**PUT** `/api/v1/contributions/{collectionId}/target-amount`

**Description:** Update the target amount for a monthly collection

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `collectionId` - Monthly collection ID

**Request Body:**
```json
{
  "targetAmount": 7.00
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Target amount updated successfully",
  "error": false,
  "data": {
    "targetAmount": 7.00,
    "goalAmount": 147.00
  }
}
```

**Error Responses:**
- `400` - Collection is locked
- `403` - Forbidden (not admin)
- `401` - Unauthorized

---

## 3.7 Lock Monthly Collection
**PUT** `/api/v1/contributions/{collectionId}/lock`

**Description:** Lock a monthly collection to prevent further edits

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `collectionId` - Monthly collection ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Monthly collection locked successfully",
  "error": false,
  "data": {
    "id": 1,
    "isLocked": true,
    "lockedAt": "2025-11-30T23:59:59"
  }
}
```

---

# 4. Events Module (`/api/v1/events`)

## 4.1 Get All Events
**GET** `/api/v1/events`

**Description:** Get list of all events with filtering and pagination

**Authentication:** Required

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 20) - Page size
- `status` (optional) - Filter by status: `upcoming`, `completed`, `cancelled`
- `type` (optional) - Filter by type: `Food`, `Café`, `Outing`, `Purchase`, `Other`
- `month` (optional) - Filter by month (1-12)
- `year` (optional) - Filter by year
- `search` (optional) - Search by event name
- `sort` (optional, default: `eventDate,desc`) - Sort field and direction

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Events retrieved successfully",
  "error": false,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Team Lunch",
        "type": "Food",
        "description": "Lunch outing to celebrate project success.",
        "eventDate": "2025-11-14",
        "eventTime": "13:00:00",
        "location": "Shams Restaurant",
        "address": "Shams Restaurant – Abdoun",
        "estimatedCost": 60.00,
        "actualCost": null,
        "status": "upcoming",
        "imageUrl": null,
        "totalParticipants": 18,
        "createdBy": {
          "id": 1,
          "name": "Mohammed Hajeer"
        },
        "createdAt": "2025-11-01T10:00:00"
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 5,
      "totalPages": 1
    }
  }
}
```

---

## 4.2 Get Event by ID
**GET** `/api/v1/events/{eventId}`

**Description:** Get detailed information about a specific event

**Authentication:** Required

**Path Variables:**
- `eventId` - Event ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Event retrieved successfully",
  "error": false,
  "data": {
    "id": 1,
    "name": "Team Lunch",
    "type": "Food",
    "description": "Lunch outing to celebrate project success.",
    "eventDate": "2025-11-14",
    "eventTime": "13:00:00",
    "location": "Shams Restaurant",
    "address": "Shams Restaurant – Abdoun",
    "estimatedCost": 60.00,
    "actualCost": null,
    "status": "upcoming",
    "imageUrl": null,
    "totalParticipants": 18,
    "participants": [
      {
        "id": 1,
        "name": "Mohammed Hajeer",
        "avatarUrl": null,
        "joinedAt": "2025-11-01T10:00:00"
      }
    ],
    "expenses": [
      {
        "id": 1,
        "title": "Team Lunch",
        "amount": 60.00,
        "expenseDate": "2025-11-14"
      }
    ],
    "createdBy": {
      "id": 1,
      "name": "Mohammed Hajeer"
    },
    "createdAt": "2025-11-01T10:00:00",
    "updatedAt": "2025-11-01T10:00:00"
  }
}
```

---

## 4.3 Create Event
**POST** `/api/v1/events`

**Description:** Create a new event

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Request Body:**
```json
{
  "name": "Team Dinner",
  "type": "Food",
  "description": "Monthly team dinner gathering",
  "eventDate": "2025-11-25",
  "eventTime": "19:00:00",
  "location": "Fakhr El-Din Restaurant",
  "address": "Fakhr El-Din Restaurant",
  "estimatedCost": 80.00,
  "imageUrl": null
}
```

**Response (201 Created):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Event created successfully",
  "error": false,
  "data": {
    "id": 6,
    "name": "Team Dinner",
    "type": "Food",
    "status": "upcoming",
    "eventDate": "2025-11-25",
    "totalParticipants": 0
  }
}
```

**Error Responses:**
- `400` - Validation error
- `403` - Forbidden (not admin)
- `401` - Unauthorized

---

## 4.4 Update Event
**PUT** `/api/v1/events/{eventId}`

**Description:** Update an existing event

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `eventId` - Event ID

**Request Body:**
```json
{
  "name": "Updated Team Dinner",
  "description": "Updated description",
  "eventDate": "2025-11-26",
  "estimatedCost": 90.00
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Event updated successfully",
  "error": false,
  "data": {
    "id": 6,
    "name": "Updated Team Dinner",
    "eventDate": "2025-11-26"
  }
}
```

**Error Responses:**
- `400` - Validation error
- `403` - Forbidden (not admin)
- `404` - Event not found
- `401` - Unauthorized

---

## 4.5 Delete Event
**DELETE** `/api/v1/events/{eventId}`

**Description:** Delete an event

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `eventId` - Event ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Event deleted successfully",
  "error": false,
  "data": null
}
```

**Error Responses:**
- `403` - Forbidden (not admin)
- `404` - Event not found
- `400` - Cannot delete event with expenses
- `401` - Unauthorized

---

## 4.6 Join Event
**POST** `/api/v1/events/{eventId}/join`

**Description:** Join an event as a participant

**Authentication:** Required

**Path Variables:**
- `eventId` - Event ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Successfully joined event",
  "error": false,
  "data": {
    "eventId": 1,
    "eventName": "Team Lunch",
    "participantId": 1,
    "participantName": "Mohammed Hajeer",
    "joinedAt": "2025-11-10T14:30:00",
    "totalParticipants": 19
  }
}
```

**Error Responses:**
- `400` - Already joined or event is completed/cancelled
- `404` - Event not found
- `401` - Unauthorized

---

## 4.7 Leave Event
**POST** `/api/v1/events/{eventId}/leave`

**Description:** Leave an event (cancel participation)

**Authentication:** Required

**Path Variables:**
- `eventId` - Event ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Successfully left event",
  "error": false,
  "data": {
    "eventId": 1,
    "totalParticipants": 17
  }
}
```

**Error Responses:**
- `400` - Not a participant or event is completed
- `404` - Event not found
- `401` - Unauthorized

---

## 4.8 Get Upcoming Events
**GET** `/api/v1/events/upcoming`

**Description:** Get list of upcoming events

**Authentication:** Required

**Query Parameters:**
- `limit` (optional, default: 10) - Number of events to return

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Upcoming events retrieved successfully",
  "error": false,
  "data": [
    {
      "id": 1,
      "name": "Team Lunch",
      "eventDate": "2025-11-14",
      "eventTime": "13:00:00",
      "location": "Shams Restaurant",
      "totalParticipants": 18
    }
  ]
}
```

---

## 4.9 Get Completed Events
**GET** `/api/v1/events/completed`

**Description:** Get list of completed events

**Authentication:** Required

**Query Parameters:**
- `page` (optional, default: 0)
- `size` (optional, default: 20)
- `month` (optional) - Filter by month
- `year` (optional) - Filter by year

**Response (200 OK):** Same structure as 4.1

---

## 4.10 Update Event Status
**PUT** `/api/v1/events/{eventId}/status`

**Description:** Update event status (e.g., mark as completed)

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `eventId` - Event ID

**Request Body:**
```json
{
  "status": "completed",
  "actualCost": 65.00
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Event status updated successfully",
  "error": false,
  "data": {
    "id": 1,
    "status": "completed",
    "actualCost": 65.00
  }
}
```

---

# 5. Expenses Module (`/api/v1/expenses`)

## 5.1 Get All Expenses
**GET** `/api/v1/expenses`

**Description:** Get list of all expenses with filtering

**Authentication:** Required

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 20) - Page size
- `month` (optional) - Filter by month (1-12)
- `year` (optional) - Filter by year
- `category` (optional) - Filter by category: `Food`, `Café`, `Purchase`, `Other`
- `eventId` (optional) - Filter by linked event
- `paidById` (optional) - Filter by who paid
- `search` (optional) - Search by title
- `sort` (optional, default: `expenseDate,desc`) - Sort field and direction

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Expenses retrieved successfully",
  "error": false,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Team Lunch",
        "amount": 60.00,
        "category": "Food",
        "description": "Lunch at Shams Restaurant for the whole team",
        "expenseDate": "2025-11-14",
        "event": {
          "id": 1,
          "name": "Team Lunch"
        },
        "paidBy": {
          "id": 2,
          "name": "Ashraf Matar"
        },
        "receiptUrl": null,
        "createdBy": {
          "id": 1,
          "name": "Mohammed Hajeer"
        },
        "createdAt": "2025-11-14T13:00:00"
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 7,
      "totalPages": 1
    },
    "summary": {
      "totalSpent": 235.00,
      "totalCollected": 120.00,
      "balance": -115.00,
      "expensesCount": 7
    }
  }
}
```

---

## 5.2 Get Expense by ID
**GET** `/api/v1/expenses/{expenseId}`

**Description:** Get detailed information about a specific expense

**Authentication:** Required

**Path Variables:**
- `expenseId` - Expense ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Expense retrieved successfully",
  "error": false,
  "data": {
    "id": 1,
    "title": "Team Lunch",
    "amount": 60.00,
    "category": "Food",
    "description": "Lunch at Shams Restaurant for the whole team",
    "expenseDate": "2025-11-14",
    "event": {
      "id": 1,
      "name": "Team Lunch",
      "eventDate": "2025-11-14"
    },
    "paidBy": {
      "id": 2,
      "name": "Ashraf Matar",
      "email": "ashraf.matar@company.com"
    },
    "receiptUrl": null,
    "createdBy": {
      "id": 1,
      "name": "Mohammed Hajeer"
    },
    "createdAt": "2025-11-14T13:00:00",
    "updatedAt": "2025-11-14T13:00:00"
  }
}
```

---

## 5.3 Create Expense
**POST** `/api/v1/expenses`

**Description:** Add a new expense

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Request Body:**
```json
{
  "title": "Office Supplies",
  "amount": 25.00,
  "category": "Purchase",
  "description": "Purchased office supplies for the team",
  "expenseDate": "2025-11-10",
  "eventId": null,
  "paidById": 1,
  "receiptUrl": null
}
```

**Response (201 Created):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Expense created successfully",
  "error": false,
  "data": {
    "id": 8,
    "title": "Office Supplies",
    "amount": 25.00,
    "category": "Purchase",
    "expenseDate": "2025-11-10"
  }
}
```

**Error Responses:**
- `400` - Validation error
- `403` - Forbidden (not admin)
- `404` - Event or member not found (if specified)
- `401` - Unauthorized

---

## 5.4 Update Expense
**PUT** `/api/v1/expenses/{expenseId}`

**Description:** Update an existing expense

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `expenseId` - Expense ID

**Request Body:**
```json
{
  "title": "Updated Office Supplies",
  "amount": 30.00,
  "description": "Updated description"
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Expense updated successfully",
  "error": false,
  "data": {
    "id": 8,
    "title": "Updated Office Supplies",
    "amount": 30.00
  }
}
```

**Error Responses:**
- `400` - Validation error
- `403` - Forbidden (not admin)
- `404` - Expense not found
- `401` - Unauthorized

---

## 5.5 Delete Expense
**DELETE** `/api/v1/expenses/{expenseId}`

**Description:** Delete an expense

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Path Variables:**
- `expenseId` - Expense ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Expense deleted successfully",
  "error": false,
  "data": null
}
```

**Error Responses:**
- `403` - Forbidden (not admin)
- `404` - Expense not found
- `401` - Unauthorized

---

## 5.6 Get Expenses Summary
**GET** `/api/v1/expenses/summary`

**Description:** Get expense summary for a specific month

**Authentication:** Required

**Query Parameters:**
- `month` (optional) - Month (1-12), defaults to current month
- `year` (optional) - Year, defaults to current year

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Expense summary retrieved successfully",
  "error": false,
  "data": {
    "month": 11,
    "year": 2025,
    "totalSpent": 235.00,
    "totalCollected": 120.00,
    "balance": -115.00,
    "expensesCount": 7,
    "byCategory": [
      {
        "category": "Food",
        "amount": 140.00,
        "percentage": 59.57
      },
      {
        "category": "Café",
        "amount": 15.00,
        "percentage": 6.38
      }
    ],
    "byEvent": [
      {
        "eventId": 1,
        "eventName": "Team Lunch",
        "amount": 60.00
      }
    ]
  }
}
```

---

# 6. Notifications Module (`/api/v1/notifications`)

## 6.1 Get All Notifications
**GET** `/api/v1/notifications`

**Description:** Get user's notifications

**Authentication:** Required

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 20) - Page size
- `type` (optional) - Filter by type: `event`, `payment`, `expense`, `announcement`, `system`
- `isRead` (optional) - Filter by read status: `true` or `false`
- `sort` (optional, default: `createdAt,desc`) - Sort field and direction

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Notifications retrieved successfully",
  "error": false,
  "data": {
    "content": [
      {
        "id": 1,
        "type": "event",
        "title": "New Event Added: Team Lunch",
        "message": "Lunch scheduled for Thursday at 1:00 PM",
        "relatedId": 1,
        "relatedType": "event",
        "isRead": false,
        "readAt": null,
        "createdAt": "2025-11-12T14:00:00"
      }
    ],
    "page": {
      "number": 0,
      "size": 20,
      "totalElements": 10,
      "totalPages": 1
    },
    "unreadCount": 3
  }
}
```

---

## 6.2 Get Unread Notifications Count
**GET** `/api/v1/notifications/unread-count`

**Description:** Get count of unread notifications

**Authentication:** Required

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Unread count retrieved successfully",
  "error": false,
  "data": {
    "unreadCount": 3
  }
}
```

---

## 6.3 Mark Notification as Read
**PUT** `/api/v1/notifications/{notificationId}/read`

**Description:** Mark a notification as read

**Authentication:** Required

**Path Variables:**
- `notificationId` - Notification ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Notification marked as read",
  "error": false,
  "data": {
    "id": 1,
    "isRead": true,
    "readAt": "2025-11-12T15:30:00"
  }
}
```

---

## 6.4 Mark All Notifications as Read
**PUT** `/api/v1/notifications/mark-all-read`

**Description:** Mark all user's notifications as read

**Authentication:** Required

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "All notifications marked as read",
  "error": false,
  "data": {
    "markedCount": 10
  }
}
```

---

## 6.5 Delete Notification
**DELETE** `/api/v1/notifications/{notificationId}`

**Description:** Delete a notification

**Authentication:** Required

**Path Variables:**
- `notificationId` - Notification ID

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Notification deleted successfully",
  "error": false,
  "data": null
}
```

---

## 6.6 Clear All Notifications
**DELETE** `/api/v1/notifications`

**Description:** Delete all user's notifications

**Authentication:** Required

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "All notifications cleared",
  "error": false,
  "data": {
    "deletedCount": 10
  }
}
```

---

## 6.7 Create Announcement (Admin)
**POST** `/api/v1/notifications/announcements`

**Description:** Create an announcement for all members

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Request Body:**
```json
{
  "title": "Team Lunch Budget Update",
  "message": "Budget increased to 60 JOD for the upcoming event"
}
```

**Response (201 Created):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Announcement created successfully",
  "error": false,
  "data": {
    "id": 1,
    "title": "Team Lunch Budget Update",
    "message": "Budget increased to 60 JOD for the upcoming event",
    "createdAt": "2025-11-12T14:00:00",
    "notificationsSent": 21
  }
}
```

**Error Responses:**
- `400` - Validation error
- `403` - Forbidden (not admin)
- `401` - Unauthorized

---

# 7. Reports/Summary Module (`/api/v1/reports`)

## 7.1 Get Monthly Summary
**GET** `/api/v1/reports/monthly-summary`

**Description:** Get comprehensive monthly financial and activity summary

**Authentication:** Required

**Query Parameters:**
- `month` (optional) - Month (1-12), defaults to current month
- `year` (optional) - Year, defaults to current year

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Monthly summary retrieved successfully",
  "error": false,
  "data": {
    "month": 11,
    "year": 2025,
    "monthName": "November 2025",
    "financial": {
      "totalCollected": 120.00,
      "totalSpent": 80.00,
      "balance": 40.00,
      "membersPaid": 18,
      "totalMembers": 21,
      "progressPercentage": 85.71
    },
    "expensesByCategory": [
      {
        "category": "Food",
        "amount": 60.00,
        "percentage": 75.00
      },
      {
        "category": "Café",
        "amount": 15.00,
        "percentage": 18.75
      }
    ],
    "events": [
      {
        "id": 1,
        "name": "Team Lunch",
        "type": "Food",
        "cost": 60.00,
        "participants": 18
      }
    ],
    "memberContributions": [
      {
        "memberId": 1,
        "memberName": "Mohammed Hajeer",
        "amount": 5.00,
        "status": "paid"
      }
    ],
    "insights": [
      "This month's highest expense was Team Lunch (60 JOD).",
      "85.71% of members contributed — great consistency!",
      "You have 40 JOD left for next month's activities."
    ]
  }
}
```

---

## 7.2 Get Financial Overview
**GET** `/api/v1/reports/financial-overview`

**Description:** Get financial overview for multiple months

**Authentication:** Required

**Query Parameters:**
- `startMonth` (optional) - Start month (1-12)
- `startYear` (optional) - Start year
- `endMonth` (optional) - End month (1-12)
- `endYear` (optional) - End year

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Financial overview retrieved successfully",
  "error": false,
  "data": {
    "period": {
      "start": "2025-10-01",
      "end": "2025-11-30"
    },
    "totalCollected": 240.00,
    "totalSpent": 160.00,
    "balance": 80.00,
    "monthlyBreakdown": [
      {
        "month": 10,
        "year": 2025,
        "collected": 120.00,
        "spent": 80.00,
        "balance": 40.00
      },
      {
        "month": 11,
        "year": 2025,
        "collected": 120.00,
        "spent": 80.00,
        "balance": 40.00
      }
    ]
  }
}
```

---

## 7.3 Export Monthly Report (PDF/Excel)
**GET** `/api/v1/reports/export`

**Description:** Export monthly report as PDF or Excel

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Query Parameters:**
- `month` (required) - Month (1-12)
- `year` (required) - Year
- `format` (required) - Export format: `pdf` or `excel`

**Response (200 OK):**
- Content-Type: `application/pdf` or `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- File download

**Error Responses:**
- `400` - Invalid format or missing parameters
- `403` - Forbidden (not admin)
- `401` - Unauthorized

---

# 8. Settings/Preferences Module (`/api/v1/settings`)

## 8.1 Get User Profile
**GET** `/api/v1/settings/profile`

**Description:** Get current user's profile information

**Authentication:** Required

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Profile retrieved successfully",
  "error": false,
  "data": {
    "id": 1,
    "name": "Mohammed Hajeer",
    "email": "mohammed.hajeer@company.com",
    "phone": "0790000001",
    "role": "admin",
    "position": "React Native Developer",
    "avatarUrl": null,
    "joinedDate": "2024-03-10",
    "isActive": true
  }
}
```

---

## 8.2 Update Profile
**PUT** `/api/v1/settings/profile`

**Description:** Update user's profile information

**Authentication:** Required

**Request Body:**
```json
{
  "name": "Updated Name",
  "phone": "0791111111",
  "position": "Senior React Native Developer"
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Profile updated successfully",
  "error": false,
  "data": {
    "id": 1,
    "name": "Updated Name",
    "phone": "0791111111",
    "position": "Senior React Native Developer"
  }
}
```

**Error Responses:**
- `400` - Validation error
- `401` - Unauthorized

---

## 8.3 Upload Avatar
**POST** `/api/v1/settings/profile/avatar`

**Description:** Upload profile picture

**Authentication:** Required

**Request:** Multipart form data
- `file` - Image file (jpg, png, max 5MB)

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Avatar uploaded successfully",
  "error": false,
  "data": {
    "avatarUrl": "https://storage.example.com/avatars/user_1.jpg"
  }
}
```

---

## 8.4 Get User Preferences
**GET** `/api/v1/settings/preferences`

**Description:** Get user's app preferences

**Authentication:** Required

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Preferences retrieved successfully",
  "error": false,
  "data": {
    "notificationsEnabled": true,
    "darkMode": false,
    "language": "en",
    "autoLogin": true,
    "defaultMonth": "current"
  }
}
```

---

## 8.5 Update User Preferences
**PUT** `/api/v1/settings/preferences`

**Description:** Update user's app preferences

**Authentication:** Required

**Request Body:**
```json
{
  "notificationsEnabled": true,
  "darkMode": true,
  "language": "ar",
  "autoLogin": false,
  "defaultMonth": "previous"
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Preferences updated successfully",
  "error": false,
  "data": {
    "notificationsEnabled": true,
    "darkMode": true,
    "language": "ar",
    "autoLogin": false,
    "defaultMonth": "previous"
  }
}
```

---

## 8.6 Get Admin Settings
**GET** `/api/v1/settings/admin`

**Description:** Get admin-specific settings and options

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Admin settings retrieved successfully",
  "error": false,
  "data": {
    "canExportData": true,
    "canResetMonth": true,
    "canSendAnnouncements": true,
    "totalMembers": 21,
    "activeMembers": 21
  }
}
```

**Error Responses:**
- `403` - Forbidden (not admin)
- `401` - Unauthorized

---

## 8.7 Export All Data (Admin)
**POST** `/api/v1/settings/admin/export-data`

**Description:** Export all system data (admin only)

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Request Body:**
```json
{
  "format": "excel",
  "includeMembers": true,
  "includeEvents": true,
  "includeExpenses": true,
  "includeContributions": true
}
```

**Response (200 OK):**
- File download (Excel or CSV)

**Error Responses:**
- `400` - Invalid format
- `403` - Forbidden (not admin)
- `401` - Unauthorized

---

## 8.8 Reset Current Month (Admin)
**POST** `/api/v1/settings/admin/reset-month`

**Description:** Reset current month's collection and expenses (admin only)

**Authentication:** Required (Admin only)

**Authorization:** Admin role required

**Request Body:**
```json
{
  "month": 11,
  "year": 2025,
  "confirm": true
}
```

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Month reset successfully",
  "error": false,
  "data": {
    "month": 11,
    "year": 2025,
    "resetAt": "2025-11-30T23:59:59"
  }
}
```

**Error Responses:**
- `400` - Invalid month/year or not confirmed
- `403` - Forbidden (not admin)
- `401` - Unauthorized

---

# 9. Dashboard/Home Module (`/api/v1/dashboard`)

## 9.1 Get Dashboard Data
**GET** `/api/v1/dashboard`

**Description:** Get dashboard summary data for home screen

**Authentication:** Required

**Query Parameters:**
- `month` (optional) - Month (1-12), defaults to current month
- `year` (optional) - Year, defaults to current year

**Response (200 OK):**
```json
{
  "statusCode": "MOTIVE-CREW-0000",
  "message": "Dashboard data retrieved successfully",
  "error": false,
  "data": {
    "user": {
      "id": 1,
      "name": "Mohammed Hajeer",
      "role": "admin"
    },
    "summary": {
      "month": "November 2025",
      "totalCollected": 120.00,
      "totalSpent": 80.00,
      "balance": 40.00,
      "eventsCount": 3,
      "membersPaid": 18,
      "totalMembers": 21,
      "progressPercentage": 85.71
    },
    "upcomingEvent": {
      "id": 1,
      "name": "Team Lunch",
      "date": "2025-11-14",
      "time": "13:00:00",
      "location": "Shams Restaurant",
      "participants": 18,
      "type": "Food"
    },
    "recentActivity": [
      {
        "id": 1,
        "text": "Ashraf Matar paid 5 JOD",
        "date": "2025-11-10",
        "type": "payment"
      },
      {
        "id": 2,
        "text": "Added expense: Coffee - 15 JOD",
        "date": "2025-11-08",
        "type": "expense"
      },
      {
        "id": 3,
        "text": "Event created: Lunch Break",
        "date": "2025-11-07",
        "type": "event"
      }
    ],
    "quickActions": {
      "canAddEvent": true,
      "canRecordPayment": true,
      "canAddExpense": true
    }
  }
}
```

---

# HTTP Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `400 Bad Request` - Validation error or invalid request
- `401 Unauthorized` - Missing or invalid authentication token
- `403 Forbidden` - User doesn't have permission (not admin)
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource conflict (e.g., duplicate email)
- `500 Internal Server Error` - Server error

---

# Pagination

All list endpoints support pagination using Spring Data's Pageable:

**Query Parameters:**
- `page` - Page number (0-indexed, default: 0)
- `size` - Page size (default: 20)
- `sort` - Sort field and direction (e.g., `name,asc` or `createdAt,desc`)

**Response Format:**
```json
{
  "content": [...],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

---

# Authentication & Authorization

## Public Endpoints (No Auth Required)
- `POST /public/login`
- `POST /public/signup` (if enabled)

## Member Endpoints (Auth Required)
- All `/api/v1/*` endpoints require valid JWT token
- Members can access their own data and view-only data

## Admin Endpoints (Auth + Admin Role Required)
- Member management (create, update, delete)
- Event management (create, update, delete)
- Expense management (create, update, delete)
- Contribution management (mark paid/unpaid, lock month)
- Announcements
- Data export
- Month reset

---

# Error Codes

| Code | Description |
|------|-------------|
| MOTIVE-CREW-0000 | Success |
| MOTIVE-CREW-0001 | Validation error |
| MOTIVE-CREW-0002 | Resource not found |
| MOTIVE-CREW-0003 | Unauthorized |
| MOTIVE-CREW-0004 | Forbidden (insufficient permissions) |
| MOTIVE-CREW-0005 | Internal server error |
| MOTIVE-CREW-0006 | Duplicate resource (e.g., email already exists) |
| MOTIVE-CREW-0007 | Invalid operation (e.g., collection locked) |

---

# Notes

1. **Versioning**: All APIs are versioned under `/api/v1/`
2. **Date Format**: All dates use ISO 8601 format: `YYYY-MM-DD` or `YYYY-MM-DDTHH:mm:ss`
3. **Currency**: All amounts are in JOD (Jordanian Dinar)
4. **Time Zone**: All timestamps are in UTC
5. **File Uploads**: Maximum file size is 5MB for avatars and receipts
6. **Rate Limiting**: Consider implementing rate limiting for production
7. **Caching**: Consider caching for frequently accessed data (dashboard, summaries)
8. **WebSocket**: Consider WebSocket for real-time notifications

---

# Implementation Notes

## Controller Structure
```
com.eska.motive.crew.ws.controller.v1
├── AuthController          (/api/v1/auth, /public)
├── MemberController        (/api/v1/members)
├── ContributionController  (/api/v1/contributions)
├── EventController         (/api/v1/events)
├── ExpenseController       (/api/v1/expenses)
├── NotificationController  (/api/v1/notifications)
├── ReportController        (/api/v1/reports)
└── SettingsController      (/api/v1/settings)
```

## Service Structure
```
com.eska.motive.crew.ws.service
├── AuthService
├── MemberService
├── ContributionService
├── EventService
├── ExpenseService
├── NotificationService
├── ReportService
└── SettingsService
```

## Repository Structure
```
com.eska.motive.crew.ws.repository
├── UserRepository
├── MonthlyCollectionRepository
├── MemberPaymentRepository
├── EventRepository
├── EventParticipantRepository
├── ExpenseRepository
├── NotificationRepository
└── UserPreferencesRepository
```

---

This specification covers all features of the Motive Crew application and follows REST best practices with proper versioning, authentication, and authorization.

