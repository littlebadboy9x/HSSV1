package org.example.hssv1.util.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebFilter;

/**
 * Filter bảo vệ các tài nguyên admin
 */
public class AdminAuthFilter implements Filter {
    
    public AdminAuthFilter() {
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        // Kiểm tra đăng nhập và quyền admin
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        boolean isAdmin = false;
        
        if (isLoggedIn) {
            isAdmin = session.getAttribute("isAdmin") != null && (boolean) session.getAttribute("isAdmin");
        }
        
        // Nếu chưa đăng nhập hoặc không phải admin, chuyển hướng về trang đăng nhập
        if (!isLoggedIn || !isAdmin) {
            httpRequest.getSession().setAttribute("errorMessage", "Bạn không có quyền truy cập trang này. Vui lòng đăng nhập với tài khoản admin.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        
        // Nếu là admin, cho phép truy cập
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }
} 
