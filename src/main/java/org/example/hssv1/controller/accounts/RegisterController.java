package org.example.hssv1.controller.accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.example.hssv1.dao.UserDAO;
import org.example.hssv1.model.CustomUser;

import java.io.IOException;

/**
 * Controller xử lý đăng ký tài khoản
 */
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/accounts/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Lấy thông tin từ form
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String studentId = request.getParameter("studentId"); // MSSV
        String userTypeStr = request.getParameter("userType");

        // Thông tin bổ sung theo loại người dùng
        String schoolYear = request.getParameter("schoolYear");      // cho cựu sinh viên
        String phoneNumber = request.getParameter("phoneNumber");    // cho phụ huynh
        String className = request.getParameter("className");        // cho học sinh phổ thông

        // Kiểm tra dữ liệu
        boolean hasError = false;
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty() ||
            userTypeStr == null || userTypeStr.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            hasError = true;
        } else if (password.length() < 6) {
            request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự.");
            hasError = true;
        } else if (userDAO.findByUsername(username) != null) {
            request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            hasError = true;
        } else if (userDAO.findByEmail(email) != null) {
            request.setAttribute("errorMessage", "Email đã tồn tại.");
            hasError = true;
        }
        
        // Kiểm tra thông tin bổ sung theo loại người dùng
        CustomUser.UserType userType;
        try {
            userType = CustomUser.UserType.valueOf(userTypeStr.toUpperCase());
            
            // Kiểm tra thông tin bổ sung theo loại người dùng
            switch (userType) {
                case ALUMNI: // Cựu sinh viên
                    if (schoolYear == null || schoolYear.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "Vui lòng nhập niên khóa.");
                        hasError = true;
                    }
                    break;
                case PARENT: // Phụ huynh
                    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "Vui lòng nhập số điện thoại.");
                        hasError = true;
                    }
                    break;
                case HIGH_SCHOOL_STUDENT: // Học sinh phổ thông
                    if (className == null || className.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "Vui lòng nhập thông tin lớp.");
                        hasError = true;
                    }
                    break;
                case STUDENT: // Sinh viên
                    if (studentId == null || studentId.trim().isEmpty()) {
                        request.setAttribute("errorMessage", "Vui lòng nhập mã số sinh viên.");
                        hasError = true;
                    }
                    break;
                default:
                    break;
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Loại người dùng không hợp lệ.");
            hasError = true;
        }

        if (hasError) {
            // Giữ lại các giá trị đã nhập
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("studentId", studentId);
            request.setAttribute("userType", userTypeStr);
            request.setAttribute("schoolYear", schoolYear);
            request.setAttribute("phoneNumber", phoneNumber);
            request.setAttribute("className", className);
            
            request.getRequestDispatcher("/WEB-INF/views/accounts/register.jsp").forward(request, response);
            return;
        }

        // Tạo đối tượng người dùng mới
        CustomUser newUser = new CustomUser();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setUserType(CustomUser.UserType.valueOf(userTypeStr.toUpperCase()));
        newUser.setStudentId(studentId);
        newUser.setSchoolYear(schoolYear);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setClassName(className);
        
        // Lưu mật khẩu dạng text thường, không mã hóa
        newUser.setPassword(password);

        // Lưu người dùng mới
        boolean saveSuccess = userDAO.saveUser(newUser);

        if (saveSuccess) {
            // Chuyển hướng đến trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login?registered=true");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.");
            request.getRequestDispatcher("/WEB-INF/views/accounts/register.jsp").forward(request, response);
        }
    }
} 