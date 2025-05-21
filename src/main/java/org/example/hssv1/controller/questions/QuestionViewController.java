package org.example.hssv1.controller.questions;

import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.dao.AnswerDAO;
import org.example.hssv1.dao.AdvisorProfileDAO;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.Answer;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.AdvisorProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý hiển thị chi tiết câu hỏi và các câu trả lời (URL pattern: /questions/view)
 */
@WebServlet("/questions/view")
public class QuestionViewController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;
    private AdvisorProfileDAO advisorProfileDAO;
    
    @Override
    public void init() {
        questionDAO = new QuestionDAO();
        answerDAO = new AnswerDAO();
        advisorProfileDAO = new AdvisorProfileDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String questionIdStr = request.getParameter("id");
        
        if (questionIdStr == null || questionIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/questions");
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                request.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Tăng số lượt xem
            questionDAO.incrementViewCount(questionId);
            
            List<Answer> answers = answerDAO.findByQuestionId(questionId);
            
            // Kiểm tra xem người dùng hiện tại có phải là cố vấn hay không
            HttpSession session = request.getSession(false);
            boolean isAdvisor = false;
            boolean isOwner = false;
            
            if (session != null && session.getAttribute("user") != null) {
                CustomUser currentUser = (CustomUser) session.getAttribute("user");
                AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(currentUser.getId());
                isAdvisor = (advisorProfile != null);
                
                // Kiểm tra xem người dùng hiện tại có phải là người đặt câu hỏi không
                if (question.getUser() != null && currentUser.getId().equals(question.getUser().getId())) {
                    isOwner = true;
                }
                
                request.setAttribute("user", currentUser);
            }
            
            request.setAttribute("isAdvisor", isAdvisor);
            request.setAttribute("isOwner", isOwner);
            request.setAttribute("question", question);
            request.setAttribute("answers", answers);
            
            request.getRequestDispatcher("/WEB-INF/views/questions/detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // Debug log
        System.out.println("POST request received to /questions/view");
        
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("No session or user found.");
            session = request.getSession();
            session.setAttribute("errorMessage", "Vui lòng đăng nhập để trả lời câu hỏi.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomUser user = (CustomUser) session.getAttribute("user");
        System.out.println("User found: " + user.getUsername() + " (ID: " + user.getId() + ")");
        
        AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(user.getId());

        if (advisorProfile == null) {
            System.out.println("User is not an advisor");
            session.setAttribute("errorMessage", "Chỉ cố vấn hoặc quản trị viên mới có thể trả lời câu hỏi.");
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/questions");
            }
            return;
        }
        
        System.out.println("User is an advisor with role: " + advisorProfile.getRole());

        // Thử lấy questionId từ parameter trước, nếu không có thì lấy từ URL parameter id
        String questionIdStr = request.getParameter("questionId");
        if (questionIdStr == null || questionIdStr.isEmpty()) {
            questionIdStr = request.getParameter("id");
        }
        
        // Lấy nội dung từ form
        String content = request.getParameter("content");
        
        System.out.println("questionId: " + questionIdStr);
        System.out.println("content length: " + (content != null ? content.length() : "null"));
        
        // Log thêm thông tin để debug
        System.out.println("All request parameters:");
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            System.out.println(paramName + ": " + request.getParameter(paramName));
        }
        
        if (questionIdStr == null || questionIdStr.isEmpty()) {
            System.out.println("Missing questionId parameter");
            session.setAttribute("errorMessage", "Thiếu tham số ID câu hỏi");
            response.sendRedirect(request.getContextPath() + "/questions");
            return;
        }
        
        if (content == null || content.trim().isEmpty()) {
            System.out.println("Missing content parameter");
            session.setAttribute("errorMessage", "Vui lòng nhập nội dung câu trả lời");
            response.sendRedirect(request.getContextPath() + "/questions/view?id=" + questionIdStr);
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                System.out.println("Question not found with ID: " + questionId);
                session.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                response.sendRedirect(request.getContextPath() + "/questions");
                return;
            }
            
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setUser(user);
            answer.setContent(content.trim());
            
            System.out.println("Saving answer for question ID: " + questionId);
            System.out.println("Content to save: " + content.substring(0, Math.min(100, content.length())) + "...");
            
            if (answerDAO.saveAnswer(answer)) {
                System.out.println("Answer saved successfully");
                question.setStatus(Question.QuestionStatus.ANSWERED);
                questionDAO.updateQuestion(question);
                
                session.setAttribute("successMessage", "Câu trả lời đã được gửi thành công.");
                response.sendRedirect(request.getContextPath() + "/questions/view?id=" + questionId);
            } else {
                System.out.println("Failed to save answer");
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi lưu câu trả lời. Vui lòng thử lại.");
                List<Answer> answers = answerDAO.findByQuestionId(questionId);
                request.setAttribute("question", question);
                request.setAttribute("answers", answers);
                request.getRequestDispatcher("/WEB-INF/views/questions/detail.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid question ID format: " + questionIdStr);
            session.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/questions");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/questions");
        }
    }
} 
