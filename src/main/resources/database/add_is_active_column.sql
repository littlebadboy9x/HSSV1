-- Thêm cột is_active vào bảng users nếu chưa tồn tại
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Thêm cột is_active vào bảng question_categories nếu chưa tồn tại
ALTER TABLE question_categories ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Thêm cột is_active vào bảng departments nếu chưa tồn tại
ALTER TABLE departments ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Thêm cột is_active vào bảng majors nếu chưa tồn tại
ALTER TABLE majors ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- Thêm các cột khác cần thiết
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_staff BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_superuser BOOLEAN DEFAULT FALSE; 