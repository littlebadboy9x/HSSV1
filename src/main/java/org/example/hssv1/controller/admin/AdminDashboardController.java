package org.example.hssv1.controller.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.dao.UserDAO;
import org.example.hssv1.dao.AnswerDAO;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.Answer;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.example.hssv1.util.HibernateUtil;

/**
 * Controller cho trang Dashboard của Admin
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        questionDAO = new QuestionDAO();
        answerDAO = new AnswerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy số liệu thống kê cho dashboard
        int totalUsers = userDAO.getAllUsers().size();
        int totalQuestions = questionDAO.getAllQuestions().size();
        int totalAnswers = countTotalAnswers();
        int pendingQuestions = questionDAO.findByStatus(Question.QuestionStatus.PENDING).size();
        
        // Gửi dữ liệu đến view
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("totalAnswers", totalAnswers);
        request.setAttribute("pendingQuestions", pendingQuestions);
        
        // Forward đến trang jsp
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
    
    /**
     * Đếm tổng số câu trả lời
     */
    private int countTotalAnswers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(a) FROM Answer a", Long.class);
            Long count = query.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
} 