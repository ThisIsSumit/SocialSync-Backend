#  SocialSync - Backend API

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?style=for-the-badge&logo=postgresql)

**A powerful RESTful API for comprehensive social media management**

[Features](#features) • [Quick Start](#quick-start) • [API Documentation](#api-documentation) • [Configuration](#configuration) • [Contributing](#contributing)

</div>

---

## 📖 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

**SocialSync Backend** is a robust Spring Boot REST API that powers the SocialSync social media management platform. It enables users to connect multiple social media accounts, schedule posts, track analytics, and manage their social media presence from a single, unified interface.

### Why SocialSync?

- 🔗 **Multi-Platform Support**: Connect Facebook, Twitter, Instagram, and LinkedIn
- 📅 **Smart Scheduling**: Schedule posts across platforms with automated publishing
- 📊 **com.socialsync.socialsyncbackend.entity.Analytics Dashboard**: Track engagement metrics and performance
- 🔐 **Enterprise Security**: JWT authentication with bcrypt password hashing
- 🎯 **RESTful Design**: Clean, well-documented API following REST principles
- 📱 **Mobile Ready**: Optimized for mobile and web clients

---

## ✨ Features

### Core Functionality

- ✅ **User Management**
  - Secure user registration and authentication
  - JWT-based session management
  - Profile management with MFA support
  - Password encryption using bcrypt

- ✅ **Social Media Account Integration**
  - Connect multiple accounts per platform
  - OAuth 2.0 integration
  - Token refresh management
  - Account status monitoring

- ✅ **Post Scheduling**
  - Schedule posts for future publication
  - Content calendar management
  - Automated post publishing via scheduled tasks
  - Multi-platform post support
  - Media attachment handling

- ✅ **com.socialsync.socialsyncbackend.entity.Analytics & Reporting**
  - Real-time engagement metrics
  - Follower growth tracking
  - Post performance analytics
  - Custom date range reports
  - Platform-specific insights

- ✅ **Support System**
  - Ticket creation and management
  - FAQ system
  - User inquiry tracking

### Technical Features

- 🔒 **Security**: Spring Security with JWT, HTTPS enforcement, input validation
- 📚 **API Documentation**: Auto-generated Swagger/OpenAPI documentation
- 🗄️ **Database**: PostgreSQL with JPA/Hibernate ORM
- ⚡ **Performance**: Connection pooling, query optimization, caching strategies
- 🔄 **Scheduling**: Spring's task scheduler for automated post publishing
- 🛡️ **Error Handling**: Global exception handling with meaningful error messages
- ✅ **Validation**: Jakarta Bean Validation for request validation

---

## 🛠️ Tech Stack

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17+ | Programming Language |
| **Spring Boot** | 3.2+ | Application Framework |
| **Spring Security** | 6.2+ | Authentication & Authorization |
| **Spring Data JPA** | 3.2+ | Data Access Layer |
| **PostgreSQL** | 15+ | Primary Database |
| **JWT (jjwt)** | 0.11.5 | Token Management |
| **Lombok** | 1.18+ | Code Generation |
| **SpringDoc OpenAPI** | 2.2.0 | API Documentation |
| **Maven** | 3.8+ | Build Tool |

### Additional Libraries

- **Hibernate Validator** - Bean validation
- **Jackson** - JSON processing
- **HikariCP** - Connection pooling

---

## 🏗️ Architecture

```
src/main/java/com/socialmedia/dashboard/
│
├── controller/          # REST API Controllers
│   ├── AuthController.java
│   ├── AccountController.java
│   ├── PostController.java
│   ├── AnalyticsController.java
│   └── SupportController.java
│
├── service/            # Business Logic Layer
│   ├── AuthService.java
│   ├── UserService.java
│   ├── SocialMediaAccountService.java
│   ├── PostSchedulingService.java
│   ├── AnalyticsService.java
│   └── SupportService.java
│
├── repository/         # Data Access Layer
│   ├── UserRepository.java
│   ├── SocialMediaAccountRepository.java
│   ├── ScheduledPostRepository.java
│   ├── AnalyticsRepository.java
│   └── SupportTicketRepository.java
│
├── entity/            # Database Entities
│   ├── User.java
│   ├── com.socialsync.socialsyncbackend.entity.SocialMediaAccount.java
│   ├── com.socialsync.socialsyncbackend.entity.ScheduledPost.java
│   ├── com.socialsync.socialsyncbackend.entity.Analytics.java
│   └── com.socialsync.socialsyncbackend.entity.SupportTicket.java
│
├── dto/               # Data Transfer Objects
│   ├── request/
│   │   ├── SignUpRequest.java
│   │   ├── LoginRequest.java
│   │   └── SchedulePostRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── SocialAccountResponse.java
│       └── AnalyticsResponse.java
│
├── security/          # Security Configuration
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   ├── SecurityConfig.java
│   └── CustomUserDetailsService.java
│
├── exception/         # Exception Handling
│   └── GlobalExceptionHandler.java
│
└── config/           # Application Configuration
    └── WebConfig.java
```

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17 or higher**
  ```bash
  java -version
  ```

- **Maven 3.8+**
  ```bash
  mvn -version
  ```

- **PostgreSQL 15+**
  ```bash
  psql --version
  ```

- **Git**
  ```bash
  git --version
  ```

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/socialsync-backend.git
cd socialsync-backend
```

### 2. Create PostgreSQL Database

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE socialsync_db;

-- Create user (optional)
CREATE USER socialsync_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE socialsync_db TO socialsync_user;

-- Exit
\q
```

### 3. Configure Application Properties

Create `src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/socialsync_db
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

jwt:
  secret: your-super-secret-jwt-key-at-least-256-bits-long
  expiration: 86400000 # 24 hours
```

### 4. Set Environment Variables

**Linux/Mac:**
```bash
export JWT_SECRET="your-super-secret-jwt-key"
export DB_PASSWORD="your_database_password"
export FACEBOOK_CLIENT_ID="your_facebook_client_id"
export FACEBOOK_CLIENT_SECRET="your_facebook_client_secret"
export TWITTER_CLIENT_ID="your_twitter_client_id"
export TWITTER_CLIENT_SECRET="your_twitter_client_secret"
```

**Windows (PowerShell):**
```powershell
$env:JWT_SECRET="your-super-secret-jwt-key"
$env:DB_PASSWORD="your_database_password"
```

### 5. Install Dependencies

```bash
mvn clean install
```

---

## ⚙️ Configuration

### Application Profiles

The application supports multiple profiles:

- **default** - Production configuration
- **local** - Local development
- **test** - Testing environment
- **dev** - Development server

Activate a profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Key Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Application port | 8080 |
| `jwt.secret` | JWT signing key | Required |
| `jwt.expiration` | Token expiry time (ms) | 86400000 |
| `spring.jpa.hibernate.ddl-auto` | Schema generation | update |

### OAuth2 Configuration

Configure social media OAuth credentials in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          facebook:
            client-id: ${FACEBOOK_CLIENT_ID}
            client-secret: ${FACEBOOK_CLIENT_SECRET}
          twitter:
            client-id: ${TWITTER_CLIENT_ID}
            client-secret: ${TWITTER_CLIENT_SECRET}
          instagram:
            client-id: ${INSTAGRAM_CLIENT_ID}
            client-secret: ${INSTAGRAM_CLIENT_SECRET}
          linkedin:
            client-id: ${LINKEDIN_CLIENT_ID}
            client-secret: ${LINKEDIN_CLIENT_SECRET}
```

---

## 🏃 Running the Application

### Development Mode

```bash
# Using Maven
mvn spring-boot:run

# Using Maven with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Using Java
mvn clean package
java -jar target/socialsync-backend-1.0.0.jar
```

### Production Mode

```bash
# Build production JAR
mvn clean package -DskipTests

# Run with production profile
java -jar target/socialsync-backend-1.0.0.jar --spring.profiles.active=prod
```

### Using Docker

```bash
# Build Docker image
docker build -t socialsync-backend .

# Run container
docker run -p 8080:8080 \
  -e JWT_SECRET=your_secret \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/socialsync_db \
  socialsync-backend
```

### Docker Compose

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down
```

The application will start on `http://localhost:8080`

---

## 📚 API Documentation

### Swagger UI

Once the application is running, access the interactive API documentation:

**URL:** `http://localhost:8080/swagger-ui.html`

### OpenAPI Specification

**URL:** `http://localhost:8080/api-docs`

### Postman Collection

Import the Postman collection for easy API testing:

```bash
# Located in /docs/postman/
SocialSync-API.postman_collection.json
```

---

## 🗃️ Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐
│     Users       │
├─────────────────┤
│ id (PK)         │
│ email           │
│ password        │
│ first_name      │
│ last_name       │
│ created_at      │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼────────────────┐
│ SocialMediaAccounts     │
├─────────────────────────┤
│ id (PK)                 │
│ user_id (FK)            │
│ platform                │
│ account_name            │
│ access_token            │
│ connected_at            │
└────────┬────────────────┘
         │
         │ 1:N
         │
┌────────▼──────────┐         ┌─────────────────┐
│ ScheduledPosts    │         │   com.socialsync.socialsyncbackend.entity.Analytics     │
├───────────────────┤         ├─────────────────┤
│ id (PK)           │         │ id (PK)         │
│ user_id (FK)      │◄───────►│ account_id (FK) │
│ account_id (FK)   │         │ followers       │
│ content           │         │ likes           │
│ scheduled_time    │         │ comments        │
│ status            │         │ recorded_at     │
└───────────────────┘         └─────────────────┘
```

### Key Tables

#### users
- Stores user account information
- Password hashed with bcrypt
- Supports MFA

#### social_media_accounts
- Connected social media platforms
- OAuth tokens (encrypted)
- Platform-specific settings

#### scheduled_posts
- Queued posts for publication
- Content and media URLs
- Scheduling metadata

#### analytics
- Engagement metrics
- Time-series data
- Platform-specific KPIs

---

## 🔐 Authentication

### JWT Authentication Flow

```
1. User registers/logs in
   POST /api/auth/signup or /api/auth/login
   
2. Server validates credentials
   - Checks email/password
   - Generates JWT token
   
3. Server returns JWT token
   {
     "token": "eyJhbGciOiJIUzI1NiIs...",
     "type": "Bearer",
     "userId": 123
   }
   
4. Client includes token in requests
   Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
   
5. Server validates token
   - Verifies signature
   - Checks expiration
   - Extracts user information
```

### Protected Endpoints

All endpoints except the following require authentication:

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `GET /api/support/faqs`
- Swagger documentation endpoints

---

## 🛣️ API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/signup` | Register new user | ❌ |
| POST | `/api/auth/login` | Login user | ❌ |

### User Profile

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/profile` | Get user profile | ✅ |
| PUT | `/api/profile` | Update profile | ✅ |

### Social Media Accounts

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/accounts` | Connect account | ✅ |
| GET | `/api/accounts` | List accounts | ✅ |
| DELETE | `/api/accounts/{id}` | Disconnect account | ✅ |

### Post Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/posts` | Schedule post | ✅ |
| GET | `/api/posts` | List scheduled posts | ✅ |
| DELETE | `/api/posts/{id}` | Delete scheduled post | ✅ |

### com.socialsync.socialsyncbackend.entity.Analytics

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/analytics` | Get analytics | ✅ |
| GET | `/api/analytics/reports` | Generate report | ✅ |

### Support

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/support` | Create ticket | ✅ |
| GET | `/api/support/faqs` | Get FAQs | ❌ |

### Example Requests

#### Register User

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

#### Schedule Post

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "accountId": 1,
    "content": "Check out our new features!",
    "scheduledTime": "2024-12-25T10:00:00"
  }'
