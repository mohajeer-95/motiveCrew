# Create Tables in DBeaver - Step by Step Guide

## Quick Steps

1. **Open DBeaver**
2. **Connect to MySQL database:**
   - Host: `192.168.0.51`
   - Port: `3306`
   - Database: `motive_crew`
   - Username: `motive_crew`
   - Password: `motive_crew`

3. **Open SQL Editor** (Right-click on database → SQL Editor → New SQL Script)

4. **Copy and paste the SQL from `V1__create_tables.sql`**

5. **Execute** (Press F5 or click Execute button)

6. **Verify tables created** (Check in Database Navigator)

## SQL Files Location

The SQL scripts are located in:
```
MotiveCrew/motive-crew-ws/src/main/resources/db/migration/
```

Files:
- **V1__create_tables.sql** - Creates all tables
- **V2__insert_initial_data.sql** - Inserts initial team members (optional)

## What Gets Created

### Tables (9 total):
1. `users` - Team members
2. `monthly_collections` - Monthly collection periods
3. `member_payments` - Payment records
4. `events` - Team events/activities
5. `event_participants` - Event participants (many-to-many)
6. `expenses` - Expense records
7. `notifications` - System notifications
8. `user_preferences` - User app preferences
9. `announcements` - Admin announcements

### Features:
- ✅ All foreign keys and relationships
- ✅ Indexes for performance
- ✅ Unique constraints
- ✅ Default values
- ✅ Proper data types (MySQL compatible)

## Important Notes

1. **Password Hash:** The initial data script includes a BCrypt hash for "password123"
   - You can change passwords later via the API
   - Or update the hash in the SQL script

2. **Auto-increment IDs:** All tables use `AUTO_INCREMENT` for primary keys

3. **Cascade Deletes:** 
   - User deletion cascades to related records
   - Collection deletion cascades to payments

4. **Character Set:** Uses `utf8mb4` for full Unicode support

## After Creating Tables

1. **Restart the application** (if running)
2. **Hibernate will detect existing tables** (with `ddl-auto=update`)
3. **Test the API endpoints**

## Verification Query

Run this in DBeaver to verify all tables:

```sql
SHOW TABLES;
```

Should show 9 tables:
- announcements
- event_participants
- events
- expenses
- member_payments
- monthly_collections
- notifications
- user_preferences
- users

## Check Table Structure

```sql
DESCRIBE users;
DESCRIBE monthly_collections;
DESCRIBE member_payments;
-- etc.
```

