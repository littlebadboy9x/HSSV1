package org.example.hssv1.dao;

import org.example.hssv1.model.Question;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý truy vấn dữ liệu câu hỏi
 */
public class QuestionDAO {
    public QuestionDAO() {
        // Empty constructor
    }
    
    public Question findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Question.class, id);
        } catch (Exception e) {
            e.printStackTrace(); // Consider proper logging
            return null;
        }
    }

    public boolean saveQuestion(Question question) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(question);
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

    public boolean updateQuestion(Question question) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(question);
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

    public List<Question> getAllQuestions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Question q ORDER BY q.createdAt DESC", Question.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Question> getRecentQuestions(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Question q ORDER BY q.createdAt DESC", Question.class)
                          .setMaxResults(limit)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Question> getRecentAnsweredQuestions(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Question.QuestionStatus> statuses = new ArrayList<>();
            statuses.add(Question.QuestionStatus.ANSWERED);
            statuses.add(Question.QuestionStatus.CLOSED);

            return session.createQuery(
                            "FROM Question q WHERE q.status IN (:statuses) ORDER BY q.updatedAt DESC", Question.class)
                    .setParameterList("statuses", statuses)
                    .setMaxResults(limit)
                    .list();
        } catch (Exception e) {
            e.printStackTrace(); // Consider proper logging
            return new ArrayList<>();
        }
    }

    public List<Question> getPopularQuestions(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Question q ORDER BY q.viewCount DESC, q.createdAt DESC", Question.class)
                          .setMaxResults(limit)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Question> findByKeyword(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Question> query = session.createQuery("FROM Question q WHERE lower(q.title) LIKE lower(:keyword) OR lower(q.content) LIKE lower(:keyword) ORDER BY q.createdAt DESC", Question.class);
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Question> findByUser(CustomUser user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Question> query = session.createQuery("FROM Question q WHERE q.user = :user ORDER BY q.createdAt DESC", Question.class);
            query.setParameter("user", user);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Question> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Question> query = session.createQuery("FROM Question q WHERE q.user.id = :userId ORDER BY q.createdAt DESC", Question.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean deleteQuestion(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Question question = session.get(Question.class, id);
            if (question != null) {
                session.remove(question);
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
    
    public boolean incrementViewCount(Long questionId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Question question = session.get(Question.class, questionId);
            if (question != null) {
                question.setViewCount(question.getViewCount() + 1);
                session.merge(question);
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
    
    public List<Question> findByCategoryId(Long categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Question> query = session.createQuery("FROM Question q WHERE q.category.id = :categoryId ORDER BY q.createdAt DESC", Question.class);
            query.setParameter("categoryId", categoryId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Question> findByMajorId(Long majorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Question> query = session.createQuery("FROM Question q WHERE q.major.id = :majorId ORDER BY q.createdAt DESC", Question.class);
            query.setParameter("majorId", majorId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Question> findByStatus(Question.QuestionStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Question> query = session.createQuery("FROM Question q WHERE q.status = :status ORDER BY q.createdAt DESC", Question.class);
            query.setParameter("status", status);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Question> findByDepartmentId(Long departmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Question> query = session.createQuery("FROM Question q WHERE q.major.department.id = :departmentId ORDER BY q.createdAt DESC", Question.class);
            query.setParameter("departmentId", departmentId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Question> findUnansweredByDepartmentId(Long departmentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Question> query = session.createQuery(
                "FROM Question q WHERE q.major.department.id = :departmentId AND q.status = :status ORDER BY q.createdAt ASC", 
                Question.class
            );
            query.setParameter("departmentId", departmentId);
            query.setParameter("status", Question.QuestionStatus.PENDING);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Question> findAnsweredByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Find questions that have at least one answer from the specified user
            // and the question status is ANSWERED or CLOSED.
            Query<Question> query = session.createQuery(
                "SELECT DISTINCT q FROM Question q JOIN q.answers a WHERE a.user.id = :userId AND q.status IN (:statuses) ORDER BY q.updatedAt DESC", 
                Question.class
            );
            query.setParameter("userId", userId);
            List<Question.QuestionStatus> statuses = new ArrayList<>();
            statuses.add(Question.QuestionStatus.ANSWERED);
            statuses.add(Question.QuestionStatus.CLOSED);
            query.setParameterList("statuses", statuses);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
