package org.example.hssv1.dao;

import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho đối tượng QuestionCategory
 */
public class QuestionCategoryDAO {
    
    /**
     * Lấy tất cả các loại câu hỏi
     */
    public List<QuestionCategory> getAllCategories() {
        String sql = "SELECT * FROM question_categories WHERE is_active = true ORDER BY name";
        List<QuestionCategory> categories = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
            
            return categories;
        } catch (SQLException e) {
            e.printStackTrace();
            return categories;
        }
    }
    
    /**
     * Lấy loại câu hỏi theo ID
     */
    public QuestionCategory getCategoryById(int id) {
        String sql = "SELECT * FROM question_categories WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
            
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tạo loại câu hỏi mới
     */
    public int createCategory(QuestionCategory category) {
        String sql = "INSERT INTO question_categories (name, description, is_active) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setBoolean(3, category.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Cập nhật loại câu hỏi
     */
    public boolean updateCategory(QuestionCategory category) {
        String sql = "UPDATE question_categories SET name = ?, description = ?, is_active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            pstmt.setBoolean(3, category.isActive());
            pstmt.setInt(4, category.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa loại câu hỏi
     */
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM question_categories WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Chuyển đổi từ ResultSet sang đối tượng QuestionCategory
     */
    private QuestionCategory mapResultSetToCategory(ResultSet rs) throws SQLException {
        QuestionCategory category = new QuestionCategory();
        
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setActive(rs.getBoolean("is_active"));
        
        return category;
    }
}
