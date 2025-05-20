package com.hssv.dao;

import com.hssv.model.Question;
import com.hssv.model.QuestionCategory;
import com.hssv.model.Major;
import com.hssv.model.CustomUser;
import com.hssv.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {
    // ... existing code ...

    /**
     * Tạo câu hỏi mới
     */
    public boolean createQuestion(Question question) {
        String sql = "INSERT INTO questions (user_id, title, content, major_id, category_id, status, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, question.getUser().getId());
            stmt.setString(2, question.getTitle());
            stmt.setString(3, question.getContent());
            
            if (question.getMajor() != null) {
                stmt.setInt(4, question.getMajor().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setInt(5, question.getCategory().getId());
            stmt.setString(6, question.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        question.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    // ... existing code ...
} 