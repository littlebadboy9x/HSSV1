-- Xóa database nếu đã tồn tại và tạo mới
DROP DATABASE IF EXISTS hssv;
CREATE DATABASE hssv CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hssv;

-- Bảng users - Người dùng
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(150) NOT NULL UNIQUE,
    fullName VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    studentId VARCHAR(20) UNIQUE,
    user_type VARCHAR(20) NOT NULL,
    schoolYear VARCHAR(20),
    phoneNumber VARCHAR(15),
    className VARCHAR(50),
    isStaff BIT(1) NOT NULL DEFAULT 0,
    isSuperuser BIT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login DATETIME
);

-- Bảng departments - Khoa/Phòng ban
CREATE TABLE departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Bảng majors - Ngành học
CREATE TABLE majors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    department_id BIGINT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Bảng advisor_profiles - Hồ sơ cố vấn
CREATE TABLE advisor_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    department_id BIGINT,
    title VARCHAR(50),
    bio TEXT,
    expertise VARCHAR(255),
    phone VARCHAR(255), 
    available BIT(1) NOT NULL DEFAULT 1,
    major_id BIGINT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (major_id) REFERENCES majors(id)
);

-- Bảng question_categories - Danh mục câu hỏi
CREATE TABLE question_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Bảng questions - Câu hỏi
CREATE TABLE questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    major_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    view_count INT NOT NULL DEFAULT 0,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES question_categories(id),
    FOREIGN KEY (major_id) REFERENCES majors(id)
);

-- Bảng answers - Câu trả lời
CREATE TABLE answers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);

-- -------------------- DỮ LIỆU MẪU --------------------

-- Dữ liệu mẫu cho bảng departments
INSERT INTO departments (name, description) VALUES
('Khoa Công nghệ thông tin', 'Khoa đào tạo về công nghệ thông tin và khoa học máy tính'),
('Khoa Kinh tế', 'Khoa đào tạo về kinh tế và quản trị kinh doanh'),
('Khoa Ngoại ngữ', 'Khoa đào tạo về ngoại ngữ');

-- Dữ liệu mẫu cho bảng majors
INSERT INTO majors (name, description, department_id) VALUES
('Kỹ thuật phần mềm', 'Chương trình đào tạo về kỹ thuật phần mềm', 1),
('Khoa học máy tính', 'Chương trình đào tạo về khoa học máy tính', 1),
('Quản trị kinh doanh', 'Chương trình đào tạo về quản trị kinh doanh', 2),
('Tiếng Anh', 'Chương trình đào tạo về ngôn ngữ Anh', 3);

-- Dữ liệu mẫu cho bảng question_categories
INSERT INTO question_categories (name, description) VALUES
('Học tập', 'Các câu hỏi liên quan đến học tập, môn học'),
('Đời sống sinh viên', 'Các câu hỏi về đời sống sinh viên, ký túc xá'),
('Thủ tục hành chính', 'Các câu hỏi về thủ tục hành chính, giấy tờ');

-- TÀI KHOẢN TEST CHO CÁC VAI TRÒ

-- 1. Tài khoản Admin (Quản trị viên)
INSERT INTO users (username, fullName, email, password, user_type, isStaff, isSuperuser)
VALUES ('admin', 'Quản Trị Viên', 'admin@example.com', '123456', 'STAFF', 1, 1);

-- Đã thêm giá trị cho cột available
INSERT INTO advisor_profiles (user_id, role, department_id, title, available)
VALUES (1, 'ADMIN', 1, 'Quản trị viên hệ thống', 1);

-- 2. Tài khoản Advisor (Cố vấn học tập CNTT)
INSERT INTO users (username, fullName, email, password, user_type, isStaff, isSuperuser)
VALUES ('advisor_it', 'Cố Vấn CNTT', 'advisor_it@example.com', '123456', 'STAFF', 1, 0);

INSERT INTO advisor_profiles (user_id, role, department_id, title, phone, expertise, available)
VALUES (2, 'ADVISOR', 1, 'Giảng viên', '0987654321', 'Lập trình, Trí tuệ nhân tạo', 1);

