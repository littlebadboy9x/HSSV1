package org.example.hssv1.dao;

import org.example.hssv1.model.Question;
import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.model.Major;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.Answer;
import org.example.hssv1.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu câu hỏi
 */
public class QuestionDAO {
    private UserDAO userDAO;
    private QuestionCategoryDAO categoryDAO;
    private MajorDAO majorDAO;
    
    public QuestionDAO() {
        userDAO = new UserDAO();
        categoryDAO = new QuestionCategoryDAO();
        majorDAO = new MajorDAO();
    }
    
    /**
     * Lấy tất cả câu hỏi
     */
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                questions.add(extractQuestionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    /**
     * Lấy câu hỏi theo ID
     */
    public Question getQuestionById(int id) {
        String sql = "SELECT * FROM questions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Question question = extractQuestionFromResultSet(rs);
                    // Tăng lượt xem
                    incrementViewCount(id);
                    return question;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Tăng lượt xem câu hỏi
     */
    private void incrementViewCount(int questionId) {
        String sql = "UPDATE questions SET view_count = view_count + 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy danh sách câu hỏi theo loại
     */
    public List<Question> getQuestionsByCategoryId(int categoryId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE category_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(extractQuestionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    /**
     * Lấy danh sách câu hỏi theo ngành
     */
    public List<Question> getQuestionsByMajorId(int majorId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE major_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, majorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(extractQuestionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    /**
     * Lấy danh sách câu hỏi theo trạng thái
     */
    public List<Question> getQuestionsByStatus(String status) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(extractQuestionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    /**
     * Tìm kiếm câu hỏi theo từ khóa
     */
    public List<Question> searchQuestions(String keyword) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE title LIKE ? OR content LIKE ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + keyword + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(extractQuestionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    /**
     * Lấy các câu hỏi mới được trả lời
     */
    public List<Question> getRecentAnsweredQuestions(int limit) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT q.* FROM questions q " +
                     "INNER JOIN answers a ON q.id = a.question_id " +
                     "WHERE q.status = 'answered' " +
                     "GROUP BY q.id " +
                     "ORDER BY MAX(a.created_at) DESC " +
                     "LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(extractQuestionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    /**
     * Lấy các câu hỏi phổ biến (nhiều lượt xem)
     */
    public List<Question> getPopularQuestions(int limit) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY view_count DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(extractQuestionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    /**
     * Tạo câu hỏi mới
     */
    public int createQuestion(Question question) {
        String sql = "INSERT INTO questions (user_id, title, content, category_id, status, view_count, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, question.getUser().getId());
            stmt.setString(2, question.getTitle());
            stmt.setString(3, question.getContent());
            stmt.setInt(4, question.getCategory() != null ? question.getCategory().getId() : null);
            stmt.setString(5, question.getStatus());
            stmt.setInt(6, question.getViewCount());
            stmt.setTimestamp(7, question.getCreatedAt());
            stmt.setTimestamp(8, question.getUpdatedAt());
            
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
     * Cập nhật câu hỏi
     */
    public boolean updateQuestion(Question question) {
        String sql = "UPDATE questions SET title = ?, content = ?, category_id = ?, " +
                     "status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, question.getTitle());
            stmt.setString(2, question.getContent());
            stmt.setInt(3, question.getCategory() != null ? question.getCategory().getId() : null);
            stmt.setString(4, question.getStatus());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(6, question.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật trạng thái câu hỏi
     */
    public boolean updateQuestionStatus(int questionId, String status) {
        String sql = "UPDATE questions SET status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, questionId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa câu hỏi
     */
    public boolean deleteQuestion(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        
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
     * Trích xuất đối tượng Question từ ResultSet
     */
    private Question extractQuestionFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("id"));
        question.setTitle(rs.getString("title"));
        question.setContent(rs.getString("content"));
        question.setStatus(rs.getString("status"));
        question.setViewCount(rs.getInt("view_count"));
        question.setCreatedAt(rs.getTimestamp("created_at"));
        question.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Lấy thông tin người dùng
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            CustomUser user = userDAO.getUserById(userId);
            question.setUser(user);
        }
        
        // Lấy thông tin loại câu hỏi
        int categoryId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            QuestionCategory category = categoryDAO.getCategoryById(categoryId);
            question.setCategory(category);
        }
        
        // Lấy thông tin ngành học
        int majorId = rs.getInt("major_id");
        if (!rs.wasNull()) {
            Major major = majorDAO.getMajorById(majorId);
            question.setMajor(major);
        }
        
        return question;
    }
    
    /**
     * Lấy danh sách câu hỏi chưa được trả lời trong một khoa
     */
    public List<Question> getUnansweredQuestionsByDepartment(int departmentId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT DISTINCT q.* FROM questions q " +
                    "INNER JOIN users u ON q.user_id = u.id " +
                    "LEFT JOIN advisor_profiles ap ON u.id = ap.user_id " +
                    "LEFT JOIN answers a ON q.id = a.question_id " +
                    "WHERE (ap.department_id = ? OR q.user_id IN " +
                    "(SELECT user_id FROM advisor_profiles WHERE department_id = ?)) " +
                    "AND a.id IS NULL " +
                    "ORDER BY q.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, departmentId);
            stmt.setInt(2, departmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(extractQuestionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
    
    /**
     * Lấy danh sách câu hỏi đã được một cố vấn trả lời
     */
    public List<Question> getAnsweredQuestionsByAdvisor(int advisorId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT DISTINCT q.* FROM questions q " +
                    "INNER JOIN answers a ON q.id = a.question_id " +
                    "WHERE a.user_id = ? " +
                    "ORDER BY q.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, advisorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(extractQuestionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }
}
