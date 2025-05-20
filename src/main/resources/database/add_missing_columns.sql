-- Script thêm các cột cần thiết vào cơ sở dữ liệu HSSV
-- Sử dụng: mysql -u root -p123456 hssv < add_missing_columns.sql

-- Thêm cột is_active, is_staff, is_superuser vào bảng users
ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN is_staff BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN is_superuser BOOLEAN DEFAULT FALSE;

-- Thêm cột is_active vào bảng question_categories
ALTER TABLE question_categories ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Thêm cột is_active vào bảng departments
ALTER TABLE departments ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Thêm cột is_active vào bảng majors
ALTER TABLE majors ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- Cập nhật dữ liệu mẫu
UPDATE users SET is_active = TRUE, is_staff = TRUE, is_superuser = TRUE WHERE id = 1;

-- Hiển thị thông báo hoàn thành
SELECT 'Đã thêm các cột cần thiết vào cơ sở dữ liệu thành công!' AS Message; 