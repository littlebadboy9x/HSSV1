package org.example.hssv1.controller.accounts;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet xử lý đăng xuất người dùng
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Remove specific attributes if needed for clean logout before invalidating
            // session.removeAttribute("user");
            // session.removeAttribute("isAdmin");
            // session.removeAttribute("isAdvisor");
            // session.removeAttribute("advisorProfile");
            session.invalidate();
        }
        
        // Redirect to home page with a logout message
        // Consider using a flash attribute if a framework supports it, or a query parameter for simplicity
        response.sendRedirect(request.getContextPath() + "/login?message=logoutSuccess");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
} 