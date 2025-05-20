package org.example.hssv1.dao;

import org.example.hssv1.model.AdvisorProfile;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu cố vấn
 */
public class AdvisorProfileDAO {
    public AdvisorProfileDAO() {
        // Constructor is now empty or can be removed if not needed elsewhere
    }
    
    /**
     * Lấy hồ sơ cố vấn theo ID
     */
    public AdvisorProfile findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(AdvisorProfile.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy hồ sơ cố vấn theo người dùng
     */
    public AdvisorProfile findByUser(CustomUser user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<AdvisorProfile> query = session.createQuery("FROM AdvisorProfile WHERE user = :user", AdvisorProfile.class);
            query.setParameter("user", user);
            return query.uniqueResult();
        }
    }
    
    /**
     * Lấy hồ sơ cố vấn theo ID người dùng
     */
    public AdvisorProfile findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<AdvisorProfile> query = session.createQuery("FROM AdvisorProfile ap JOIN FETCH ap.user WHERE ap.user.id = :userId", AdvisorProfile.class);
            query.setParameter("userId", userId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy tất cả hồ sơ cố vấn
     */
    public List<AdvisorProfile> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Eagerly fetch user and department to avoid N+1 issues if accessed later outside session
            return session.createQuery("SELECT ap FROM AdvisorProfile ap JOIN FETCH ap.user LEFT JOIN FETCH ap.department ORDER BY ap.user.fullName ASC", AdvisorProfile.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy danh sách cố vấn theo khoa
     */
    public List<AdvisorProfile> findByDepartmentId(Long departmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<AdvisorProfile> query = session.createQuery("SELECT ap FROM AdvisorProfile ap JOIN FETCH ap.user LEFT JOIN FETCH ap.department WHERE ap.department.id = :departmentId ORDER BY ap.user.fullName ASC", AdvisorProfile.class);
            query.setParameter("departmentId", departmentId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tạo hồ sơ cố vấn mới
     */
    public boolean saveProfile(AdvisorProfile profile) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(profile);
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
     * Cập nhật hồ sơ cố vấn
     */
    public boolean updateProfile(AdvisorProfile profile) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(profile);
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
     * Xóa hồ sơ cố vấn
     */
    public boolean deleteProfile(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            AdvisorProfile profile = session.get(AdvisorProfile.class, id);
            if (profile != null) {
                CustomUser user = profile.getUser();
                if (user != null) {
                    user.setAdvisorProfile(null); // Disassociate from CustomUser
                    session.merge(user);
                }
                session.remove(profile);
                transaction.commit();
                return true;
            }
            return false; // Profile not found
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
