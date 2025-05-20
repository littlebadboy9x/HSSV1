package com.hssv.controller.questions;

import com.hssv.dao.AnswerDAO;
import com.hssv.dao.QuestionDAO;
import com.hssv.model.Answer;
import com.hssv.model.Question;
import com.hssv.model.CustomUser;
import com.hssv.model.AdvisorProfile;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/questions/*/answer")
public class AnswerCreateController extends HttpServlet {
    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;
    
    @Override
    public void init() throws ServletException {
        questionDAO = new QuestionDAO();
        answerDAO = new AnswerDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Kiểm tra đăng nhập và quyền tư vấn viên
        HttpSession session = request.getSession();
        CustomUser user = (CustomUser) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra xem người dùng có phải là tư vấn viên không
        AdvisorProfile advisor = (AdvisorProfile) session.getAttribute("advisor");
        if (advisor == null) {
            response.sendRedirect(request.getContextPath() + "/questions");
            return;
        }
        
        // Lấy ID câu hỏi từ URL
        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        int questionId = Integer.parseInt(pathParts[1]);
        
        // Lấy thông tin câu hỏi
        Question question = questionDAO.getQuestionById(questionId);
        if (question == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        request.setAttribute("question", question);
        request.getRequestDispatcher("/WEB-INF/views/questions/answer.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // Kiểm tra đăng nhập và quyền tư vấn viên
        HttpSession session = request.getSession();
        CustomUser user = (CustomUser) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra xem người dùng có phải là tư vấn viên không
        AdvisorProfile advisor = (AdvisorProfile) session.getAttribute("advisor");
        if (advisor == null) {
            response.sendRedirect(request.getContextPath() + "/questions");
            return;
        }
        
        // Lấy ID câu hỏi từ URL
        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        int questionId = Integer.parseInt(pathParts[1]);
        
        // Lấy thông tin câu hỏi
        Question question = questionDAO.getQuestionById(questionId);
        if (question == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Lấy nội dung trả lời
        String content = request.getParameter("content");
        
        // Validate dữ liệu
        if (content == null || content.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập nội dung trả lời");
            request.setAttribute("question", question);
            request.getRequestDispatcher("/WEB-INF/views/questions/answer.jsp").forward(request, response);
            return;
        }
        
        try {
            // Tạo câu trả lời mới
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setUser(user);
            
            // Xử lý nội dung để hiển thị xuống dòng
            content = content.trim().replace("\n", "<br>");
            answer.setContent(content);
            
            // Lưu câu trả lời
            if (answerDAO.createAnswer(answer)) {
                response.sendRedirect(request.getContextPath() + "/questions/" + questionId);
                return;
            } else {
                throw new Exception("Không thể lưu câu trả lời");
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại sau");
            request.setAttribute("question", question);
            request.getRequestDispatcher("/WEB-INF/views/questions/answer.jsp").forward(request, response);
        }
    }
} 