```

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=AuthControllerTest
```

### Run with Coverage

```bash
mvn clean test jacoco:report
```

View coverage report at: `target/site/jacoco/index.html`

### Integration Tests

```bash
mvn verify -P integration-tests
```

### Test Structure

```
src/test/java/
├── controller/     # Controller tests
├── service/        # Service tests
├── repository/     # Repository tests
└── integration/    # Integration tests
```

---

## 🚢 Deployment

### Heroku

```bash
# Login to Heroku
heroku login

# Create app
heroku create socialsync-api

# Add PostgreSQL
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set JWT_SECRET=your_secret

# Deploy
git push heroku main
```

### AWS Elastic Beanstalk

```bash
# Initialize EB
eb init -p java-17 socialsync-backend

# Create environment
eb create socialsync-env

# Deploy
eb deploy
```

### Docker Production

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and push:
```bash
docker build -t yourusername/socialsync-backend:latest .
docker push yourusername/socialsync-backend:latest
```

---

## 🔧 Troubleshooting

### Common Issues

#### Database Connection Failed

```
Error: org.postgresql.util.PSQLException: Connection refused
```

**Solution:**
- Check PostgreSQL is running: `sudo service postgresql status`
- Verify database credentials in `application.yml`
- Ensure database exists: `psql -l`

#### Port Already in Use

```
Error: Port 8080 is already in use
```

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.yml
server.port=8081
```

#### JWT Token Invalid

```
Error: 401 Unauthorized - Invalid JWT token
```

**Solution:**
- Ensure token is included: `Authorization: Bearer <token>`
- Check token hasn't expired
- Verify JWT_SECRET matches between requests

#### Maven Build Fails

```
Error: Could not resolve dependencies
```

**Solution:**
```bash
# Clear Maven cache
mvn dependency:purge-local-repository

# Rebuild
mvn clean install -U
```

### Logging

Enable debug logging:

```yaml
logging:
  level:
    com.socialmedia.dashboard: DEBUG
    org.springframework.security: DEBUG
```

---

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### Getting Started

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Code Style

- Follow Java naming conventions
- Use Lombok annotations where appropriate
- Write unit tests for new features
- Update documentation

### Commit Messages

Follow conventional commits:
```
feat: Add post scheduling feature
fix: Resolve JWT expiration issue
docs: Update API documentation
test: Add analytics service tests
```

### Pull Request Process

1. Update README if needed
2. Add/update tests
3. Ensure CI passes
4. Request review from maintainers



</div>
