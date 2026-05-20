# SocialX - Backend API

SocialX is the Backend system for a social media platform, providing APIs for the Frontend application (such as Gram View Vibe). The project is built on a Monolith architecture using Java and Spring Boot.

## 🚀 Technologies Used

*   **Language**: Java 17
*   **Framework**: Spring Boot 3.4.9
*   **Primary Database**: PostgreSQL
*   **Caching & Session**: Redis (using Redisson)
*   **File/Media Storage**: MinIO (S3-compatible)
*   **Security**: Spring Security + JWT Token
*   **Real-time (Chat)**: Spring WebSocket (STOMP protocol)
*   **Object Mapping**: MapStruct
*   **Boilerplate Reduction**: Lombok
*   **API Documentation**: Swagger UI (springdoc-openapi)

## 📁 Folder Structure (Main Packages)

*   `config`: Contains system configuration files (Security, WebSocket, Redis, Minio, etc.).
*   `domain`: Contains Entities (Models) mapped to the database.
*   `repository`: Data access layer communicating with the Database (Spring Data JPA).
*   `service`: Contains business logic layer.
*   `web/rest`: Controller layer handling HTTP Requests (APIs).
*   `security`: Handles authentication and authorization (JWT Filter, Token Provider).
*   `integration`: Contains source code integrating with external systems (MinIO, Firebase, etc.).

## 🛠 Prerequisites

To run this project locally, you need to install:
1.  **Java 17** or newer.
2.  **Maven** (or use the provided Maven wrapper in the project).
3.  **PostgreSQL** running on the default port 5432.
4.  **Redis** running on port 6379.
5.  **MinIO Server** (for uploading images/videos).

## ⚙️ Environment Configuration (Application Properties)

You need to update the connection parameters in the `src/main/resources/application.yml` (or `.properties`) file to match your local environment:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/socialx_db
    username: <your_db_username>
    password: <your_db_password>
  
  data:
    redis:
      host: localhost
      port: 6379

minio:
  url: http://localhost:9000
  accessKey: <your_minio_access_key>
  secretKey: <your_minio_secret_key>
  bucketName: socialx-media

jwt:
  secret: <your_jwt_secret_key_base64>
  expiration: 86400000 # 24 hours
```

## ▶️ Run Locally

**Step 1**: Build the project using Maven
```bash
mvn clean install -DskipTests
```

**Step 2**: Run the application
```bash
mvn spring-boot:run
```
*Alternatively, you can open the project using IntelliJ IDEA / Eclipse and run the `SocialXApplication.java` file.*

The application will start on the default port: `http://localhost:8080`.

## 📚 API Documentation (Swagger UI)

While the project is running, you can view and test the APIs via the Swagger UI interface:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

## ✨ Key Features

*   **Authentication**: Register, Login, Token Management (JWT).
*   **Users**: Manage personal information (Profile), Search users.
*   **Posts**: Create posts (images/videos), List posts on Newsfeed (Pagination).
*   **Interaction**: Like, Comment on posts.
*   **Messaging (Real-time Chat)**:
    *   Direct 1-1 chat.
    *   Create group chats, add/remove members.
    *   Send text, image, video messages (via WebSocket & STOMP).
    *   Update group info (Name, Avatar).
*   **Friend Suggestions / Follow**: Follow users and view suggested friends list.
