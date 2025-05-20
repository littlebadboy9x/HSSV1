package org.example.hssv1.util.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebFilter;

/**
 * Filter bảo vệ các tài nguyên admin
 */
@WebFilter("/admin/*")
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