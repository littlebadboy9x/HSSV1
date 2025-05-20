-- Script tạo đầy đủ cơ sở dữ liệu HSSV từ đầu
-- Sử dụng: mysql -u root -p123456 < create_full_database.sql

-- Tạo cơ sở dữ liệu nếu chưa tồn tại
CREATE DATABASE IF NOT EXISTS hssv CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Sử dụng cơ sở dữ liệu
USE hssv;

-- Xóa các bảng nếu đã tồn tại để tạo lại
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS question_categories;
DROP TABLE IF EXISTS advisor_profiles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS majors;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS sys_config;

-- Tạo bảng departments (Khoa)
CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng majors (Ngành học)
CREATE TABLE majors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    department_id INT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng users (Người dùng)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    user_type ENUM('student', 'advisor', 'admin') NOT NULL DEFAULT 'student',
    is_active BOOLEAN DEFAULT TRUE,
    is_staff BOOLEAN DEFAULT FALSE,
    is_superuser BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng advisor_profiles (Hồ sơ tư vấn viên)
CREATE TABLE advisor_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    department_id INT,
    major_id INT,
    title VARCHAR(50),
    bio TEXT,
    expertise TEXT,
    role ENUM('advisor', 'admin') NOT NULL DEFAULT 'advisor',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng question_categories (Danh mục câu hỏi)
CREATE TABLE question_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng questions (Câu hỏi)
CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    category_id INT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('pending', 'answered', 'closed') NOT NULL DEFAULT 'pending',
    is_anonymous BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES question_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng answers (Câu trả lời)
CREATE TABLE answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    user_id INT,
    content TEXT NOT NULL,
    is_accepted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo bảng sys_config (Cấu hình hệ thống)
CREATE TABLE sys_config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chèn dữ liệu mẫu vào bảng departments
INSERT INTO departments (name, description) VALUES
('Khoa Công nghệ thông tin', 'Khoa đào tạo các ngành liên quan đến công nghệ thông tin'),
('Khoa Kinh tế', 'Khoa đào tạo các ngành liên quan đến kinh tế, quản trị'),
('Khoa Ngoại ngữ', 'Khoa đào tạo các ngành ngôn ngữ'),
('Khoa Xây dựng', 'Khoa đào tạo các ngành liên quan đến xây dựng, kiến trúc');

-- Chèn dữ liệu mẫu vào bảng majors
INSERT INTO majors (department_id, name, description) VALUES
(1, 'Công nghệ thông tin', 'Chuyên ngành đào tạo về công nghệ thông tin'),
(1, 'Kỹ thuật phần mềm', 'Chuyên ngành đào tạo về phát triển phần mềm'),
(1, 'An toàn thông tin', 'Chuyên ngành đào tạo về bảo mật thông tin'),
(2, 'Quản trị kinh doanh', 'Chuyên ngành đào tạo về quản trị doanh nghiệp'),
(2, 'Kế toán', 'Chuyên ngành đào tạo về kế toán, kiểm toán'),
(3, 'Ngôn ngữ Anh', 'Chuyên ngành đào tạo về ngôn ngữ Anh'),
(3, 'Ngôn ngữ Nhật', 'Chuyên ngành đào tạo về ngôn ngữ Nhật'),
(4, 'Kỹ thuật xây dựng', 'Chuyên ngành đào tạo về xây dựng công trình'),
(4, 'Kiến trúc', 'Chuyên ngành đào tạo về thiết kế kiến trúc');

-- Chèn dữ liệu mẫu vào bảng users
INSERT INTO users (username, password, email, first_name, last_name, user_type, is_active, is_staff, is_superuser) VALUES
('admin', 'admin123', 'admin@example.com', 'Admin', 'System', 'admin', TRUE, TRUE, TRUE),
('tuvan01', 'tuvan123', 'tuvan01@example.com', 'Nguyễn', 'Tư Vấn', 'advisor', TRUE, TRUE, FALSE),
('tuvan02', 'tuvan123', 'tuvan02@example.com', 'Trần', 'Hướng Dẫn', 'advisor', TRUE, TRUE, FALSE),
('sinhvien01', 'sv123', 'sv01@example.com', 'Lê', 'Sinh Viên', 'student', TRUE, FALSE, FALSE),
('sinhvien02', 'sv123', 'sv02@example.com', 'Phạm', 'Học Sinh', 'student', TRUE, FALSE, FALSE);

