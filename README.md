# Hệ thống Tư vấn Sinh viên (HSSV1)

Dự án web sử dụng Jakarta EE 11 và Tomcat 11.

## Yêu cầu hệ thống

- Java Development Kit (JDK) 11 trở lên
- Apache Tomcat 11.0.0 trở lên
- MySQL 8.0 trở lên

## Cấu hình Tomcat 11

1. Tải Tomcat 11.0.0 tại [https://tomcat.apache.org/download-11.cgi](https://tomcat.apache.org/download-11.cgi)
2. Giải nén file zip/tar.gz vào thư mục mong muốn
3. Cấu hình biến môi trường:
   - `CATALINA_HOME`: Trỏ tới thư mục Tomcat
   - `JAVA_HOME`: Trỏ tới thư mục JDK

## Cấu hình cơ sở dữ liệu

1. Tạo cơ sở dữ liệu MySQL với tên `hssv`
2. Cập nhật thông tin kết nối cơ sở dữ liệu trong file `src/main/java/org/example/hssv1/util/HibernateUtil.java`

```java
settings.put(Environment.URL, "jdbc:mysql://localhost:3306/hssv?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
settings.put(Environment.USER, "root"); // Cập nhật tài khoản
settings.put(Environment.PASS, "123456"); // Cập nhật mật khẩu
```

## Các thay đổi đã thực hiện để chuyển sang Jakarta EE 11

1. Cập nhật các phụ thuộc trong pom.xml từ Java EE 8 sang Jakarta EE 11
2. Thay đổi tất cả import từ `javax.*` sang `jakarta.*` trong tất cả các file Java
3. Cập nhật file `web.xml` để sử dụng Jakarta EE 11
4. Cập nhật file `persistence.xml` để sử dụng Jakarta Persistence 3.1
5. Cập nhật trong các file JSP, đổi các taglib từ `http://java.sun.com/jsp/jstl/*` sang `jakarta.tags.*`

## Biên dịch và chạy dự án

### Sử dụng Maven

```bash
# Biên dịch dự án
mvn clean package

# Copy file WAR vào thư mục webapps của Tomcat
cp target/HSSV1-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/HSSV1.war
```

### Sử dụng IDE

1. IntelliJ IDEA:
   - Cấu hình Tomcat Server trong Run/Debug Configurations
   - Chọn Deployment và thêm artifact WAR

2. Eclipse:
   - Cài đặt Eclipse Enterprise for Jakarta EE plugin
   - Cấu hình Tomcat 11 Server trong Server tab
   - Right-click vào dự án > Run As > Run on Server

## Truy cập ứng dụng

- URL cơ bản: `http://localhost:8080/HSSV1/`
- Trang đăng nhập: `http://localhost:8080/HSSV1/login`
- Trang đăng ký: `http://localhost:8080/HSSV1/register`

## Tài khoản mặc định

- Admin: admin@example.com / admin123
- Cố vấn: advisor@example.com / advisor123
- Sinh viên: student@example.com / student123 