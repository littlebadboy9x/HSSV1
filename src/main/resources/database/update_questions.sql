-- Thêm cột major_id vào bảng questions
ALTER TABLE questions ADD COLUMN major_id INT;

-- Kiểm tra và thêm khóa ngoại cho major_id nếu chưa tồn tại
SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'questions'
    AND COLUMN_NAME = 'major_id'
    AND REFERENCED_TABLE_NAME = 'majors'
);

SET @sql = IF(@constraint_exists = 0,
    'ALTER TABLE questions ADD FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE SET NULL',
    'SELECT "Foreign key already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 