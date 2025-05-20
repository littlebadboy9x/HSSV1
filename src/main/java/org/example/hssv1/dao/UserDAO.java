package org.example.hssv1.dao;

import org.example.hssv1.model.CustomUser;
import org.example.hssv1.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu người dùng
 */
public class UserDAO {
    
    /**
     * Lấy người dùng theo ID
     */
    public CustomUser getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy người dùng theo tên đăng nhập
     */
    public CustomUser getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Xác thực người dùng
     */
    public CustomUser authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // Trong thực tế nên sử dụng mã hóa password
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CustomUser user = extractUserFromResultSet(rs);
                    updateLastLogin(user.getId());
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cập nhật thời gian đăng nhập cuối
     */
    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy tất cả người dùng
     */
    public List<CustomUser> getAllUsers() {
        List<CustomUser> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Tạo người dùng mới
     */
    public int createUser(CustomUser user) {
        String sql = "INSERT INTO users (username, password, email, first_name, last_name, is_active, " +
                     "is_staff, is_superuser, date_joined, last_login, user_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Trong thực tế nên mã hóa password
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setBoolean(6, user.isActive());
            stmt.setBoolean(7, user.isStaff());
            stmt.setBoolean(8, user.isSuperuser());
            
            if (user.getDateJoined() == null) {
                stmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            } else {
                stmt.setTimestamp(9, user.getDateJoined());
            }
            
            stmt.setTimestamp(10, user.getLastLogin());
            stmt.setString(11, user.getUserType());
            
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
     * Cập nhật thông tin người dùng
     */
    public boolean updateUser(CustomUser user) {
        String sql = "UPDATE users SET username = ?, email = ?, first_name = ?, last_name = ?, " +
                     "is_active = ?, is_staff = ?, is_superuser = ?, user_type = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setBoolean(5, user.isActive());
            stmt.setBoolean(6, user.isStaff());
            stmt.setBoolean(7, user.isSuperuser());
            stmt.setString(8, user.getUserType());
            stmt.setInt(9, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật mật khẩu người dùng
     */
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword); // Trong thực tế nên mã hóa password
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa người dùng
     */
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
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
     * Trích xuất đối tượng User từ ResultSet
     */
    private CustomUser extractUserFromResultSet(ResultSet rs) throws SQLException {
        CustomUser user = new CustomUser();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setActive(rs.getBoolean("is_active"));
        user.setStaff(rs.getBoolean("is_staff"));
        user.setSuperuser(rs.getBoolean("is_superuser"));
        user.setDateJoined(rs.getTimestamp("date_joined"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setUserType(rs.getString("user_type"));
        return user;
    }
}
