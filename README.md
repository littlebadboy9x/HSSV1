# Hệ thống Tư vấn Sinh viên (HSSV)

Hệ thống Tư vấn Sinh viên là một ứng dụng web được xây dựng bằng Java Servlet/JSP, giúp sinh viên đặt câu hỏi và nhận câu trả lời từ các cố vấn học tập.

## Công nghệ sử dụng

- Java Servlet/JSP
- JDBC
- MySQL
- Bootstrap 4
- jQuery
- CKEditor
- Chart.js
- DataTables

## Tính năng đã triển khai

### Module Người dùng
- Đăng ký tài khoản
- Đăng nhập/Đăng xuất
- Quản lý thông tin cá nhân
- Phân quyền (Sinh viên, Cố vấn, Admin)

### Module Câu hỏi
- Xem danh sách câu hỏi
- Lọc câu hỏi theo loại, ngành, trạng thái
- Tạo câu hỏi mới
- Xem chi tiết câu hỏi
- Quản lý câu hỏi cá nhân

### Module Câu trả lời
- Hiển thị danh sách câu trả lời cho câu hỏi
- Cố vấn trả lời câu hỏi
- Cập nhật trạng thái câu hỏi khi có câu trả lời

### Module Tìm kiếm
- Tìm kiếm cơ bản
- Tìm kiếm nâng cao với nhiều bộ lọc
- Sắp xếp kết quả tìm kiếm

### Module Thống kê
- Thống kê tổng quan
- Thống kê theo khoa
- Thống kê theo ngành
- Thống kê theo loại câu hỏi
- Thống kê theo trạng thái

### Module Quản lý
- Quản lý người dùng (thêm, sửa, xóa)
- Quản lý cố vấn
- Quản lý câu hỏi và câu trả lời

## Cấu trúc dự án

```
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── example/
│   │           └── hssv1/
│   │               ├── controller/    # Các servlet xử lý request
│   │               ├── dao/           # Các lớp truy cập dữ liệu
│   │               ├── model/         # Các lớp đối tượng
│   │               └── util/          # Các lớp tiện ích
│   ├── resources/                     # Tài nguyên
│   └── webapp/
│       ├── resources/                 # CSS, JS, hình ảnh
│       └── WEB-INF/
│           ├── views/                 # Các trang JSP
│           └── web.xml                # Cấu hình ứng dụng
```

## Hướng dẫn cài đặt

1. Clone dự án từ repository
2. Import dự án vào IDE (Eclipse, IntelliJ IDEA)
3. Cấu hình MySQL database theo script trong `src/main/resources/database/create_database.sql`
4. Cấu hình kết nối database trong `src/main/java/org/example/hssv1/util/DatabaseConnection.java`
5. Build và deploy ứng dụng lên server Tomcat

## Tài khoản mặc định

- Admin: admin/admin123
- Cố vấn: advisor/advisor123
- Sinh viên: student/student123 