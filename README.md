# Motive Crew - Spring Boot Backend

Spring Boot backend application for the Wikidenia (Motive Crew) mobile app.

## Prerequisites

- **Java 17** (JDK 17 or higher)
- **MySQL 8.0+** (or MariaDB 10.3+)
- **Gradle 7.0+** (or use Gradle Wrapper)

## Setup Instructions

### 1. Database Setup

#### Option A: Use MySQL from Command Line
```bash
# Create database
mysql -u root -p
CREATE DATABASE wikidenia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;

# Or run the schema script
mysql -u root -p < ../database-schema.sql
```

#### Option B: Use MySQL Workbench or phpMyAdmin
- Create a new database named `wikidenia`
- Import the SQL file: `../database-schema.sql`

### 2. Update Database Configuration

Edit `motive-crew-ws/src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wikidenia?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

**Note:** If your MySQL password is empty, leave it blank: `spring.datasource.password=`

### 3. Build the Project

From the root directory (`/Users/eska-wireless/Wikidenia/MotiveCrew`):

```bash
# Using Gradle Wrapper (recommended)
./motive-crew-ws/gradlew build -p motive-crew-ws

# Or if you have Gradle installed globally
gradle build
```

### 4. Run the Application

#### Option A: Using Gradle
```bash
cd motive-crew-ws
./gradlew bootRun
```

#### Option B: Using Java
```bash
cd motive-crew-ws
./gradlew bootJar
java -jar build/libs/motive-crew-ws.jar
```

#### Option C: Using IDE (IntelliJ IDEA / Eclipse)
1. Import the project as a Gradle project
2. Find `MotiveCrewWsApplication.java`
3. Right-click → Run

### 5. Verify the Application

The application will run on: **http://localhost:7777**

Check health endpoint:
```bash
curl http://localhost:7777/actuator/health
```

## Project Structure

```
MotiveCrew/
├── motive-crew-contract/     # DTOs and contracts module
│   └── src/main/java/com/eska/motive/crew/contract/
│       ├── dto/
│       ├── request/
│       └── response/
│
└── motive-crew-ws/           # Main Spring Boot application
    └── src/main/java/com/eska/motive/crew/ws/
        ├── config/           # Configuration classes
        ├── controller/       # REST controllers
        ├── service/          # Business logic
        ├── exception/        # Exception handlers
        └── validation/       # Validators
```

## Database Schema

The database schema is defined in:
- `../database-schema.sql` - Complete SQL schema
- `../database-schema.md` - Detailed documentation
- `../jpa-entities-example.java` - JPA entity examples

## Configuration Files

- `application.properties` - Base configuration
- `application-dev.properties` - Development profile (active by default)
- `application-qa.properties` - QA profile

## Troubleshooting

### Issue: "MySQL connection refused"
- Check if MySQL is running: `mysql.server start` (macOS)
- Verify MySQL port (default: 3306)
- Check firewall settings

### Issue: "Java version mismatch"
- The project requires Java 17
- Check version: `java -version`
- Install Java 17 if needed

### Issue: "Gradle build fails"
- Try: `./gradlew clean build --refresh-dependencies`
- Check internet connection (for downloading dependencies)
- Some dependencies may require internal Maven repository access

### Issue: "Port 7777 already in use"
- Change port in `application-dev.properties`: `server.port=8080`
- Or kill the process using port 7777

## Next Steps

1. **Create JPA Entities** - Use `../jpa-entities-example.java` as reference
2. **Create Repositories** - Spring Data JPA repositories
3. **Create Services** - Business logic layer
4. **Create Controllers** - REST API endpoints
5. **Add Authentication** - JWT-based authentication (already configured)

## API Endpoints

Once running, the API will be available at:
- Base URL: `http://localhost:7777`
- Health Check: `http://localhost:7777/actuator/health`

## Development Tips

- Enable dev tools for hot reload (already included)
- Use `spring.jpa.show-sql=true` to see SQL queries
- Check logs in console for debugging

## Support

For database schema questions, refer to `../database-schema.md`

