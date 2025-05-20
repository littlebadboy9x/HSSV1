package org.example.hssv1.dao;

import org.example.hssv1.model.Department;
import org.example.hssv1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu khoa
 */
public class DepartmentDAO {
    
    /**
     * Lấy khoa theo ID
     */
    public Department findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Department.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy khoa theo tên
     */
    public Department findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Department> query = session.createQuery("FROM Department WHERE name = :name", Department.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy tất cả khoa
     */
    public List<Department> getAllDepartments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Department ORDER BY name ASC", Department.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tạo khoa mới
     */
    public boolean saveDepartment(Department department) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(department);
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
     * Cập nhật thông tin khoa
     */
    public boolean updateDepartment(Department department) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(department);
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
     * Xóa khoa
     */
    public boolean deleteDepartment(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Department department = session.get(Department.class, id);
            if (department != null) {
                if ((department.getMajors() == null || department.getMajors().isEmpty()) &&
                    (department.getAdvisorProfiles() == null || department.getAdvisorProfiles().isEmpty())) {
                    session.remove(department);
                    transaction.commit();
                    return true;
                } else {
                    if (transaction != null) transaction.rollback();
                    System.err.println("Cannot delete department: It has associated majors or advisor profiles.");
                    return false; 
                }
            }
            return false; // Department not found
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
