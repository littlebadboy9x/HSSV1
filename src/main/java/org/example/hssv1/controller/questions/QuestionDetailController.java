package org.example.hssv1.controller.questions;

import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.dao.AnswerDAO;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.Answer;
import org.example.hssv1.model.CustomUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Controller xử lý hiển thị chi tiết câu hỏi và các câu trả lời
 */
@WebServlet("/questions/detail")
public class QuestionDetailController extends HttpServlet {
    private static final long serialVersionUID = 1L;
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
        
        // Lấy ID câu hỏi từ tham số
        String questionIdStr = request.getParameter("id");
        
        if (questionIdStr == null || questionIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/questions");
            return;
        }
        
        try {
            int questionId = Integer.parseInt(questionIdStr);
            Question question = questionDAO.getQuestionById(questionId);
            
            if (question == null) {
                response.sendRedirect(request.getContextPath() + "/questions");
                return;
            }
            
            // Lấy danh sách câu trả lời cho câu hỏi
            List<Answer> answers = answerDAO.getAnswersByQuestionId(questionId);
            
            // Đặt thuộc tính vào request
            request.setAttribute("question", question);
            request.setAttribute("answers", answers);
            
            // Forward đến trang chi tiết câu hỏi
            request.getRequestDispatcher("/WEB-INF/views/questions/detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/questions");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        CustomUser user = (CustomUser) session.getAttribute("user");
        
        if (user == null) {
            session.setAttribute("errorMessage", "Vui lòng đăng nhập để trả lời câu hỏi.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra xem người dùng có phải là cố vấn không
        Boolean isAdvisor = (Boolean) session.getAttribute("isAdvisor");
        if (isAdvisor == null || !isAdvisor) {
            session.setAttribute("errorMessage", "Chỉ cố vấn mới có thể trả lời câu hỏi.");
            response.sendRedirect(request.getHeader("Referer"));
            return;
        }
        
        // Lấy dữ liệu từ form
        String questionIdStr = request.getParameter("questionId");
        String content = request.getParameter("content");
        
        // Validate dữ liệu
        if (questionIdStr == null || questionIdStr.isEmpty() || content == null || content.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập nội dung câu trả lời.");
            doGet(request, response);
            return;
        }
        
        try {
            int questionId = Integer.parseInt(questionIdStr);
            Question question = questionDAO.getQuestionById(questionId);
            
            if (question == null) {
                response.sendRedirect(request.getContextPath() + "/questions");
                return;
            }
            
            // Tạo đối tượng Answer
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setUser(user);
            answer.setContent(content.trim());
            answer.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            answer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Lưu câu trả lời vào database
            int answerId = answerDAO.createAnswer(answer);
            
            if (answerId > 0) {
                // Cập nhật trạng thái câu hỏi thành đã trả lời
                questionDAO.updateQuestionStatus(questionId, "answered");
                
                // Chuyển hướng đến trang chi tiết câu hỏi
                response.sendRedirect(request.getContextPath() + "/questions/detail?id=" + questionId);
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo câu trả lời. Vui lòng thử lại.");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/questions");
        }
    }
} 