## 🚀 About the Project & Permissions


# Zorvyn Finance Backend

---
This backend is built with **Spring Boot (Java)** using **MySQL** for data persistence and **JWT-based authentication** with **role-based access control (RBAC)**. There are three main roles in the system:

1. **VIEWER**: Can only read financial records and dashboard summaries.
2. **ANALYST**: Can create, read, update, and delete financial records that belong to their own scope.
3. **ADMIN**: Has full access to all features, including CRUD operations on all records and users.

Key design choices and features:

4. **Registration**: Users sign up via `POST /api/auth/register`.
5. **Login**: Users receive a JWT token via `POST /api/auth/login`.
6. **Token usage**: All secured endpoints require `Authorization: Bearer <token>`.
7. **Unauthorized access**: Invalid/expired tokens return HTTP 401 (Unauthorized).
8. **Forbidden access**: Insufficient role permissions return HTTP 403 (Forbidden).

Financial records module:

9. **CRUD operations**: Full Create, Read, Update, Delete on records via `/api/records`.
10. **Filtering**: Records can be filtered by date range, category, and transaction type.
11. **Dashboard APIs**: Provide income, expenses, totals, and trends in JSON format.
12. **Input validation**: Spring Boot validation annotations ensure data quality at the API level.
13. **Business validation**: Additional checks ensure only authorized users can modify or delete records.

Swagger UI and documentation:

14. API documentation is available at `http://localhost:8080/swagger-ui/index.html`.
15. All endpoints, request/response models, and example payloads are visible and testable there.
16. Local testing is easy for both frontend developers and backend integrations.

Deployment and structure:

17. Backend is designed to run locally using MySQL and embedded Tomcat.
18. No Docker is used in this commit, but the project can be containerized later.
19. Environment variables (database URL, JWT secret) can be configured for deployment on platforms like Render, Railway, or Fly.io.
20. The project is ready for production deployment once the chosen cloud provider is configured.

---

### 📝 Note





## 🏁 How to Run Locally

### Prerequisites
- Java 17 (or 11+)
- Maven
- MySQL installed locally

### Step 1: Clone the repo
```bash
git clone https://github.com/Parshant2003/ZorvynFinance-Backend.git
cd ZorvynFinance-Backend
```

### Step 2: Create database
Open MySQL and create a database:
```sql
CREATE DATABASE zorvyn_finance;
```

### Step 3: Configure `application.properties`
Edit `src/main/resources/application.properties` with your MySQL details:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/zorvyn_finance
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### Step 4: Build and run
```bash
./mvnw clean package
./mvnw spring-boot:run
```

Or jar se:
```bash
java -jar target/ZorvynFinance-Backend-0.0.1-SNAPSHOT.jar
```

Server will start at `http://localhost:8080`.

---

## 📖 API Documentation (Swagger UI)

After running the app, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Here you can:
- View all REST endpoints
- Test APIs directly
- See request/response models

---

## 🚀 Quick Brief about this project

- **Backend framework**: Spring Boot (Java)  
- **Auth**: JWT with role‑based access control (VIEWER, ANALYST, ADMIN)  
- **Database**: MySQL only  
- **API docs**: Swagger UI (OpenAPI)  
- **Deployment ready**: Designed for platforms like Render, Railway, Fly.io, etc. (Dockerfile and configs can be added later).

---

## 📝 Note

This project is currently deployed **locally for demo purposes on MySQL**.  
All deployment instructions and environment variables are documented in this GitHub repository.
