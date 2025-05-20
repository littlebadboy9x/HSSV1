package org.example.hssv1.dao;

import org.example.hssv1.model.Answer;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.Question;
import org.example.hssv1.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho đối tượng Answer
 */
public class AnswerDAO {
    
    private UserDAO userDAO;
    private QuestionDAO questionDAO;
    
    /**
     * Constructor mặc định
     */
    public AnswerDAO() {
        userDAO = new UserDAO();
        questionDAO = new QuestionDAO();
    }
    
    /**
     * Lấy câu trả lời theo ID
     */
    public Answer getAnswerById(int id) {
        String sql = "SELECT * FROM answers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAnswer(rs);
            }
            
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy tất cả câu trả lời cho một câu hỏi
     */
    public List<Answer> getAnswersByQuestionId(int questionId) {
        String sql = "SELECT * FROM answers WHERE question_id = ? ORDER BY created_at ASC";
        List<Answer> answers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                answers.add(mapResultSetToAnswer(rs));
            }
            
            return answers;
        } catch (SQLException e) {
            e.printStackTrace();
            return answers;
        }
    }
    
    /**
     * Tạo câu trả lời mới
     */
    public int createAnswer(Answer answer) {
        String sql = "INSERT INTO answers (question_id, user_id, content) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, answer.getQuestion().getId());
            pstmt.setInt(2, answer.getUser().getId());
            pstmt.setString(3, answer.getContent());
            
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
     * Cập nhật câu trả lời
     */
    public boolean updateAnswer(Answer answer) {
        String sql = "UPDATE answers SET content = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, answer.getContent());
            pstmt.setInt(2, answer.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa câu trả lời
     */
    public boolean deleteAnswer(int id) {
        String sql = "DELETE FROM answers WHERE id = ?";
        
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
     * Chuyển đổi từ ResultSet sang đối tượng Answer
     */
    private Answer mapResultSetToAnswer(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        
        answer.setId(rs.getInt("id"));
        
        // Lấy thông tin câu hỏi
        int questionId = rs.getInt("question_id");
        Question question = questionDAO.getQuestionById(questionId);
        answer.setQuestion(question);
        
        // Lấy thông tin người dùng
        int userId = rs.getInt("user_id");
        CustomUser user = userDAO.getUserById(userId);
        answer.setUser(user);
        
        answer.setContent(rs.getString("content"));
        answer.setCreatedAt(rs.getTimestamp("created_at"));
        answer.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return answer;
    }
    
    /**
     * Đếm số câu trả lời của một cố vấn
     */
    public int countAnswersByAdvisor(int advisorId) {
        String sql = "SELECT COUNT(*) FROM answers WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, advisorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}
