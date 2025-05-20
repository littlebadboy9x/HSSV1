package org.example.hssv1.dao;

import org.example.hssv1.model.Major;
import org.example.hssv1.model.Department;
import org.example.hssv1.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu ngành học
 */
public class MajorDAO {
    private DepartmentDAO departmentDAO;
    
    public MajorDAO() {
        departmentDAO = new DepartmentDAO();
    }
    
    /**
     * Lấy ngành học theo ID
     */
    public Major getMajorById(int id) {
        String sql = "SELECT * FROM majors WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMajorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy tất cả ngành học
     */
    public List<Major> getAllMajors() {
        List<Major> majors = new ArrayList<>();
        String sql = "SELECT * FROM majors ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                majors.add(extractMajorFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return majors;
    }
    
    /**
     * Lấy ngành học theo khoa
     */
    public List<Major> getMajorsByDepartmentId(int departmentId) {
        List<Major> majors = new ArrayList<>();
        String sql = "SELECT * FROM majors WHERE department_id = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, departmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    majors.add(extractMajorFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return majors;
    }
    
    /**
     * Tạo ngành học mới
     */
    public int createMajor(Major major) {
        String sql = "INSERT INTO majors (name, description, is_active, department_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, major.getName());
            stmt.setString(2, major.getDescription());
            stmt.setBoolean(3, major.isActive());
            stmt.setInt(4, major.getDepartment() != null ? major.getDepartment().getId() : null);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Cập nhật ngành học
     */
    public boolean updateMajor(Major major) {
        String sql = "UPDATE majors SET name = ?, description = ?, is_active = ?, department_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, major.getName());
            stmt.setString(2, major.getDescription());
            stmt.setBoolean(3, major.isActive());
            stmt.setInt(4, major.getDepartment() != null ? major.getDepartment().getId() : null);
            stmt.setInt(5, major.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa ngành học
     */
    public boolean deleteMajor(int id) {
        String sql = "DELETE FROM majors WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Trích xuất đối tượng Major từ ResultSet
     */
    private Major extractMajorFromResultSet(ResultSet rs) throws SQLException {
        Major major = new Major();
        major.setId(rs.getInt("id"));
        major.setName(rs.getString("name"));
        major.setDescription(rs.getString("description"));
        major.setActive(rs.getBoolean("is_active"));
        
        int departmentId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            Department department = departmentDAO.getDepartmentById(departmentId);
            major.setDepartment(department);
        }
        
        return major;
    }
}
