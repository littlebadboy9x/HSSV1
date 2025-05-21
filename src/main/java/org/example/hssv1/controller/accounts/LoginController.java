package org.example.hssv1.controller.accounts;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.hssv1.dao.UserDAO;
import org.example.hssv1.model.CustomUser;

import java.io.IOException;


/**
 * Controller xử lý đăng nhập
 */
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    
    @Override
    public void init() {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu người dùng đã đăng nhập, chuyển hướng đến trang chủ
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        
        // Xử lý thông báo đăng ký thành công
        if ("true".equals(request.getParameter("registered"))) {
            request.setAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        }
        
        // Lấy URL chuyển hướng sau khi đăng nhập nếu có
        String redirectUrl = request.getParameter("redirectUrl");
        if (redirectUrl != null) {
            request.setAttribute("redirectUrl", redirectUrl);
        }
        
        request.getRequestDispatcher("/WEB-INF/views/accounts/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // Lấy thông tin đăng nhập
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String redirectUrl = request.getParameter("redirectUrl");
        
        // Kiểm tra dữ liệu
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            request.setAttribute("username", username);
            
            if (redirectUrl != null) {
                request.setAttribute("redirectUrl", redirectUrl);
            }
            
            request.getRequestDispatcher("/WEB-INF/views/accounts/login.jsp").forward(request, response);
            return;
        }
        
        // Tìm người dùng theo username
        CustomUser user = userDAO.findByUsername(username.trim());
        
        if (user == null) {
            // Có thể thông báo "Tên đăng nhập không tồn tại", nhưng có thể gây lỗ hổng bảo mật
            request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác.");
            request.setAttribute("username", username);
            
            if (redirectUrl != null) {
                request.setAttribute("redirectUrl", redirectUrl);
            }
            
            request.getRequestDispatcher("/WEB-INF/views/accounts/login.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra mật khẩu - sử dụng so sánh trực tiếp
        if (!password.equals(user.getPassword())) {
            request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác.");
            request.setAttribute("username", username);
            
            if (redirectUrl != null) {
                request.setAttribute("redirectUrl", redirectUrl);
            }
            
            request.getRequestDispatcher("/WEB-INF/views/accounts/login.jsp").forward(request, response);
            return;
        }
        
        // Tạo session và lưu thông tin người dùng
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        
        // Thiết lập quyền admin dựa trên thuộc tính isSuperuser
        session.setAttribute("isAdmin", user.isSuperuser());
        
        // Cập nhật thời gian đăng nhập
        userDAO.updateLastLogin(user.getId());
        
        // Chuyển hướng sau khi đăng nhập thành công
        if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/" + redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/home?message=login_success");
        }
    }
} 
