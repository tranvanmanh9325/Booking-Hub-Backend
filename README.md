# Booking Hub - Backend Service

Dịch vụ Backend cho nền tảng Booking Hub, được xây dựng bằng Java Spring Boot.

## 🛠️ Công Nghệ

- **Framework**: Spring Boot 3.5.9
- **Ngôn ngữ**: Java 21
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security + JWT
- **Build Tool**: Maven

## 📋 Yêu Cầu Hệ Thống

- JDK 21 trở lên
- Maven (hoặc dùng `mvnw` đi kèm)
- PostgreSQL đang chạy
- Redis (Tùy chọn, khuyến nghị có)

## ⚙️ Cài Đặt Biến Môi Trường

Bạn cần tạo file `.env` tại thư mục gốc của `Booking-Hub-Backend` hoặc set biến môi trường hệ thống. Tham khảo `.env.example`.

Các biến quan trọng:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/booking_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# JWT
application.security.jwt.secret-key=your_very_long_secret_key_must_be_at_least_256_bits
application.security.jwt.expiration=86400000 # 1 day
application.security.jwt.refresh-token.expiration=604800000 # 7 days

# CORS
application.cors.allowed-origins=http://localhost:3000

# Google OAuth2 (Optional)
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
```

## 🚀 Hướng Dẫn Chạy

### Cách 1: Chạy trực tiếp với Maven

```bash
# Windows
./mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

API sẽ chạy tại: `http://localhost:8080`

### Cách 2: Chạy với Docker

```bash
docker build -t booking-backend .
docker run -p 8080:8080 --env-file .env booking-backend
```

## 📚 API Documentation

Sau khi server chạy, bạn có thể truy cập tài liệu API (Swagger/OpenAPI):

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
