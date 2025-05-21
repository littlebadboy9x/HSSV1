package org.example.hssv1.dao;

import org.example.hssv1.model.CustomUser;
import org.example.hssv1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.ArrayList;

/**
 * DAO xử lý truy vấn dữ liệu người dùng
 */
public class UserDAO {
    
    /**
     * Lấy người dùng theo email
     */
    public CustomUser findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<CustomUser> query = session.createQuery("FROM CustomUser WHERE email = :email", CustomUser.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy người dùng theo username
     */
    public CustomUser findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<CustomUser> query = session.createQuery("FROM CustomUser WHERE username = :username", CustomUser.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy người dùng theo ID
     */
    public CustomUser findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(CustomUser.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy tất cả người dùng
     */
    public List<CustomUser> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM CustomUser", CustomUser.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Xác thực người dùng
     */
    public CustomUser authenticate(String email, String password) {
        CustomUser user = findByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    /**
     * Xác thực người dùng bằng username
     */
    public CustomUser authenticateByUsername(String username, String password) {
        CustomUser user = findByUsername(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    /**
     * Tạo người dùng mới
     */
    public boolean saveUser(CustomUser user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            session.persist(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    public boolean updateUser(CustomUser user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật mật khẩu người dùng
     */
    public boolean updatePassword(Long userId, String newPassword) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            CustomUser user = session.get(CustomUser.class, userId);
            if (user != null) {
                user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                session.merge(user);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa người dùng
     */
    public boolean deleteUser(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            CustomUser user = session.get(CustomUser.class, id);
            if (user != null) {
                if (user.getAdvisorProfile() != null) {
                    // Option 1: Delete AdvisorProfile as well (if it's owned by CustomUser and cascade is set)
                    // Option 2: Disassociate and leave AdvisorProfile (or delete it separately based on requirements)
                    // For now, let's assume cascade will handle it or it's disassociated if needed.
                    // advisorProfileDAO.deleteProfile(user.getAdvisorProfile().getId()); // If manual deletion needed
                }
                session.remove(user);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra mật khẩu
     */
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    /**
     * Cập nhật thời gian đăng nhập
     */
    public boolean updateLastLogin(Long userId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            CustomUser user = session.get(CustomUser.class, userId);
            if (user != null) {
                java.util.Date now = new java.util.Date();
                user.setLastLogin(now);
                session.merge(user);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
