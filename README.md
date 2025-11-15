# FixMed — Medical Platform Backend

> A production-grade Spring Boot REST API for a comprehensive medical platform connecting patients, doctors, and healthcare facilities with appointment scheduling, real-time messaging, and secure file management.

## 📑 Quick Links

- [Features](#-features)
- [Tech Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [API Overview](#-api-overview)
- [Configuration](#-configuration)
- [Testing](#-testing)

---

## ✨ Features

### 🔐 Authentication & Security
- JWT-based token authentication with role-based access control (RBAC)
- Support for multiple user roles: `PATIENT`, `DOCTOR`, `ADMIN`, `FACILITY`
- Token lifecycle management (issuance, refresh, validation, revocation)
- BCrypt password hashing for enhanced security
- Stateless authentication architecture

### 👥 User Management
- Role-specific user profiles with comprehensive data models
- Doctor profiles with specialization and verification details
- Patient medical history tracking
- Healthcare facility management
- Profile updates and self-service management via `/users/me` endpoint

### 📅 Appointment Management
- Intelligent slot availability verification
- Full appointment lifecycle (create, retrieve, update, cancel, complete)
- Attachment support for medical documents
- Advanced filtering and search capabilities
- Asynchronous event-driven processing for side-effects (notifications, audit)

### 💬 Real-Time Messaging
- Direct doctor-patient communication
- Conversation history management
- Patient-doctor relationship tracking
- Built for scalability with RabbitMQ integration

### 🏥 Healthcare Services
- Medical service catalog per facility
- Service details: pricing, duration, specialization
- Service search and filtering

### 📁 File Management
- Doctor profile photo uploads and management
- Medical document attachment storage
- File validation (size and type constraints)
- MinIO S3-compatible integration for reliable, scalable storage

### ⭐ Review System
- Doctor and facility rating system
- Review history tracking
- Aggregate rating calculations

---

## 🛠 Technology Stack

| Layer | Technology |
|-------|-----------|
| **Framework** | Spring Boot 3.4.4, Spring Framework 6.x |
| **Language** | Java 17 (LTS) |
| **Build Tool** | Maven 3.9+ |
| **Security** | Spring Security, JWT (JJWT 0.12.6), BCrypt |
| **Database** | MySQL 8.0+, Spring Data JPA |
| **Migration** | Liquibase (schema versioning) |
| **File Storage** | MinIO 8.5.17 (S3-compatible) |
| **Messaging** | RabbitMQ, Spring AMQP |
| **Testing** | JUnit 5, Spring Security Test, MockMVC |
| **Code Quality** | Lombok, Spring Boot DevTools |
| **Validation** | Jakarta Bean Validation, Spring Validation |

---

## 🏗 Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│   REST Controllers (Presentation)   │
│   ├── AuthenticationController      │
│   ├── UsersController               │
│   └── Domain-specific Controllers   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Service Layer (Business Logic)    │
│   ├── AuthenticationService         │
│   ├── AppointmentsService           │
��   └── Domain Services               │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   Persistence Layer (Data Access)   │
│   ├── Spring Data Repositories      │
│   └── JPA Entity Models             │
└──────────────┬──────────────────────┘
               │
        ┌──────▼──────┐
        │   MySQL DB  │
        └─────────────┘
```

### Key Design Patterns

- **Event-Driven Architecture**: Domain events (e.g., `AppointmentRegisteredEvent`) published via `ApplicationEventPublisher` for decoupled, asynchronous processing
- **Service-Repository Pattern**: Clean separation between business logic and data access
- **DTO Pattern**: Request/Response models in `model.dto` package for secure API contracts
- **Stateless Authentication**: JWT tokens eliminate server-side session storage
- **Async Messaging**: RabbitMQ integration for inter-service communication and notifications

---

## 📁 Project Structure

```
src/main/java/org/fixmed/fixmed/
├── FixmedApplication.java                 # Spring Boot entry point
├── config/
│   ├── ApplicationConfig.java             # Bean definitions (security, auth, validation)
│   ├── SecurityConfig.java                # Spring Security configuration
│   └── RabbitMQConfig.java                # Message broker setup
├── auth/
│   ├── AuthenticationController.java      # Auth endpoints (register, login, refresh)
│   ├── AuthenticationService.java         # JWT lifecycle & token validation
│   └── JwtTokenProvider.java              # Token generation & parsing
├── controller/
│   ├── UsersController.java               # User management endpoints
│   ├── AppointmentsController.java        # Appointment CRUD & operations
│   ├── MessagingController.java           # Doctor-patient messaging
│   └── FacilitiesController.java          # Facility management
├── service/
│   ├── AppointmentsService.java           # Interface defining appointment operations
│   ├── MessagingService.java              # Messaging logic
│   └── FileStorageService.java            # MinIO integration
├── service/impl/
│   ├── AppointmentsServiceImpl.java        # Slot verification, event publishing
│   ├── MessagingServiceImpl.java           # Message persistence & retrieval
│   └── FileStorageServiceImpl.java         # S3 upload/download
├── repository/
│   ├── AppointmentsRepository.java        # JPA queries (Spring Data)
│   ├── UsersRepository.java               # User data access
│   └── MessagesRepository.java            # Message persistence
├── model/
│   ├── User.java                          # JPA entity with role support
│   ├── Appointment.java                   # Appointment entity
│   ├── Message.java                       # Message entity
│   └── dto/
│       ├── UserDTO.java                   # Transfer object for user data
│       ├── AppointmentDTO.java            # Transfer object for appointments
│       └── AuthResponse.java              # JWT response payload
├── event/
│   ├── AppointmentRegisteredEvent.java    # Domain event for appointments
│   └── AppointmentEventListener.java      # Async listener for side-effects
└── exception/
    ├── GlobalExceptionHandler.java        # Centralized error handling
    └── CustomExceptions.java              # Domain-specific exceptions

src/main/resources/
├── application.properties                 # Base configuration
├── application-dev.properties             # Development overrides
├── application-prod.properties            # Production overrides
├── application-example.properties         # Template for secrets/credentials
└── db/changelog/                          # Liquibase migration files

src/test/java/
├── controller/                            # Controller unit & integration tests
├── service/                               # Service layer tests
└── repository/                            # Repository & JPA tests
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (LTS)
- **Maven 3.9+**
- **MySQL 8.0+**
- **Docker & Docker Compose** (optional, for RabbitMQ and MinIO)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/JBRKR000/FixmedFullstackApp.git
   cd FixmedFullstackApp
   ```

2. **Configure environment**
   ```bash
   cp src/main/resources/application-example.properties src/main/resources/application.properties
   # Edit application.properties with your database, JWT secret, MinIO, and RabbitMQ credentials
   ```

3. **Start dependencies (Docker)**
   ```bash
   docker-compose up -d
   ```

4. **Build and run**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

   The API will be available at `http://localhost:8080`

---

## 📡 API Overview

### Authentication Endpoints

```
POST   /api/auth/register          → Create new user account
POST   /api/auth/authenticate      → Login and receive JWT token
POST   /api/auth/refresh           → Refresh expired token
POST   /api/auth/authenticatetoken → Validate current token
POST   /api/auth/logout            → Revoke token
```

### User Management

```
GET    /api/users                  → List all users (paginated)
GET    /api/users/{id}             → Get user profile by ID
POST   /api/users                  → Create new user (admin only)
GET    /api/users/me               → Current authenticated user profile
PUT    /api/users/me               → Update current user profile
```

### Appointments

```
POST   /api/appointments           → Create appointment
GET    /api/appointments/{id}      → Get appointment details
GET    /api/appointments           → List appointments (filtered, paginated)
PUT    /api/appointments/{id}      → Update appointment
DELETE /api/appointments/{id}      → Cancel appointment
POST   /api/appointments/{id}/complete → Mark as completed
```

### Messaging

```
POST   /api/messages               → Send message
GET    /api/messages/{conversationId} → Get conversation history
GET    /api/messages/doctors/{doctorId} → List doctors' conversations
GET    /api/messages/patients/{patientId} → List patient's doctors
```

### Additional Endpoints

- **Facilities**: `/api/facilities` - Manage healthcare facilities
- **Services**: `/api/services` - Medical service catalog
- **Slots**: `/api/availability-slots` - Appointment availability
- **Reviews**: `/api/reviews` - Rate doctors and facilities

**Full API documentation available via Swagger/OpenAPI at `/swagger-ui.html` (if enabled)**

---

## ⚙️ Configuration

### Environment Variables

Create `src/main/resources/application.properties` with:

```properties
# Spring & Server
spring.application.name=fixmed-backend
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/fixmed_db
spring.datasource.username=root
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=validate

# JWT Security
app.security.jwt.secret=your_jwt_secret_key_min_256_bits
app.security.jwt.expiration=86400000
app.security.jwt.refresh-expiration=604800000

# MinIO File Storage
minio.url=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=fixmed-files
doctor.photos.base-url=http://localhost:9000/fixmed-files

# RabbitMQ Messaging
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
app.rabbitmq.exchange=fixmed-exchange
app.rabbitmq.queue=fixmed-queue

# Logging
logging.level.org.fixmed.fixmed=DEBUG
logging.level.org.springframework.security=DEBUG
```

---

## 🧪 Testing

### Running Tests

```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=AppointmentsServiceTest

# With coverage
./mvnw test jacoco:report
```

### Test Coverage

- **Unit Tests**: Service layer logic, validation, calculations
- **Integration Tests**: Repository queries, database transactions
- **Controller Tests**: REST endpoints, request validation, response formats
- **Security Tests**: Authentication, authorization, token validation

**Recommended**: Use **Testcontainers** for realistic MinIO and RabbitMQ testing

---

## 🔒 Security Highlights

✅ **Stateless JWT Authentication** - No session storage overhead
✅ **Role-Based Access Control** - Fine-grained authorization per role
✅ **Password Hashing** - BCrypt with configurable strength
✅ **Token Revocation** - Logout functionality with token blacklisting
✅ **Input Validation** - Jakarta Bean Validation on all DTOs
✅ **Exception Handling** - Centralized, secure error responses
✅ **CORS Configuration** - Configurable cross-origin policies

---

## 📊 Performance & Scalability

- **Stateless Design**: Horizontally scalable (add more instances behind load balancer)
- **Async Events**: Non-blocking appointment creation and notifications
- **RabbitMQ Integration**: Decoupled inter-service communication
- **MinIO S3 Storage**: Scalable file storage with multi-instance support
- **Database Indexing**: Optimized queries with strategic index placement
- **Pagination**: All list endpoints support paginated responses

---

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is proprietary and confidential.

---

## 👨‍💻 Author

**JBRKR000**
Full-stack developer specializing in Spring Boot backend architecture and Java microservices.

---

## 🙋 Support

For issues, questions, or suggestions, please open a GitHub issue or contact the development team.
