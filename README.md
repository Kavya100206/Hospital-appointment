# Hospital Management System - Backend API

A comprehensive Spring Boot REST API for hospital management with advanced features including intelligent appointment scheduling, role-based analytics dashboards, and automated notification system.

## Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (PATIENT, DOCTOR, ADMIN)
- Secure password encryption with BCrypt
- Method-level security with `@PreAuthorize`

### Appointment Scheduling Intelligence
- **Doctor Availability Management**: Set working hours, configure time slots
- **Leave Management**: Track doctor holidays and unavailable dates
- **Smart Slot Generation**: Automatically generate available time slots
- **Appointment Rescheduling**: Patients can reschedule (max 2 times)
- **Conflict Detection**: Prevents double-booking and validates availability
- **Composite Unique Constraints**: Allows same doctor names with different specializations

### Role-Based Dashboard Analytics
- **Patient Dashboard**: Appointment statistics, upcoming appointments, favorite doctors
- **Doctor Dashboard**: Today's schedule, weekly stats, total patients served
- **Admin Dashboard**: System-wide analytics, appointment trends, popular specializations
- **Performance Optimized**: Cached responses for faster load times

### Notification System
- **Async Email Notifications**: Non-blocking email delivery
- **Scheduled Reminders**: 
  - 24-hour reminder (runs every hour)
  - 1-hour reminder (runs every 30 minutes)
- **Notification History**: Track all sent notifications
- **Delivery Status**: Monitor sent/failed notifications

### Doctor Management
- Add/view doctors with specializations
- Search by specialization
- Pagination support
- Experience and availability tracking
- Duplicate prevention with name+specialization validation

### Security Features
- Rate limiting (50 requests/minute per IP)
- Input validation on all endpoints
- Global exception handling
- Audit logging ready

---

## Tech Stack

- **Framework**: Spring Boot 3.5.10
- **Language**: Java 17
- **Database**: PostgreSQL / H2 (file-based for persistence)
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Bean Validation
- **Email**: Spring Mail (Mailtrap)
- **Caching**: Spring Cache
- **Rate Limiting**: Bucket4j
- **API Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Maven

---

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (or H2 for local development)
- SMTP server (for email notifications)

---

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd hospital
```

### 2. Configure Database & Email
Update `src/main/resources/application.yaml`:

#### For PostgreSQL:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://YOUR_HOST:5432/YOUR_DB
    username: YOUR_USERNAME
    password: YOUR_PASSWORD
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

#### For H2 (Local Development):
```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/hospitaldb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
      
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
```

#### Email Configuration:
```yaml
spring:
  mail:
    host: smtp.mailtrap.io
    port: 2525
    username: YOUR_MAILTRAP_USERNAME
    password: YOUR_MAILTRAP_PASSWORD
```

#### JWT Configuration:
```yaml
jwt:
  secret: YOUR_SECRET_KEY_MINIMUM_256_BITS
  expiration: 86400000
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8081`

---

## API Documentation

Once the application is running, access the interactive API documentation:

**Swagger UI**: `http://localhost:8081/swagger-ui/index.html`

**H2 Console** (if using H2): `http://localhost:8081/h2-console`

---

## API Endpoints

### Authentication
```http
POST   /api/auth/register          # Register new user
POST   /api/auth/login             # Login and get JWT token
```

### Doctor Management
```http
POST   /api/doctors                # Add doctor (ADMIN only)
GET    /api/doctors                # List all doctors
GET    /api/doctors/paginated      # Paginated list
GET    /api/doctors/specialization # Filter by specialization
```

### Doctor Availability
```http
POST   /api/doctors/{id}/availability       # Set availability (ADMIN/DOCTOR)
GET    /api/doctors/{id}/availability       # Get availability
POST   /api/doctors/{id}/leaves             # Add leave (ADMIN/DOCTOR)
GET    /api/doctors/{id}/leaves             # Get leaves
GET    /api/doctors/{id}/available-slots    # Get available time slots
```

### Appointments
```http
POST   /api/appointments                    # Book appointment (PATIENT)
DELETE /api/appointments/{id}               # Cancel appointment (PATIENT)
PUT    /api/appointments/{id}/reschedule    # Reschedule appointment (PATIENT)
GET    /api/appointments/doctor/{id}        # Get doctor appointments (BOOKED only)
GET    /api/appointments/all                # Get all appointments (ADMIN)
GET    /api/appointments/analytics/status-count  # Count by status
```

### Dashboards
```http
GET    /api/dashboard/patient      # Patient dashboard (PATIENT)
GET    /api/dashboard/doctor       # Doctor dashboard (DOCTOR)
GET    /api/dashboard/admin        # Admin dashboard (ADMIN)
```

### Notifications
```http
GET    /api/notifications          # Get user notifications
GET    /api/notifications/unread-count  # Get unread count
```

---

## Testing the API

### 1. Register Users
```bash
# Register as Patient
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "patient@test.com",
    "password": "password123"
  }'

# Register as Admin
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@test.com",
    "password": "password123",
    "role": "ADMIN"
  }'
```

### 2. Login and Get Token
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@test.com",
    "password": "password123"
  }'
```

### 3. Use Token in Requests
```bash
curl -X GET http://localhost:8081/api/dashboard/patient \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Database Schema

### Core Entities
- **User**: Patients, Doctors, Admins
- **Doctor**: Doctor profiles with specialization (composite unique on name+specialization)
- **Appointment**: Appointment bookings
- **DoctorAvailability**: Working hours and time slots
- **DoctorLeave**: Doctor holidays and leaves
- **Notification**: Email notification tracking

### Relationships
- User (Patient) → Appointment (One-to-Many)
- Doctor → Appointment (One-to-Many)
- Doctor → DoctorAvailability (One-to-Many)
- Doctor → DoctorLeave (One-to-Many)
- User → Notification (One-to-Many)

---

## Scheduled Jobs

### Appointment Reminders
- **24-Hour Reminder**: Runs every hour (`0 0 * * * *`)
- **1-Hour Reminder**: Runs every 30 minutes (`0 */30 * * * *`)

---

## Key Highlights

### Advanced Backend Features
- Complex business logic (scheduling algorithms)
- Data aggregation and analytics
- Scheduled background jobs
- Async processing
- Caching strategy
- Composite unique constraints

### Professional Practices
- Role-based access control
- Input validation
- Global exception handling
- Logging (SLF4J)
- Transaction management
- Performance optimization

### Spring Boot Advanced
- `@Scheduled` for cron jobs
- `@Async` for async processing
- `@Cacheable` for caching
- `@PreAuthorize` for method security
- Spring Data JPA complex queries
- Stream API for data processing

---

## Project Structure

```
src/main/java/com/example/hospital/
├── config/              # Security, JWT, Rate Limiting, Swagger
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
├── exception/           # Exception Handlers
├── model/               # JPA Entities
├── repository/          # Spring Data Repositories
├── service/             # Business Logic
└── util/                # Utility Classes
```

---

## Security

- **JWT Authentication**: Stateless authentication with JWT tokens
- **Password Encryption**: BCrypt hashing
- **Rate Limiting**: 50 requests/minute per IP
- **CORS**: Configured for REST API
- **Input Validation**: Jakarta Bean Validation on all DTOs

---

## Deployment

### Environment Variables (Recommended)
Instead of hardcoding credentials, use environment variables:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    
  mail:
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

### Docker Support (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/hospital-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

---

