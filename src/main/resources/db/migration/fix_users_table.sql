-- Script để fix bảng users nếu có dữ liệu null
-- Chạy script này nếu gặp lỗi "column password contains null values"

-- Option 1: Xóa bảng users nếu không cần giữ dữ liệu cũ (UNCOMMENT nếu muốn dùng)
-- DROP TABLE IF EXISTS users CASCADE;

-- Option 2: Xóa các record có password null (UNCOMMENT nếu muốn dùng)
-- DELETE FROM users WHERE password IS NULL;

-- Option 3: Cập nhật password null thành giá trị mặc định (không khuyến khích)
-- UPDATE users SET password = '' WHERE password IS NULL;

-- Sau đó thêm constraint NOT NULL
ALTER TABLE users ALTER COLUMN password SET NOT NULL;


