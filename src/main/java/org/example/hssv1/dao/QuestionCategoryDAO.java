package org.example.hssv1.dao;

import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.ArrayList;

/**
 * Data Access Object cho đối tượng QuestionCategory
 */
public class QuestionCategoryDAO {
    
    public QuestionCategory findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(QuestionCategory.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public QuestionCategory findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<QuestionCategory> query = session.createQuery("FROM QuestionCategory WHERE name = :name", QuestionCategory.class);
            query.setParameter("name", name);
            return (QuestionCategory) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả các loại câu hỏi
     */
    public List<QuestionCategory> getAllCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM QuestionCategory ORDER BY name ASC", QuestionCategory.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean saveCategory(QuestionCategory category) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(category);
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
     * Cập nhật loại câu hỏi
     */
    public boolean updateCategory(QuestionCategory category) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(category);
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
     * Xóa loại câu hỏi
     */
    public boolean deleteCategory(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            QuestionCategory category = session.get(QuestionCategory.class, id);
            if (category != null) {
                 if (category.getQuestions() == null || category.getQuestions().isEmpty()) {
                    session.remove(category);
                    transaction.commit();
                    return true;
                } else {
                    if (transaction != null) transaction.rollback();
                    System.err.println("Cannot delete category: It has associated questions.");
                    return false;
                }
            }
            return false; // Category not found
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}

