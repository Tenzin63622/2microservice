# 📸 Instagram-like Social Media Backend – Microservices with Spring Boot

A fully functional Instagram-like backend built using **Spring Boot Microservices**, featuring user management, posts, likes, comments, follows, search, and notifications.

---

## 🏗️ Architecture Overview

```
                        ┌─────────────────┐
                        │   API Gateway   │ :8080
                        │ (Spring Cloud   │
                        │   Gateway +JWT) │
                        └────────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
    ┌─────────▼──────┐  ┌────────▼───────┐  ┌──────▼────────┐
    │  User Service  │  │  Post Service  │  │ Like Service  │
    │    :8081       │  │    :8082       │  │   :8083       │
    └────────────────┘  └────────────────┘  └───────────────┘
              │                  │                  │
    ┌─────────▼──────┐  ┌────────▼───────┐  ┌──────▼────────┐
    │Comment Service │  │ Follow Service │  │Search Service │
    │    :8084       │  │    :8085       │  │   :8086       │
    └────────────────┘  └────────────────┘  └───────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │ Notification Service    │
                    │         :8087           │
                    └─────────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │     Eureka Server       │
                    │   (Service Registry)    │
                    │         :8761           │
                    └─────────────────────────┘
```

---

## 📦 Microservices

| Service              | Port | Description                        |
|----------------------|------|------------------------------------|
| eureka-server        | 8761 | Service registry & discovery       |
| api-gateway          | 8080 | Entry point, JWT validation, routing |
| user-service         | 8081 | Registration, login, profile       |
| post-service         | 8082 | Upload posts, feed, captions       |
| like-service         | 8083 | Like/unlike posts                  |
| comment-service      | 8084 | Add/view/delete comments           |
| follow-service       | 8085 | Follow/unfollow users              |
| search-service       | 8086 | Search users and posts             |
| notification-service | 8087 | Bonus: Notifications for events    |

---

## ⚙️ Tech Stack

- **Spring Boot 3.2** – Core framework
- **Spring Cloud Netflix Eureka** – Service discovery
- **Spring Cloud Gateway** – API Gateway with JWT auth filter
- **Spring Data JPA + Hibernate** – ORM
- **MySQL 8** – Database (one per service)
- **JWT (jjwt)** – Authentication
- **Lombok** – Reduce boilerplate
- **OpenFeign** – Service-to-service communication
- **Docker + Docker Compose** – Containerization
- **Jenkins** – CI/CD pipeline

---

## 🚀 How to Run

### Option 1: Run with Docker Compose (Recommended)

**Prerequisites:** Docker, Docker Compose installed

```bash
# 1. Clone the project
git clone <your-repo-url>
cd insta-backend

# 2. Build all services (from each service directory)
cd user-service && mvn clean package -DskipTests && cd ..
cd post-service && mvn clean package -DskipTests && cd ..
# ... repeat for all services

# 3. Start everything
docker-compose up --build -d

# 4. Check status
docker-compose ps

# 5. View logs
docker-compose logs -f user-service
```

**Services will start in this order:**
1. MySQL (waits for healthcheck)
2. Eureka Server
3. All microservices
4. API Gateway

### Option 2: Run Locally (Without Docker)

**Prerequisites:** Java 17, Maven, MySQL 8 running locally

```bash
# 1. Create all databases in MySQL
mysql -u root -p < init-db.sql

# 2. Start Eureka Server first
cd eureka-server
mvn spring-boot:run &

# 3. Wait 15 seconds, then start all services
cd user-service && mvn spring-boot:run &
cd post-service && mvn spring-boot:run &
cd like-service && mvn spring-boot:run &
cd comment-service && mvn spring-boot:run &
cd follow-service && mvn spring-boot:run &
cd search-service && mvn spring-boot:run &
cd notification-service && mvn spring-boot:run &

# 4. Start API Gateway last
cd api-gateway && mvn spring-boot:run
```

---

## 🔌 API Reference

All requests go through: **`http://localhost:8080`**

### 🔐 Auth (No token needed)

