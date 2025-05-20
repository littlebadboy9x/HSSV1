package org.example.hssv1.dao;

import org.example.hssv1.model.Answer;
import org.example.hssv1.model.Question;
import org.example.hssv1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho đối tượng Answer
 */
public class AnswerDAO {
    
    /**
     * Constructor mặc định
     */
    public AnswerDAO() {
        // Empty constructor
    }
    
    /**
     * Lấy câu trả lời theo ID
     */
    public Answer findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Answer.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy tất cả câu trả lời cho một câu hỏi
     */
    public List<Answer> findByQuestionId(Long questionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Answer> query = session.createQuery("FROM Answer a WHERE a.question.id = :questionId ORDER BY a.createdAt ASC", Answer.class);
            query.setParameter("questionId", questionId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Tạo câu trả lời mới
     */
    public boolean saveAnswer(Answer answer) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Question question = answer.getQuestion();
            if (question != null && question.getId() != null) {
                 Question managedQuestion = session.get(Question.class, question.getId());
                 if (managedQuestion != null) {
                    answer.setQuestion(managedQuestion); // Ensure answer is associated with the managed question
                    managedQuestion.setStatus(Question.QuestionStatus.ANSWERED);
                    session.merge(managedQuestion); 
                 } else {
                    System.err.println("Attempted to save answer for non-existent question ID: " + question.getId());
                    if(transaction != null) transaction.rollback();
                    return false;
                 }
            }

            session.persist(answer);
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
     * Cập nhật câu trả lời
     */
    public boolean updateAnswer(Answer answer) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(answer);
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
     * Xóa câu trả lời
     */
    public boolean deleteAnswer(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Answer answer = session.get(Answer.class, id);
            if (answer != null) {
                session.remove(answer);
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
     * Đếm số câu trả lời của một người dùng (cố vấn)
     */
    public int countAnswersByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(a) FROM Answer a WHERE a.user.id = :userId", Long.class);
            query.setParameter("userId", userId);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Answer> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Answer> query = session.createQuery("FROM Answer a WHERE a.user.id = :userId ORDER BY a.createdAt DESC", Answer.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
