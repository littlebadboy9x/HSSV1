package org.example.hssv1.dao;

import org.example.hssv1.model.AdvisorProfile;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.Department;
import org.example.hssv1.model.Major;
import org.example.hssv1.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu cố vấn
 */
public class AdvisorProfileDAO {
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private MajorDAO majorDAO;
    
    public AdvisorProfileDAO() {
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        majorDAO = new MajorDAO();
    }
    
    /**
     * Lấy hồ sơ cố vấn theo ID
     */
    public AdvisorProfile getById(int id) {
        String sql = "SELECT * FROM advisor_profiles WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdvisorProfileFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy hồ sơ cố vấn theo ID người dùng
     */
    public AdvisorProfile getByUserId(int userId) {
        String sql = "SELECT * FROM advisor_profiles WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAdvisorProfileFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy tất cả hồ sơ cố vấn
     */
    public List<AdvisorProfile> getAllAdvisorProfiles() {
        List<AdvisorProfile> advisorProfiles = new ArrayList<>();
        String sql = "SELECT * FROM advisor_profiles";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                advisorProfiles.add(extractAdvisorProfileFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return advisorProfiles;
    }
    
    /**
     * Lấy danh sách cố vấn theo khoa
     */
    public List<AdvisorProfile> getAdvisorsByDepartment(int departmentId) {
        List<AdvisorProfile> advisorProfiles = new ArrayList<>();
        String sql = "SELECT * FROM advisor_profiles WHERE department_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, departmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    advisorProfiles.add(extractAdvisorProfileFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return advisorProfiles;
    }
    
    /**
     * Tạo hồ sơ cố vấn mới
     */
    public int createAdvisorProfile(AdvisorProfile advisorProfile) {
        String sql = "INSERT INTO advisor_profiles (user_id, department_id, major_id, title, bio, expertise, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, advisorProfile.getUser().getId());
            stmt.setInt(2, advisorProfile.getDepartment() != null ? advisorProfile.getDepartment().getId() : null);
            stmt.setInt(3, advisorProfile.getMajor() != null ? advisorProfile.getMajor().getId() : null);
            stmt.setString(4, advisorProfile.getTitle());
            stmt.setString(5, advisorProfile.getBio());
            stmt.setString(6, advisorProfile.getExpertise());
            stmt.setString(7, advisorProfile.getRole());
            
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
     * Cập nhật hồ sơ cố vấn
     */
    public boolean updateAdvisorProfile(AdvisorProfile advisorProfile) {
        String sql = "UPDATE advisor_profiles SET department_id = ?, major_id = ?, title = ?, bio = ?, " +
                     "expertise = ?, role = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, advisorProfile.getDepartment() != null ? advisorProfile.getDepartment().getId() : null);
            stmt.setInt(2, advisorProfile.getMajor() != null ? advisorProfile.getMajor().getId() : null);
            stmt.setString(3, advisorProfile.getTitle());
            stmt.setString(4, advisorProfile.getBio());
            stmt.setString(5, advisorProfile.getExpertise());
            stmt.setString(6, advisorProfile.getRole());
            stmt.setInt(7, advisorProfile.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa hồ sơ cố vấn
     */
    public boolean deleteAdvisorProfile(int id) {
        String sql = "DELETE FROM advisor_profiles WHERE id = ?";
        
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
     * Trích xuất đối tượng AdvisorProfile từ ResultSet
     */
    private AdvisorProfile extractAdvisorProfileFromResultSet(ResultSet rs) throws SQLException {
        AdvisorProfile advisorProfile = new AdvisorProfile();
        advisorProfile.setId(rs.getInt("id"));
        advisorProfile.setTitle(rs.getString("title"));
        advisorProfile.setBio(rs.getString("bio"));
        advisorProfile.setExpertise(rs.getString("expertise"));
        advisorProfile.setRole(rs.getString("role"));
        
        // Lấy thông tin người dùng
        int userId = rs.getInt("user_id");
        CustomUser user = userDAO.getUserById(userId);
        advisorProfile.setUser(user);
        
        // Lấy thông tin khoa
        int departmentId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            Department department = departmentDAO.getDepartmentById(departmentId);
            advisorProfile.setDepartment(department);
        }
        
        // Lấy thông tin ngành học
        int majorId = rs.getInt("major_id");
        if (!rs.wasNull()) {
            Major major = majorDAO.getMajorById(majorId);
            advisorProfile.setMajor(major);
        }
        
        return advisorProfile;
    }
    
    /**
     * Lấy vai trò của cố vấn (admin hoặc advisor)
     */
    public String getRole(int advisorId) {
        String sql = "SELECT role FROM advisor_profiles WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, advisorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "advisor"; // Mặc định là cố vấn thông thường
    }
}
