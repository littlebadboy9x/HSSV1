package org.example.hssv1.controller.questions;

import org.example.hssv1.dao.QuestionCategoryDAO;
import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.model.Major;
import org.example.hssv1.model.CustomUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList; // For handling null DAO results

/**
 * Controller xử lý tạo câu hỏi mới
 */
@WebServlet("/questions/ask")
public class QuestionCreateController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    private QuestionCategoryDAO categoryDAO;
    private MajorDAO majorDAO;
    
    @Override
    public void init() { // Removed throws ServletException
        questionDAO = new QuestionDAO();
        categoryDAO = new QuestionCategoryDAO();
        majorDAO = new MajorDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // Store intended URL for redirect after login
            // session.setAttribute("redirectAfterLogin", request.getRequestURI()); 
            // For simplicity, just set error message now
            if (session != null) { // if session exists but no user
                 session.setAttribute("errorMessage", "Vui lòng đăng nhập để đặt câu hỏi.");
            } else { // if no session, login page will handle it
                 request.setAttribute("errorMessage", "Phiên làm việc hết hạn hoặc bạn chưa đăng nhập.");
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        List<Major> majors = majorDAO.getAllMajors();
        
        request.setAttribute("categories", categories == null ? new ArrayList<>() : categories);
        request.setAttribute("majors", majors == null ? new ArrayList<>() : majors);
        
        request.getRequestDispatcher("/WEB-INF/views/questions/ask.jsp").forward(request, response); // Changed to ask.jsp as per earlier summary
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=session_expired_ask");
            return;
        }
        CustomUser user = (CustomUser) session.getAttribute("user");
        
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryIdStr = request.getParameter("categoryId"); // from ask.jsp
        String majorIdStr = request.getParameter("majorId");     // from ask.jsp
        
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ tiêu đề và nội dung câu hỏi.");
            // Repopulate form fields for better UX
            request.setAttribute("previousTitle", title);
            request.setAttribute("previousContent", content);
            request.setAttribute("selectedCategory", categoryIdStr);
            request.setAttribute("selectedMajor", majorIdStr);
            doGet(request, response); // doGet will repopulate categories/majors
            return;
        }
        
        Question question = new Question();
        question.setUser(user);
        question.setTitle(title.trim());
        question.setContent(content.trim());
        
        if (categoryIdStr != null && !categoryIdStr.isEmpty() && !"0".equals(categoryIdStr)) { // Assuming "0" means no selection
            try {
                Long catId = Long.parseLong(categoryIdStr);
                QuestionCategory category = categoryDAO.findById(catId);
                if (category != null) {
                    question.setCategory(category);
                } else {
                    request.setAttribute("errorMessage", "Loại câu hỏi không hợp lệ.");
                    // Repopulate form fields
                    request.setAttribute("previousTitle", title);
                    request.setAttribute("previousContent", content);
                    request.setAttribute("selectedCategory", categoryIdStr);
                    request.setAttribute("selectedMajor", majorIdStr);
                    doGet(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Định dạng ID loại câu hỏi không hợp lệ.");
                request.setAttribute("previousTitle", title);
                request.setAttribute("previousContent", content);
                request.setAttribute("selectedCategory", categoryIdStr);
                request.setAttribute("selectedMajor", majorIdStr);
                doGet(request, response);
                return;
            }
        }
        
        if (majorIdStr != null && !majorIdStr.isEmpty() && !"0".equals(majorIdStr)) { // Assuming "0" means no selection
            try {
                Long majId = Long.parseLong(majorIdStr);
                Major major = majorDAO.findById(majId);
                if (major != null) {
                    question.setMajor(major);
                } else {
                    request.setAttribute("errorMessage", "Ngành học không hợp lệ.");
                    request.setAttribute("previousTitle", title);
                    request.setAttribute("previousContent", content);
                    request.setAttribute("selectedCategory", categoryIdStr);
                    request.setAttribute("selectedMajor", majorIdStr);
                    doGet(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Định dạng ID ngành học không hợp lệ.");
                request.setAttribute("previousTitle", title);
                request.setAttribute("previousContent", content);
                request.setAttribute("selectedCategory", categoryIdStr);
                request.setAttribute("selectedMajor", majorIdStr);
                doGet(request, response);
                return;
            }
        }
        
        question.setStatus(Question.QuestionStatus.PENDING);
        question.setViewCount(0); // Default view count
        // createdAt and updatedAt are handled by @CreationTimestamp and @UpdateTimestamp
        
        if (questionDAO.saveQuestion(question)) {
            // Assuming saveQuestion now correctly persists and question gets an ID if successful
            // If questionDAO.saveQuestion doesn't update the passed 'question' object with ID,
            // we might need to fetch it again or change saveQuestion to return the saved object or ID.
            // For now, assume 'question' object is updated by Hibernate or we don't need ID for redirect immediately.
            Long savedQuestionId = question.getId(); // This relies on Hibernate setting the ID post-persist
            if (savedQuestionId != null) {
                 response.sendRedirect(request.getContextPath() + "/questions/detail?id=" + savedQuestionId + "&success=asked");
            } else {
                 // Fallback if ID is not set post-save (should not happen with proper Hibernate config)
                 response.sendRedirect(request.getContextPath() + "/questions?success=asked_unknown_id"); 
            }
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo câu hỏi. Vui lòng thử lại.");
            request.setAttribute("previousTitle", title);
            request.setAttribute("previousContent", content);
            request.setAttribute("selectedCategory", categoryIdStr);
            request.setAttribute("selectedMajor", majorIdStr);
            doGet(request, response);
        }
    }
} 