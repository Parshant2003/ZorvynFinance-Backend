# ZorvynFinance-Backend
Finance dashboard backend with JWT authentication and role‑based access control
## 🚀 About the Project & Permissions

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

This project is currently deployed **locally for demo purposes on MySQL**.  
All deployment instructions and environment variables are documented in the GitHub repository.
