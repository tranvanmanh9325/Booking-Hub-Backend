# Booking Hub Backend 🚀💎

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.10-green?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7.x-red?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

## 📖 Giới Thiệu

**Booking Hub Backend** là trái tim của hệ thống Booking Hub, cung cấp các API RESTful mạnh mẽ, bảo mật và hiệu năng cao để phục vụ các ứng dụng Frontend và Mobile. Được xây dựng trên nền tảng **Spring Boot** với kiến trúc hiện đại, hệ thống đảm bảo khả năng mở rộng (scalability) và chịu tải tốt.

## 🛠️ Công Nghệ Sử Dụng

Dự án sử dụng các công nghệ hàng đầu trong hệ sinh thái Java & Cloud Native:

- **Core Framework**: Spring Boot 3.5.10
- **Language**: Java 21 LTS
- **Database**:
  - PostgreSQL (Primary Data Store)
  - Flyway (Database Migration)
- **Caching & Rate Limiting**: Redis, Bucket4j
- **Security**:
  - Spring Security 6
  - JWT (JSON Web Token)
  - OAuth2 (Google Login)
- **Documentation**: Swagger UI / OpenAPI 3 (SpringDoc)
- **Monitoring**: Spring Boot Actuator, Prometheus, Sentry (Error Tracking)
- **Tools**: Lombok, MapStruct, Maven, Docker

## 📂 Cấu Trúc Dự Án

Chúng tôi tuân thủ kiến trúc Layered Architecture để đảm bảo Clean Code và dễ dàng bảo trì:

```
src/main/java/com/example/booking/
├── config/           # Cấu hình hệ thống (Security, Redis, Swagger...)
├── controller/       # REST API Endpoints (Xử lý request/response)
├── service/          # Business Logic (Xử lý nghiệp vụ chính)
├── repository/       # Data Access Layer (JPA Repositories)
├── model/            # JPA Entities (Ánh xạ Database)
├── dto/              # Data Transfer Objects (Requests/Responses models)
├── mapper/           # MapStruct Interfaces (Chuyển đổi Entity <-> DTO)
├── exception/        # Global Exception Handling
├── security/         # Custom Security Filters & Configs
├── validation/       # Custom Validators
└── util/             # Các tiện ích chung
```

## � Bắt Đầu

### 1. Yêu Cầu Hệ Thống

- **Java Development Kit (JDK)**: Phiên bản 21 trở lên
- **Maven**: 3.8+ (hoặc dùng `mvnw` có sẵn)
- **Database**: PostgreSQL 14+
- **Cache**: Redis 6+ (Khuyến nghị)

### 2. Cài Đặt

Clone repository:

```bash
git clone https://github.com/your-username/booking-hub.git
cd Booking-Hub-Backend
```

### 3. Cấu Hình Môi Trường

Tại thư mục gốc, tạo file `.env` dựa trên file mẫu (nếu chưa có file mẫu, hãy tham khảo cấu trúc dưới đây).

**File .env:**

```properties
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/booking_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your_super_secret_key_must_be_very_long_at_least_256_bits
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Service Bindings
SERVER_PORT=8080

# Third-party Services (Optional)
CLOUDINARY_URL=cloudinary://key:secret@cloud_name
SENTRY_DSN=your_sentry_dsn
```

### 4. Chạy Ứng Dụng

**Sử dụng Maven Wrapper (Khuyên dùng):**

```bash
# Windows
./mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

**Sử dụng Docker:**

```bash
docker build -t booking-hub-backend .
docker run -p 8080:8080 --env-file .env booking-hub-backend
```

Server sẽ khởi động tại: `http://localhost:8080`

## 📚 API Documentation

Hệ thống tích hợp sẵn Swagger UI để bạn dễ dàng tra cứu và test API:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 🧪 Testing

Chạy toàn bộ unit tests và integration tests:

```bash
./mvnw test
```

## 🤝 Contribution

Mọi đóng góp đều được hoan nghênh! Vui lòng tạo Pull Request hoặc mở Issue để thảo luận.

## 📝 License

Dự án này được phân phối dưới giấy phép [MIT](LICENSE).

---
*Được phát triển bởi đội ngũ Booking Hub.*
