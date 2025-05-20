package com.hssv.dao;

import com.hssv.model.Answer;
import com.hssv.model.Question;
import com.hssv.model.CustomUser;
import com.hssv.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {
    private UserDAO userDAO;
    
    public AnswerDAO() {
        userDAO = new UserDAO();
    }
    
    /**
     * Thêm câu trả lời mới
     */
    public boolean createAnswer(Answer answer) {
        String sql = "INSERT INTO answers (question_id, user_id, content, created_at, updated_at) " +
                    "VALUES (?, ?, ?, NOW(), NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, answer.getQuestion().getId());
            stmt.setInt(2, answer.getUser().getId());
            stmt.setString(3, answer.getContent());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        answer.setId(generatedKeys.getInt(1));
                        
                        // Cập nhật trạng thái câu hỏi thành đã trả lời
                        updateQuestionStatus(answer.getQuestion().getId(), "answered");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lấy danh sách câu trả lời cho một câu hỏi
     */
    public List<Answer> getAnswersByQuestionId(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String sql = "SELECT * FROM answers WHERE question_id = ? ORDER BY created_at ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, questionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Answer answer = extractAnswerFromResultSet(rs);
                    answers.add(answer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return answers;
    }
    
    /**
     * Cập nhật trạng thái câu hỏi
     */
    private void updateQuestionStatus(int questionId, String status) {
        String sql = "UPDATE questions SET status = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, questionId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Trích xuất đối tượng Answer từ ResultSet
     */
    private Answer extractAnswerFromResultSet(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getInt("id"));
        answer.setContent(rs.getString("content"));
        answer.setCreatedAt(rs.getTimestamp("created_at"));
        answer.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Lấy thông tin người dùng
        int userId = rs.getInt("user_id");
        CustomUser user = userDAO.getUserById(userId);
        answer.setUser(user);
        
        return answer;
    }
} 