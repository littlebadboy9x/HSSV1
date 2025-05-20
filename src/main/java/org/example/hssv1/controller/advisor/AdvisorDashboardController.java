package org.example.hssv1.controller.advisor;

import org.example.hssv1.dao.*;
import org.example.hssv1.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý trang dashboard cho cố vấn
 */
@WebServlet("/advisor/dashboard")
public class AdvisorDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;
    private AdvisorProfileDAO advisorProfileDAO;
    
    @Override
    public void init() throws ServletException {
        questionDAO = new QuestionDAO();
        answerDAO = new AnswerDAO();
        advisorProfileDAO = new AdvisorProfileDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        CustomUser user = (CustomUser) session.getAttribute("user");
        AdvisorProfile advisorProfile = (AdvisorProfile) session.getAttribute("advisorProfile");
        
        if (user == null || advisorProfile == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy các câu hỏi chưa được trả lời trong khoa của cố vấn
        List<Question> unansweredQuestions = questionDAO.getUnansweredQuestionsByDepartment(
            advisorProfile.getDepartment().getId()
        );
        request.setAttribute("unansweredQuestions", unansweredQuestions);
        
        // Lấy các câu hỏi đã được cố vấn trả lời
        List<Question> answeredQuestions = questionDAO.getAnsweredQuestionsByAdvisor(user.getId());
        request.setAttribute("answeredQuestions", answeredQuestions);
        
        // Thống kê số lượng câu trả lời
        int totalAnswers = answerDAO.countAnswersByAdvisor(user.getId());
        request.setAttribute("totalAnswers", totalAnswers);
        
        // Chuyển đến trang dashboard
        request.getRequestDispatcher("/WEB-INF/views/advisor/dashboard.jsp").forward(request, response);
    }
} 