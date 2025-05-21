package org.example.hssv1.controller.accounts;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller xử lý đăng xuất
 */
public class LogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy session hiện tại nếu có
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Xóa toàn bộ thuộc tính trong session
            session.invalidate();
        }
        
        // Chuyển hướng về trang chủ với thông báo đăng xuất thành công
        response.sendRedirect(request.getContextPath() + "/?message=logout_success");
    }
} 
