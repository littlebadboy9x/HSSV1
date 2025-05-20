package org.example.hssv1.dao;

import org.example.hssv1.model.Major;
import org.example.hssv1.model.Department;
import org.example.hssv1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu ngành học
 */
public class MajorDAO {

    public Major findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Major.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Major findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Major> query = session.createQuery("FROM Major WHERE name = :name", Major.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Major> getAllMajors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Major ORDER BY name ASC", Major.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Major> findByDepartmentId(Long departmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Major> query = session.createQuery("FROM Major WHERE department.id = :departmentId ORDER BY name ASC", Major.class);
            query.setParameter("departmentId", departmentId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveMajor(Major major) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(major);
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

    public boolean updateMajor(Major major) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(major);
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

    public boolean deleteMajor(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Major major = session.get(Major.class, id);
            if (major != null) {
                if (major.getQuestions() == null || major.getQuestions().isEmpty()) {
                    session.remove(major);
                    transaction.commit();
                    return true;
                } else {
                    if (transaction != null) transaction.rollback();
                    System.err.println("Cannot delete major: It has associated questions.");
                    return false;
                }
            }
            return false; // Major not found
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
