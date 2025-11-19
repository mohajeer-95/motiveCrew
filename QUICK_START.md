# Quick Start Guide - Motive Crew Spring Boot

## ✅ What's Been Configured

1. ✅ **MySQL Database** - Updated from Oracle to MySQL
2. ✅ **MySQL Driver** - Added to `build.gradle`
3. ✅ **Java Version** - Set to Java 17 (matches your system)
4. ✅ **Database Config** - Updated `application-dev.properties`
5. ✅ **Root Project** - Created `settings.gradle` and `build.gradle`

## 🚀 How to Run (3 Easy Ways)

### Method 1: Quick Start Script (Easiest)
```bash
cd /Users/eska-wireless/Wikidenia/MotiveCrew
./run.sh
```

### Method 2: Manual Gradle Command
```bash
cd /Users/eska-wireless/Wikidenia/MotiveCrew/motive-crew-ws
./gradlew bootRun
```

### Method 3: Build JAR and Run
```bash
cd /Users/eska-wireless/Wikidenia/MotiveCrew/motive-crew-ws
./gradlew bootJar
java -jar build/libs/motive-crew-ws.jar
```

## 📋 Before Running - Database Setup

### Step 1: Create Database
```bash
mysql -u root -p
```
Then in MySQL:
```sql
CREATE DATABASE wikidenia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### Step 2: Import Schema (Optional)
```bash
cd /Users/eska-wireless/Wikidenia
mysql -u root -p wikidenia < database-schema.sql
```

### Step 3: Update Password (if needed)
Edit: `motive-crew-ws/src/main/resources/application-dev.properties`
```properties
spring.datasource.password=YOUR_PASSWORD
```
(Leave blank if no password: `spring.datasource.password=`)

## 🌐 Access the Application

Once running:
- **URL**: http://localhost:7777
- **Health Check**: http://localhost:7777/actuator/health

## ⚙️ Current Configuration

- **Port**: 7777
- **Database**: wikidenia (MySQL)
- **Profile**: dev (active)
- **JPA**: Auto-update schema (`ddl-auto=update`)

## 🔧 Troubleshooting

### MySQL Connection Error
```bash
# Check if MySQL is running
mysql.server status

# Start MySQL (if not running)
mysql.server start
```

### Port Already in Use
Change port in `application-dev.properties`:
```properties
server.port=8080
```

### Build Errors
```bash
# Clean and rebuild
cd motive-crew-ws
./gradlew clean build --refresh-dependencies
```

## 📁 Project Structure

```
MotiveCrew/
├── run.sh                    # Quick start script
├── README.md                 # Full documentation
├── QUICK_START.md            # This file
├── settings.gradle           # Root project settings
├── build.gradle              # Root build config
├── motive-crew-contract/     # DTOs module
└── motive-crew-ws/          # Main Spring Boot app
    ├── build.gradle
    ├── src/main/
    │   ├── java/...          # Java source code
    │   └── resources/
    │       ├── application.properties
    │       └── application-dev.properties  # ← Database config here
    └── gradlew               # Gradle wrapper
```

## 🎯 Next Steps

1. **Run the application** using one of the methods above
2. **Create JPA entities** based on `../jpa-entities-example.java`
3. **Create repositories** for database access
4. **Implement REST controllers** for your API endpoints
5. **Test with Postman** or your React Native app

## 💡 Tips

- The app uses **Spring Boot DevTools** - changes will auto-reload
- SQL queries are logged (see console output)
- Check logs for any errors or warnings
- Use `application-dev.properties` for development settings

---

**Ready to go!** Just run `./run.sh` from the MotiveCrew directory! 🚀

