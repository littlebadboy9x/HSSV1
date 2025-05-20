package org.example.hssv1.controller;

import org.example.hssv1.util.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet kiểm tra kết nối đến cơ sở dữ liệu
 */
@WebServlet("/test-db-connection")
public class TestDatabaseConnectionServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Kiểm tra kết nối cơ sở dữ liệu</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
            out.println("h1 { color: #333; }");
            out.println("table { border-collapse: collapse; width: 100%; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Kiểm tra kết nối cơ sở dữ liệu</h1>");
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                out.println("<p style='color: green; font-weight: bold;'>Kết nối đến cơ sở dữ liệu thành công!</p>");
                
                // Hiển thị thông tin về cơ sở dữ liệu
                DatabaseMetaData metaData = conn.getMetaData();
                out.println("<h2>Thông tin cơ sở dữ liệu:</h2>");
                out.println("<ul>");
                out.println("<li>Database URL: " + metaData.getURL() + "</li>");
                out.println("<li>Database Product Name: " + metaData.getDatabaseProductName() + "</li>");
                out.println("<li>Database Product Version: " + metaData.getDatabaseProductVersion() + "</li>");
                out.println("<li>Driver Name: " + metaData.getDriverName() + "</li>");
                out.println("<li>Driver Version: " + metaData.getDriverVersion() + "</li>");
                out.println("</ul>");
                
                // Liệt kê các bảng trong cơ sở dữ liệu
                out.println("<h2>Danh sách các bảng:</h2>");
                out.println("<table>");
                out.println("<tr><th>Tên bảng</th><th>Loại</th></tr>");
                
                ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    String tableType = tables.getString("TABLE_TYPE");
                    out.println("<tr><td>" + tableName + "</td><td>" + tableType + "</td></tr>");
                }
                out.println("</table>");
                
                // Kiểm tra các cột trong bảng users
                out.println("<h2>Cấu trúc bảng users:</h2>");
                out.println("<table>");
                out.println("<tr><th>Tên cột</th><th>Kiểu dữ liệu</th><th>Kích thước</th><th>Nullable</th></tr>");
                
                ResultSet columns = metaData.getColumns(null, null, "users", "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    String nullable = columns.getInt("NULLABLE") == 0 ? "No" : "Yes";
                    
                    out.println("<tr><td>" + columnName + "</td><td>" + dataType + "</td><td>" + columnSize + "</td><td>" + nullable + "</td></tr>");
                }
                out.println("</table>");
                
            } catch (SQLException e) {
                out.println("<p style='color: red; font-weight: bold;'>Lỗi kết nối đến cơ sở dữ liệu:</p>");
                out.println("<pre>" + e.getMessage() + "</pre>");
                e.printStackTrace();
            }
            
            out.println("</body>");
            out.println("</html>");
        }
    }
} 