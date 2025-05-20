package org.example.hssv1.controller.advisor;

import org.example.hssv1.dao.*;
import org.example.hssv1.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    // AdvisorProfileDAO is not directly used here for fetching data for this specific page logic
    // but might be kept if other advisor-specific info not tied to questions/answers is needed later.
    // For now, it's not used in doGet.
    // private AdvisorProfileDAO advisorProfileDAO; 
    
    @Override
    public void init() {
        questionDAO = new QuestionDAO();
        answerDAO = new AnswerDAO();
        // advisorProfileDAO = new AdvisorProfileDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); // Don't create new session if not exists
        
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        CustomUser user = (CustomUser) session.getAttribute("user");
        AdvisorProfile advisorProfile = (AdvisorProfile) session.getAttribute("advisorProfile");
        
        if (user == null || advisorProfile == null || advisorProfile.getDepartment() == null) {
             // Redirect to login or an error page if essential info is missing
            session.invalidate(); // Clear potentially corrupted session
            response.sendRedirect(request.getContextPath() + "/login?error=session_expired");
            return;
        }
        
        // Lấy các câu hỏi chưa được trả lời trong khoa của cố vấn
        List<Question> unansweredQuestions = questionDAO.findUnansweredByDepartmentId(
            advisorProfile.getDepartment().getId()
        );
        request.setAttribute("unansweredQuestions", unansweredQuestions);
        
        // Lấy các câu hỏi đã được cố vấn trả lời
        List<Question> answeredQuestions = questionDAO.findAnsweredByUserId(user.getId());
        request.setAttribute("answeredQuestions", answeredQuestions);
        
        // Thống kê số lượng câu trả lời của người dùng hiện tại (cố vấn)
        int totalAnswers = answerDAO.countAnswersByUserId(user.getId());
        request.setAttribute("totalAnswers", totalAnswers);
        
        request.setAttribute("advisorName", user.getFullName());
        request.setAttribute("advisorDepartment", advisorProfile.getDepartment().getName());

        // Chuyển đến trang dashboard
        request.getRequestDispatcher("/WEB-INF/views/advisor/dashboard.jsp").forward(request, response);
    }
} 