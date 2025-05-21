package org.example.hssv1.controller.accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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