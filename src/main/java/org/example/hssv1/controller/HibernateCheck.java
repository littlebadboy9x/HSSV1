package org.example.hssv1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.hibernate.Version;
import org.example.hssv1.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/hibernate-check")
public class HibernateCheck extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Kiểm tra Hibernate</title>");
            out.println("<style>body { font-family: Arial; margin: 20px; }</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Thông tin Hibernate</h1>");
            
            try {
                // Kiểm tra phiên bản Hibernate
                out.println("<p>Phiên bản Hibernate: " + Version.getVersionString() + "</p>");
                
                // Kiểm tra xem có phương thức applySettings trong StandardServiceRegistryBuilder không
                Class<?> clazz = StandardServiceRegistryBuilder.class;
                try {
                    clazz.getMethod("applySettings", java.util.Map.class);
                    out.println("<p>Phương thức applySettings(Map) tồn tại</p>");
                } catch (NoSuchMethodException e) {
                    out.println("<p>Phương thức applySettings(Map) không tồn tại</p>");
                }
                
                // Thử lấy SessionFactory từ HibernateUtil
                try {
                    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                    if (sessionFactory != null) {
                        out.println("<p>SessionFactory được tạo thành công</p>");
                    } else {
                        out.println("<p>SessionFactory là null</p>");
                    }
                } catch (Exception e) {
                    out.println("<p>Lỗi khi tạo SessionFactory: " + e.getMessage() + "</p>");
                    out.println("<pre>");
                    e.printStackTrace(out);
                    out.println("</pre>");
                }
                
                // Hiển thị các jar file trong classpath
                out.println("<h2>Classpath JAR files</h2>");
                out.println("<ul>");
                String classpath = System.getProperty("java.class.path");
                String[] classpathEntries = classpath.split(System.getProperty("path.separator"));
                for (String path : classpathEntries) {
                    if (path.toLowerCase().contains("hibernate")) {
                        out.println("<li>" + path + "</li>");
                    }
                }
                out.println("</ul>");
                
            } catch (Exception e) {
                out.println("<p>Lỗi: " + e.getMessage() + "</p>");
                out.println("<pre>");
                e.printStackTrace(out);
                out.println("</pre>");
            }
            
            out.println("</body>");
            out.println("</html>");
        }
    }
} 