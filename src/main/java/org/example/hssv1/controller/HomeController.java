package org.example.hssv1.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller xử lý trang chủ
 */
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Trang chủ - Hệ thống Tư vấn Sinh viên</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Hệ thống Tư vấn Sinh viên - Trang chủ</h2>");
            out.println("<p>Servlet HomeController đã hoạt động</p>");
            out.println("<ul>");
            out.println("<li><a href='login'>Đăng nhập</a></li>");
            out.println("<li><a href='register'>Đăng ký</a></li>");
            out.println("</ul>");
            out.println("</body>");
            out.println("</html>");
        }
    }
} 