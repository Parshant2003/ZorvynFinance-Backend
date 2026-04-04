# Zorvyn Finance Backend

Spring Boot (Java) backend for the Zorvyn Finance assignment.  
Supports **user roles, financial records CRUD, filtering, dashboard APIs, RBAC, and JWT authentication** on **MySQL only**.

---

## 🚀 About the Project & Permissions

This backend is built with **Spring Boot (Java)** using **MySQL** for data persistence and **JWT-based authentication** with **role-based access control (RBAC)**. Roles are clearly separated for real-world finance use cases:

1. **VIEWER role** – Can only **read financial records** and **dashboard summaries**, ideal for managers and auditors who need visibility without modification rights.

2. **ANALYST role** – Can **create, read and update** financial records that belong to their own scope, enabling detailed analysis and iterative data refinement.

3. **ADMIN role** – Has **full access** to all features, including managing users, assigning roles, and performing CRUD operations on all records.

### 🔐 Key Design and Security Choices

4. **User registration** – New users sign up via `POST /api/auth/register` with server‑side validation.

5. **Secure login** – Authenticated users receive a **JWT token** via `POST /api/auth/login` for subsequent API calls.

6. **Token‑based security** – All protected endpoints require `Authorization: Bearer <token>` in the header.

7. **Unauthorized access** – Invalid or expired tokens result in HTTP `401 Unauthorized`.

8. **Forbidden access** – Insufficient role permissions result in HTTP `403 Forbidden`, with clear error messages.

### 📊 Core Financial Records Module

9. **CRUD operations** – Full **Create, Read, Update, Delete** on records via `/api/records`, supporting flexible financial workflows.

10. **Smart filtering** – Records can be filtered by **date range, category, and transaction type**, enabling precise data analysis.

11. **Dashboard APIs** – Deliver **income, expenses, totals, and trends** in clean JSON format for easy consumption by frontend dashboards.

12. **Robust validation** – Input is validated using **Spring Boot annotations** (e.g., `@NotNull`, `@Email`) and additional business‑level checks to ensure data integrity.

13. **Developer‑friendly docs** – API documentation is available at `http://localhost:8080/swagger-ui/index.html`, where all endpoints, models, and example payloads are visible and testable, making local testing simple for both frontend and backend developers.

---

## 🐱‍👤 Quick Project Summary

- **Backend framework**: Spring Boot (Java)  
- **Auth**: JWT with **role‑based access control (VIEWER, ANALYST, ADMIN)**  
- **Database**: MySQL only  
- **API docs**: Swagger UI (OpenAPI)
---

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

spring.datasource.url=jdbc:mysql://localhost:3306/zorvyn_finance?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

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

## 📝 Note

This project is currently deployed **locally for demo purposes on MySQL**.  
All deployment instructions and environment variables are documented in this GitHub repository.
