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
 * Filter bảo vệ các tài nguyên dành cho cố vấn
 */
@WebFilter("/advisor/*")
public class AdvisorAuthFilter implements Filter {
    
    public AdvisorAuthFilter() {
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
        
        // Kiểm tra đăng nhập và quyền cố vấn
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        boolean isAdvisor = false;
        
        if (isLoggedIn) {
            isAdvisor = session.getAttribute("isAdvisor") != null && (boolean) session.getAttribute("isAdvisor");
        }
        
        // Nếu chưa đăng nhập hoặc không phải cố vấn, chuyển hướng về trang đăng nhập
        if (!isLoggedIn || !isAdvisor) {
            httpRequest.getSession().setAttribute("errorMessage", "Bạn không có quyền truy cập trang này. Vui lòng đăng nhập với tài khoản cố vấn.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        
        // Nếu là cố vấn, cho phép truy cập
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }
} 