| Method | Endpoint              | Body                                      | Description        |
|--------|-----------------------|-------------------------------------------|--------------------|
| POST   | `/api/auth/register`  | `{username, email, password, fullName}`   | Register new user  |
| POST   | `/api/auth/login`     | `{email, password}`                       | Login, get JWT     |

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "userId": 1
}
```

> **For all protected routes**, add header:
> `Authorization: Bearer <your_token>`

---

### 👤 User Module

| Method | Endpoint                       | Auth | Description              |
|--------|--------------------------------|------|--------------------------|
| GET    | `/api/users/{userId}`          | ✅   | Get user profile by ID   |
| GET    | `/api/users/username/{name}`   | ✅   | Get profile by username  |
| PUT    | `/api/users/{userId}`          | ✅   | Update your profile      |
| GET    | `/api/users/search?q=john`     | ✅   | Search users             |

---

### 📷 Post Module

| Method | Endpoint                      | Auth | Description          |
|--------|-------------------------------|------|----------------------|
| POST   | `/api/posts`                  | ✅   | Create a post        |
| GET    | `/api/posts`                  | ✅   | Get all posts (feed) |
| GET    | `/api/posts/{postId}`         | ✅   | Get single post      |
| GET    | `/api/posts/user/{userId}`    | ✅   | Get user's posts     |
| PUT    | `/api/posts/{postId}`         | ✅   | Update your post     |
| DELETE | `/api/posts/{postId}`         | ✅   | Delete your post     |
| GET    | `/api/posts/search?q=sunset`  | ✅   | Search posts         |

**Create Post Body:**
```json
{
  "imageUrl": "https://example.com/photo.jpg",
  "caption": "Beautiful sunset! #nature #travel",
  "hashtags": ["nature", "travel", "sunset"]
}
```

---

### ❤️ Like Module

| Method | Endpoint            | Auth | Description              |
|--------|---------------------|------|--------------------------|
| POST   | `/api/likes/{postId}` | ✅  | Like a post              |
| DELETE | `/api/likes/{postId}` | ✅  | Unlike a post            |
| GET    | `/api/likes/{postId}` | ✅  | Get like count for post  |

---

### 💬 Comment Module

| Method | Endpoint                        | Auth | Description            |
|--------|---------------------------------|------|------------------------|
| POST   | `/api/comments/post/{postId}`   | ✅   | Add comment to post    |
| GET    | `/api/comments/post/{postId}`   | ✅   | Get all comments       |
| DELETE | `/api/comments/{commentId}`     | ✅   | Delete your comment    |

---

### 👥 Follow Module

| Method | Endpoint                         | Auth | Description              |
|--------|----------------------------------|------|--------------------------|
| POST   | `/api/follows/{targetUserId}`    | ✅   | Follow a user            |
| DELETE | `/api/follows/{targetUserId}`    | ✅   | Unfollow a user          |
| GET    | `/api/follows/{userId}/stats`    | ✅   | Get followers/following count |
| GET    | `/api/follows/{userId}/followers`| ✅   | Get follower IDs         |
| GET    | `/api/follows/{userId}/following`| ✅   | Get following IDs        |

---

### 🔍 Search Module

| Method | Endpoint              | Auth | Description                      |
|--------|-----------------------|------|----------------------------------|
| GET    | `/api/search?q=john`  | ✅   | Search both users and posts      |

---

### 🔔 Notification Module (Bonus)

| Method | Endpoint                          | Auth | Description              |
|--------|-----------------------------------|------|--------------------------|
| GET    | `/api/notifications`              | ✅   | Get all notifications    |
| PUT    | `/api/notifications/read-all`     | ✅   | Mark all as read         |
| GET    | `/api/notifications/unread-count` | ✅   | Get unread count         |

---

## 🧪 Quick Test (Step by Step)

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@test.com","password":"password123","fullName":"Alice Smith"}'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@test.com","password":"password123"}'
# Copy the token from response!

# 3. Create a post (replace TOKEN)
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"imageUrl":"https://example.com/img.jpg","caption":"Hello World! #first","hashtags":["first","hello"]}'

# 4. Get all posts
curl http://localhost:8080/api/posts \
  -H "Authorization: Bearer TOKEN"
```

---

## 🔧 Jenkins CI/CD Setup

1. Install Jenkins with plugins: Git, Maven, Docker Pipeline
2. Add credentials in Jenkins:
   - ID: `docker-hub-credentials` (DockerHub username/password)
3. Create a new Pipeline job
4. Set SCM to your Git repo
5. Set `Jenkinsfile` as the pipeline script
6. Update `DOCKER_HUB_REPO` in Jenkinsfile with your DockerHub username
7. Run the pipeline!

**Pipeline Stages:**
```
Checkout → Build All Services → Run Tests → Docker Build & Push → Deploy → Health Check
```

---

## 📁 Project Structure

```
insta-backend/
├── eureka-server/
├── api-gateway/
├── user-service/
│   └── src/main/java/com/insta/userservice/
│       ├── controller/    (REST endpoints)
│       ├── service/       (Business logic)
│       ├── repository/    (DB access)
│       ├── model/         (JPA entities)
│       ├── dto/           (Request/Response objects)
│       ├── config/        (JWT, Security config)
│       └── exception/     (Error handling)
├── post-service/
├── like-service/
├── comment-service/
├── follow-service/
├── search-service/
├── notification-service/
├── docker-compose.yml
├── init-db.sql
├── Jenkinsfile
└── README.md
```

---

## 🛠️ Configuration

**To change MySQL password**, update in `docker-compose.yml`:
```yaml
MYSQL_ROOT_PASSWORD: your_password
SPRING_DATASOURCE_PASSWORD: your_password
```
And in each service's `application.yml`:
```yaml
spring.datasource.password: your_password
```

**JWT Secret** is set in:
- `user-service/src/main/resources/application.yml`
- `api-gateway/src/main/resources/application.yml`

Both must use the **same secret**!

---

## 🌐 Service Discovery

Visit **http://localhost:8761** to see all registered services in the Eureka dashboard.

---

## 📌 Notes

- All APIs go through the **API Gateway** on port 8080
- JWT token expires in **24 hours**
- `X-User-Id` header is automatically added by the gateway from the JWT — you don't need to send it manually
- Each service has its **own MySQL database** (true microservices pattern)
- `POST /api/notifications` is meant to be called internally by other services when events happen (like, comment, follow)
