package org.example.hssv1.controller;

import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.model.Question;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý trang chủ
 */
@WebServlet("")
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    
    @Override
    public void init() throws ServletException {
        questionDAO = new QuestionDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Lấy 5 câu hỏi mới được trả lời
        List<Question> recentAnsweredQuestions = questionDAO.getRecentAnsweredQuestions(5);
        request.setAttribute("recentAnsweredQuestions", recentAnsweredQuestions);
        
        // Lấy 5 câu hỏi phổ biến nhất
        List<Question> popularQuestions = questionDAO.getPopularQuestions(5);
        request.setAttribute("popularQuestions", popularQuestions);
        
        // Forward đến trang chủ
        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
} 