-- 3. Tài khoản Advisor (Cố vấn học tập Kinh tế)
INSERT INTO users (username, fullName, email, password, user_type, isStaff, isSuperuser)
VALUES ('advisor_eco', 'Cố Vấn Kinh Tế', 'advisor_eco@example.com', '123456', 'STAFF', 1, 0);

INSERT INTO advisor_profiles (user_id, role, department_id, title, phone, expertise, available)
VALUES (3, 'ADVISOR', 2, 'Giảng viên', '0912345678', 'Quản trị doanh nghiệp, Marketing', 1);

-- 4. Tài khoản Sinh Viên CNTT
INSERT INTO users (username, fullName, email, password, studentId, user_type, className)
VALUES ('student_it', 'Sinh Viên CNTT', 'student_it@example.com', '123456', 'SV001', 'STUDENT', 'CNTT2023A');

-- 5. Tài khoản Sinh Viên Kinh Tế
INSERT INTO users (username, fullName, email, password, studentId, user_type, className)
VALUES ('student_eco', 'Sinh Viên Kinh Tế', 'student_eco@example.com', '123456', 'SV002', 'STUDENT', 'KT2022B');

-- 6. Tài khoản Sinh Viên Ngoại Ngữ
INSERT INTO users (username, fullName, email, password, studentId, user_type, className)
VALUES ('student_lang', 'Sinh Viên Ngoại Ngữ', 'student_lang@example.com', '123456', 'SV003', 'STUDENT', 'NN2023A');

-- 7. Tài khoản Cựu Sinh Viên
INSERT INTO users (username, fullName, email, password, user_type, schoolYear)
VALUES ('alumni', 'Cựu Sinh Viên', 'alumni@example.com', '123456', 'ALUMNI', '2015-2019');

-- 8. Tài khoản Phụ Huynh
INSERT INTO users (username, fullName, email, password, user_type, phoneNumber)
VALUES ('parent', 'Phụ Huynh', 'parent@example.com', '123456', 'PARENT', '0912345678');

-- 9. Tài khoản Học Sinh Phổ Thông
INSERT INTO users (username, fullName, email, password, user_type, className)
VALUES ('highschool', 'Học Sinh THPT', 'highschool@example.com', '123456', 'HIGH_SCHOOL_STUDENT', '12A1 - THPT Nguyễn Trãi');

-- Tạo câu hỏi mẫu
INSERT INTO questions (title, content, user_id, category_id, major_id, status)
VALUES 
('Câu hỏi về lịch thi cuối kỳ', 'Khi nào nhà trường công bố lịch thi học kỳ 1 năm học 2025-2026?', 4, 1, 1, 'PENDING'),
('Thủ tục đăng ký học phần', 'Em cần những giấy tờ gì để đăng ký học phần bổ sung?', 5, 3, 3, 'ANSWERED'),
('Thông tin tuyển sinh 2025', 'Điểm chuẩn dự kiến ngành Công nghệ thông tin là bao nhiêu?', 9, 1, 1, 'PENDING'),
('Chỗ ở ký túc xá', 'Sinh viên năm nhất có được ưu tiên chỗ ở ký túc xá không?', 6, 2, null, 'ANSWERED'),
('Đăng ký ngành học', 'Làm thế nào để đăng ký chuyển ngành từ Kinh tế sang CNTT?', 5, 3, 3, 'PENDING');

-- Tạo câu trả lời mẫu
INSERT INTO answers (content, user_id, question_id)
VALUES 
('Lịch thi học kỳ 1 năm học 2025-2026 sẽ được công bố vào ngày 15/12/2025 trên cổng thông tin sinh viên.', 2, 1),
('Để đăng ký học phần bổ sung, sinh viên cần đơn đăng ký có xác nhận của cố vấn học tập và giảng viên môn học. Ngoài ra, sinh viên phải nộp kèm bảng điểm tích lũy đến thời điểm hiện tại.', 3, 2),
('Sinh viên năm nhất được ưu tiên đăng ký chỗ ở ký túc xá. Thời gian đăng ký bắt đầu từ 01/08 đến 15/08 hàng năm. Sinh viên cần chuẩn bị giấy báo nhập học, CMND/CCCD và 2 ảnh 3x4.', 2, 4);