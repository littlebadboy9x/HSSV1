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
 * Controller xử lý hiển thị chi tiết câu hỏi và các câu trả lời
 */
@WebServlet("/questions/detail")
public class QuestionDetailController extends HttpServlet {
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
            response.sendRedirect(request.getContextPath() + "/questions/list");
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
            
            questionDAO.incrementViewCount(questionId);
            
            List<Answer> answers = answerDAO.findByQuestionId(questionId);
            
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

        if (session == null || session.getAttribute("user") == null) {
            session = request.getSession();
            session.setAttribute("errorMessage", "Vui lòng đăng nhập để trả lời câu hỏi.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomUser user = (CustomUser) session.getAttribute("user");
        AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(user.getId());

        if (advisorProfile == null) {
            session.setAttribute("errorMessage", "Chỉ cố vấn hoặc quản trị viên mới có thể trả lời câu hỏi.");
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/questions/list");
            }
            return;
        }

        String questionIdStr = request.getParameter("questionId");
        String content = request.getParameter("content");
        
        if (questionIdStr == null || questionIdStr.isEmpty() || content == null || content.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập nội dung câu trả lời.");
            try {
                Long qId = Long.parseLong(questionIdStr);
                Question question = questionDAO.findById(qId);
                List<Answer> answers = answerDAO.findByQuestionId(qId);
                request.setAttribute("question", question);
                request.setAttribute("answers", answers);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID câu hỏi không hợp lệ.");
                return;
            }
            request.getRequestDispatcher("/WEB-INF/views/questions/detail.jsp").forward(request, response);
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                session.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                response.sendRedirect(request.getContextPath() + "/questions/list");
                return;
            }
            
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setUser(user);
            answer.setContent(content.trim());
            
            if (answerDAO.saveAnswer(answer)) {
                question.setStatus(Question.QuestionStatus.ANSWERED);
                questionDAO.updateQuestion(question);
                
                session.setAttribute("successMessage", "Câu trả lời đã được gửi thành công.");
                response.sendRedirect(request.getContextPath() + "/questions/detail?id=" + questionId);
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi lưu câu trả lời. Vui lòng thử lại.");
                List<Answer> answers = answerDAO.findByQuestionId(questionId);
                request.setAttribute("question", question);
                request.setAttribute("answers", answers);
                request.getRequestDispatcher("/WEB-INF/views/questions/detail.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/questions/list");
        }
    }
} 