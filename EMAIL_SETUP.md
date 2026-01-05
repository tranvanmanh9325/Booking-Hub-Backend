# Hướng Dẫn Cấu Hình Email

## Bước 1: Tạo App Password cho Gmail

Để gửi email qua Gmail SMTP, bạn cần tạo App Password:

1. Đăng nhập vào tài khoản Google của bạn
2. Truy cập: https://myaccount.google.com/apppasswords
3. Chọn "Mail" và "Other (Custom name)"
4. Nhập tên: "Booking Hub"
5. Nhấn "Generate"
6. Copy App Password (16 ký tự, không có khoảng trắng)

## Bước 2: Cấu hình trong file .env

1. Copy file `.env.example` thành `.env` (nếu chưa có):
   ```bash
   cp .env.example .env
   ```

2. Mở file `.env` và cập nhật các giá trị sau:

```env
# Email Configuration (Gmail SMTP)
MAIL_USERNAME=manhtrana1k45tl@gmail.com
MAIL_PASSWORD=your-app-password-here  # Thay bằng App Password bạn vừa tạo

# Partnership Email Recipient
PARTNERSHIP_RECIPIENT_EMAIL=manhtrana1k45tl@gmail.com
```

**Lưu ý:** File `.env` đã được thêm vào `.gitignore` nên sẽ không bị commit lên Git, đảm bảo bảo mật thông tin nhạy cảm.

## Bước 3: Khởi động lại Backend

Sau khi cấu hình xong, khởi động lại Spring Boot application. Ứng dụng sẽ tự động load các biến môi trường từ file `.env`.

## Các biến môi trường khác

Bạn cũng có thể cấu hình các biến môi trường khác trong file `.env`:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` - Cấu hình database
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` - Cấu hình Redis
- `JWT_SECRET`, `JWT_EXPIRATION` - Cấu hình JWT
- `CORS_ALLOWED_ORIGINS` - Cấu hình CORS

Xem file `.env.example` để biết tất cả các biến môi trường có sẵn.

## Lưu ý

- App Password chỉ hiển thị một lần, hãy lưu lại cẩn thận
- Nếu tài khoản Gmail có bật 2-Step Verification, bạn phải tạo App Password
- Nếu không có 2-Step Verification, bạn cần bật nó trước khi tạo App Password
- File `.env` không được commit lên Git để bảo mật thông tin nhạy cảm
