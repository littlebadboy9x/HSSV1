package org.example.hssv1.controller.questions;

import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.dao.QuestionCategoryDAO;
import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.model.Major;
import org.example.hssv1.model.CustomUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Controller xử lý chỉnh sửa câu hỏi của người dùng
 */
@WebServlet("/questions/edit")
public class QuestionEditController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    private QuestionCategoryDAO categoryDAO;
    private MajorDAO majorDAO;
    
    @Override
    public void init() {
        questionDAO = new QuestionDAO();
        categoryDAO = new QuestionCategoryDAO();
        majorDAO = new MajorDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra người dùng đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirectUrl=questions/edit");
            return;
        }
        
        String questionIdStr = request.getParameter("id");
        if (questionIdStr == null || questionIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/questions");
            return;
        }
        
        try {
            CustomUser currentUser = (CustomUser) session.getAttribute("user");
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                request.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra xem người dùng hiện tại có phải là người tạo câu hỏi không
            if (question.getUser() == null || !question.getUser().getId().equals(currentUser.getId())) {
                request.setAttribute("errorMessage", "Bạn không có quyền chỉnh sửa câu hỏi này.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra xem câu hỏi đã được trả lời chưa
            if (question.getStatus() == Question.QuestionStatus.ANSWERED || question.getStatus() == Question.QuestionStatus.CLOSED) {
                request.setAttribute("errorMessage", "Câu hỏi đã được trả lời hoặc đã đóng nên không thể chỉnh sửa.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Lấy danh sách danh mục câu hỏi để hiển thị trong form
            List<QuestionCategory> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            
            // Lấy danh sách ngành học để hiển thị trong form
            List<Major> majors = majorDAO.getAllMajors();
            request.setAttribute("majors", majors);
            
            request.setAttribute("question", question);
            request.getRequestDispatcher("/WEB-INF/views/questions/edit.jsp").forward(request, response);
            
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
        
        // Kiểm tra người dùng đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        CustomUser currentUser = (CustomUser) session.getAttribute("user");
        
        // Đọc tham số từ form
        String questionIdStr = request.getParameter("questionId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryIdStr = request.getParameter("categoryId");
        String majorIdStr = request.getParameter("majorId");
        String isAnonymousStr = request.getParameter("isAnonymous");
        boolean isAnonymous = "on".equals(isAnonymousStr) || "true".equals(isAnonymousStr);
        
        // Kiểm tra các trường bắt buộc
        if (questionIdStr == null || questionIdStr.isEmpty() ||
            title == null || title.trim().isEmpty() ||
            content == null || content.trim().isEmpty() ||
            categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ tiêu đề, nội dung và chọn danh mục cho câu hỏi.");
            
            // Giữ lại dữ liệu đã nhập
            request.setAttribute("title", title);
            request.setAttribute("content", content);
            request.setAttribute("categoryId", categoryIdStr);
            request.setAttribute("majorId", majorIdStr);
            request.setAttribute("isAnonymous", isAnonymous);
            
            // Lấy lại danh sách danh mục và ngành học
            List<QuestionCategory> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            
            List<Major> majors = majorDAO.getAllMajors();
            request.setAttribute("majors", majors);
            
            request.getRequestDispatcher("/WEB-INF/views/questions/edit.jsp").forward(request, response);
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
            
            // Kiểm tra xem người dùng hiện tại có phải là người tạo câu hỏi không
            if (question.getUser() == null || !question.getUser().getId().equals(currentUser.getId())) {
                request.setAttribute("errorMessage", "Bạn không có quyền chỉnh sửa câu hỏi này.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra xem câu hỏi đã được trả lời chưa
            if (question.getStatus() == Question.QuestionStatus.ANSWERED || question.getStatus() == Question.QuestionStatus.CLOSED) {
                request.setAttribute("errorMessage", "Câu hỏi đã được trả lời hoặc đã đóng nên không thể chỉnh sửa.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Cập nhật thông tin câu hỏi
            question.setTitle(title.trim());
            question.setContent(content.trim());
            question.setAnonymous(isAnonymous);
            question.setUpdatedAt(new Date());
            
            // Cập nhật danh mục
            try {
                Long categoryId = Long.parseLong(categoryIdStr);
                QuestionCategory category = categoryDAO.findById(categoryId);
                if (category != null) {
                    question.setCategory(category);
                }
            } catch (NumberFormatException e) {
                // Giữ nguyên category cũ nếu ID không hợp lệ
            }
            
            // Cập nhật ngành học (nếu có)
            if (majorIdStr != null && !majorIdStr.trim().isEmpty()) {
                try {
                    Long majorId = Long.parseLong(majorIdStr);
                    Major major = majorDAO.findById(majorId);
                    question.setMajor(major);
                } catch (NumberFormatException e) {
                    // Không cập nhật major nếu ID không hợp lệ
                }
            } else {
                question.setMajor(null);
            }
            
            if (questionDAO.updateQuestion(question)) {
                session.setAttribute("successMessage", "Câu hỏi đã được cập nhật thành công.");
                response.sendRedirect(request.getContextPath() + "/questions/view?id=" + questionId);
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật câu hỏi. Vui lòng thử lại.");
                
                // Lấy lại danh sách danh mục và ngành học
                List<QuestionCategory> categories = categoryDAO.getAllCategories();
                request.setAttribute("categories", categories);
                
                List<Major> majors = majorDAO.getAllMajors();
                request.setAttribute("majors", majors);
                
                request.setAttribute("question", question);
                request.getRequestDispatcher("/WEB-INF/views/questions/edit.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
} 