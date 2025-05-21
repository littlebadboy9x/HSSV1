package org.example.hssv1.controller.questions;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.hssv1.dao.QuestionCategoryDAO;
import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.model.Major;

import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý tạo câu hỏi mới
 */
@WebServlet("/questions/ask")
public class AskQuestionController extends HttpServlet {
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
            response.sendRedirect(request.getContextPath() + "/login?redirectUrl=questions/ask");
            return;
        }
        
        // Lấy danh sách danh mục câu hỏi để hiển thị trong form
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);
        
        // Lấy danh sách ngành học để hiển thị trong form
        List<Major> majors = majorDAO.getAllMajors();
        request.setAttribute("majors", majors);
        
        // Hiển thị form đặt câu hỏi
        request.getRequestDispatcher("/WEB-INF/views/questions/ask.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra người dùng đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirectUrl=questions/ask");
            return;
        }
        
        request.setCharacterEncoding("UTF-8");
        
        // Lấy thông tin người dùng hiện tại
        CustomUser currentUser = (CustomUser) session.getAttribute("user");
        
        // Lấy dữ liệu từ form
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryIdStr = request.getParameter("categoryId");
        String majorIdStr = request.getParameter("majorId");
        boolean isAnonymous = "on".equals(request.getParameter("isAnonymous"));
        
        // Kiểm tra dữ liệu
        if (title == null || title.trim().isEmpty() ||
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
            
            request.getRequestDispatcher("/WEB-INF/views/questions/ask.jsp").forward(request, response);
            return;
        }
        
        // Tạo đối tượng Question mới
        Question newQuestion = new Question();
        newQuestion.setTitle(title.trim());
        newQuestion.setContent(content.trim());
        newQuestion.setUser(currentUser);
        newQuestion.setAnonymous(isAnonymous);
        newQuestion.setStatus(Question.QuestionStatus.PENDING);
        newQuestion.setViewCount(0);
        
        // Xử lý category
        try {
            Long categoryId = Long.parseLong(categoryIdStr);
            QuestionCategory category = categoryDAO.findById(categoryId);
            if (category != null) {
                newQuestion.setCategory(category);
            }
        } catch (NumberFormatException e) {
            // Không xử lý, category sẽ là null
        }
        
        // Xử lý major nếu có
        if (majorIdStr != null && !majorIdStr.trim().isEmpty()) {
            try {
                Long majorId = Long.parseLong(majorIdStr);
                Major major = majorDAO.findById(majorId);
                if (major != null) {
                    newQuestion.setMajor(major);
                }
            } catch (NumberFormatException e) {
                // Không xử lý, major sẽ là null
            }
        }
        
        // Lưu câu hỏi
        boolean saveSuccess = questionDAO.saveQuestion(newQuestion);
        
        if (saveSuccess) {
            response.sendRedirect(request.getContextPath() + "/questions/my-questions?success=create");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi lưu câu hỏi. Vui lòng thử lại.");
            
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
            
            request.getRequestDispatcher("/WEB-INF/views/questions/ask.jsp").forward(request, response);
        }
    }
} 