-- Chèn dữ liệu mẫu vào bảng advisor_profiles
INSERT INTO advisor_profiles (user_id, department_id, major_id, title, bio, expertise, role) VALUES
(1, 1, 1, 'Giám đốc CNTT', 'Chuyên gia về công nghệ thông tin với hơn 10 năm kinh nghiệm', 'Phát triển phần mềm, Quản lý dự án CNTT', 'admin'),
(2, 1, 2, 'Giảng viên', 'Giảng viên khoa CNTT với chuyên môn về phát triển phần mềm', 'Java, Web Development, Database', 'advisor'),
(3, 2, 4, 'Trưởng khoa', 'Trưởng khoa Kinh tế với nhiều năm kinh nghiệm giảng dạy', 'Quản trị kinh doanh, Marketing', 'advisor');

-- Chèn dữ liệu mẫu vào bảng question_categories
INSERT INTO question_categories (name, description, is_active) VALUES
('Học tập', 'Các câu hỏi liên quan đến việc học tập, môn học', TRUE),
('Nghề nghiệp', 'Các câu hỏi liên quan đến nghề nghiệp, việc làm sau khi tốt nghiệp', TRUE),
('Đời sống sinh viên', 'Các câu hỏi liên quan đến đời sống sinh viên, hoạt động ngoại khóa', TRUE),
('Học phí & Học bổng', 'Các câu hỏi liên quan đến học phí, học bổng, hỗ trợ tài chính', TRUE),
('Cơ sở vật chất', 'Các câu hỏi liên quan đến cơ sở vật chất, ký túc xá', TRUE);

-- Chèn dữ liệu mẫu vào bảng questions
INSERT INTO questions (user_id, category_id, title, content, status) VALUES
(4, 1, 'Làm thế nào để đăng ký môn học trực tuyến?', 'Tôi là sinh viên năm nhất và không biết cách đăng ký môn học trực tuyến. Xin hướng dẫn chi tiết.', 'pending'),
(4, 2, 'Cơ hội việc làm ngành Công nghệ thông tin?', 'Tôi đang học ngành CNTT và muốn biết về cơ hội việc làm sau khi tốt nghiệp. Các công ty nào thường tuyển dụng sinh viên từ trường mình?', 'answered'),
(5, 3, 'Làm thế nào để tham gia câu lạc bộ sinh viên?', 'Tôi muốn tham gia các câu lạc bộ sinh viên để phát triển kỹ năng mềm. Quy trình đăng ký như thế nào?', 'answered'),
(5, 4, 'Thông tin về học bổng khuyến khích học tập?', 'Tôi muốn biết thêm về học bổng khuyến khích học tập. Điều kiện để được nhận học bổng là gì?', 'pending');

-- Chèn dữ liệu mẫu vào bảng answers
INSERT INTO answers (question_id, user_id, content, is_accepted) VALUES
(2, 2, 'Ngành Công nghệ thông tin hiện có rất nhiều cơ hội việc làm. Các công ty lớn như FPT, Viettel, CMC thường xuyên tuyển dụng sinh viên từ trường ta. Ngoài ra còn có các công ty nước ngoài như Intel, IBM, Samsung cũng thường xuyên tổ chức các buổi tuyển dụng tại trường. Bạn nên chuẩn bị kỹ năng lập trình, tiếng Anh và tham gia các dự án thực tế để tăng cơ hội được tuyển dụng.', TRUE),
(3, 3, 'Để tham gia câu lạc bộ sinh viên, bạn cần theo dõi thông báo tuyển thành viên mới của các câu lạc bộ vào đầu mỗi học kỳ. Thông thường, các CLB sẽ có form đăng ký online hoặc bạn có thể đến văn phòng Đoàn - Hội để được hướng dẫn chi tiết. Mỗi CLB sẽ có quy trình tuyển chọn riêng, có thể bao gồm phỏng vấn hoặc thử thách.', TRUE);

-- Chèn dữ liệu mẫu vào bảng sys_config
INSERT INTO sys_config (config_key, config_value, description) VALUES
('site_name', 'Hệ thống Tư vấn Sinh viên', 'Tên hệ thống'),
('site_description', 'Hệ thống hỗ trợ tư vấn cho sinh viên', 'Mô tả hệ thống'),
('admin_email', 'admin@example.com', 'Email của quản trị viên'),
('items_per_page', '10', 'Số mục hiển thị trên mỗi trang');

-- Hiển thị thông báo hoàn thành
SELECT 'Đã tạo cơ sở dữ liệu HSSV thành công!' AS Message; 