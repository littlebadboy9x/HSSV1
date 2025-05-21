package org.example.hssv1.controller;

// import org.example.hssv1.util.DatabaseConnection; // Removed
import org.example.hssv1.util.HibernateUtil; // Added
import org.hibernate.Session; // Added
import org.hibernate.SessionFactory; //Added

import javax.servlet.ServletException; // Changed
import javax.servlet.annotation.WebServlet; // Changed
import javax.servlet.http.HttpServlet; // Changed
import javax.servlet.http.HttpServletRequest; // Changed
import javax.servlet.http.HttpServletResponse; // Changed
import java.io.IOException;
import java.io.PrintWriter;
// import java.sql.Connection; // Removed
// import java.sql.DatabaseMetaData; // Removed
// import java.sql.ResultSet; // Removed
// import java.sql.SQLException; // Removed

/**
 * Servlet kiểm tra kết nối đến cơ sở dữ liệu sử dụng Hibernate
 */
@WebServlet("/test-db-connection")
public class TestDatabaseConnectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // Added serialVersionUID

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Kiểm tra kết nối cơ sở dữ liệu (Hibernate)</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
            out.println("h1 { color: #333; }");
            out.println("p { font-size: 1.1em; }");
            out.println(".success { color: green; font-weight: bold; }");
            out.println(".error { color: red; font-weight: bold; }");
            out.println("pre { background-color: #f0f0f0; padding: 10px; border: 1px solid #ccc; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Kiểm tra kết nối cơ sở dữ liệu (Hibernate)</h1>");

            Session hibernateSession = null;
            try {
                SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
                if (sessionFactory == null) {
                    out.println("<p class='error'>Lỗi: Hibernate SessionFactory chưa được khởi tạo!</p>");
                    out.println("<p>Vui lòng kiểm tra cấu hình Hibernate (HibernateUtil.java và hibernate.cfg.xml).</p>");
                    return; 
                }
                
                hibernateSession = sessionFactory.openSession();
                // Thực hiện một truy vấn đơn giản để xác nhận kết nối
                Object result = hibernateSession.createNativeQuery("SELECT 1", Object.class).uniqueResult();

                if (result != null && result.toString().equals("1")) {
                    out.println("<p class='success'>Kết nối đến cơ sở dữ liệu qua Hibernate thành công!</p>");
                    out.println("<p>(Truy vấn 'SELECT 1' đã được thực thi và trả về kết quả)</p>");
                } else {
                    out.println("<p class='error'>Lỗi kiểm tra kết nối Hibernate.</p>");
                    out.println("<p>Truy vấn 'SELECT 1' không trả về kết quả mong đợi hoặc có lỗi.</p>");
                }

            } catch (Exception e) {
                out.println("<p class='error'>Lỗi trong quá trình kiểm tra kết nối Hibernate:</p>");
                out.println("<pre>");
                e.printStackTrace(out); // In stack trace ra HTML response để debug
                out.println("</pre>");
            } finally {
                if (hibernateSession != null && hibernateSession.isOpen()) {
                    hibernateSession.close();
                    out.println("<p><i>(Hibernate session đã được đóng)</i></p>");
                }
            }

            out.println("</body>");
            out.println("</html>");
        }
    }
} 