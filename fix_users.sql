-- Quick fix: Xóa bảng users để Hibernate tạo lại với cấu trúc đúng
-- Chạy lệnh: psql -U postgres -d booking_db -f fix_users.sql

DROP TABLE IF EXISTS users CASCADE;