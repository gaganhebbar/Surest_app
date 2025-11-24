## 📘 Overview
This project is a **Spring Boot–based web services** that provides secure REST APIs for handling member data.  
It includes robust authentication, role-based authorization, caching, pagination, and containerized deployment using Docker and PostgreSQL.  
The project follows clean architecture, scalable design, and enterprise-level coding standards.

---

## 🧰 Tech Stack

### Backend Technologies
- Java 17+
- Spring Boot 3+
- Spring Web
- Spring Data JPA
- Spring Security + JWT
- Hibernate ORM
- PostgreSQL

### Build & Tooling
- Gradle 
- Docker & Docker Compose
- Flyway (or SQL migration scripts)
- JUnit 5
- Mockito

---

## ⭐ Features

### Authentication
- POST /auth/login
- Validates username/password
- Generates JWT token
- Secures all member APIs
- Stateless authentication

### Member Management
- Full CRUD operations
- Pagination & sorting
- Search/filter options
- Caching for GET /members/{id}
- Cache eviction on update/delete

### Role-Based Access Control

Role | Permissions
---- | -----------
ADMIN | Full CRUD on members
USER | Read-only access

---

## 🗄 Database Configuration
8This application uses PostgreSQL 16. Configuration via application.properties or Docker Compose environment variables.

---

## 🧪 Testing

### Run all tests
```
./gradlew test
```

### Generate JaCoCo Coverage Report
```
./gradlew jacocoTestReport
```

Report location:
```
build/reports/jacoco/test/html
```

---

## ▶ Running the Application (Local)
```
./gradlew bootRun
```

---

# 🐳 Running with Docker Compose

### Start services
```
docker-compose up --build
```

### Stop containers
```
docker-compose down
```

### Rebuild only app
```
docker-compose build app
```

---

## 📜 Logs

### Spring Boot
```
docker logs spring-app
```

### PostgreSQL
```
docker logs postgres-db
```

---

## 🗄 Connect to PostgreSQL
```
docker exec -it postgres-db psql -U springuser -d DevAssignment
```

---

## 📁 Project Structure
```
src/
 ├── main/java/.../controller
 ├── main/java/.../service
 ├── main/java/.../repository
 ├── main/java/.../config
 ├── main/java/.../security
 ├── main/resources/
 └── test/java/...
```

---

## 🔐 API Authentication Flow
1. Login via /auth/login  
2. Validate credentials  
3. Generate JWT  
4. Client uses Authorization: Bearer <token>  
5. Backend validates token & roles  

---

