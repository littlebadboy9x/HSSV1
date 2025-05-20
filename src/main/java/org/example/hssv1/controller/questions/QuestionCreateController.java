package org.example.hssv1.controller.questions;

import org.example.hssv1.dao.QuestionCategoryDAO;
import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.dao.QuestionDAO;
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
import java.sql.Timestamp;
import java.util.List;

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
    public void init() throws ServletException {
        questionDAO = new QuestionDAO();
        categoryDAO = new QuestionCategoryDAO();
        majorDAO = new MajorDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            session.setAttribute("errorMessage", "Vui lòng đăng nhập để đặt câu hỏi.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy danh sách loại câu hỏi và ngành học
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        List<Major> majors = majorDAO.getAllMajors();
        
        request.setAttribute("categories", categories);
        request.setAttribute("majors", majors);
        
        // Forward đến trang tạo câu hỏi
        request.getRequestDispatcher("/WEB-INF/views/questions/create.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        CustomUser user = (CustomUser) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("errorMessage", "Vui lòng đăng nhập để đặt câu hỏi.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy dữ liệu từ form
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryId = request.getParameter("category");
        String majorId = request.getParameter("major");
        
        // Validate dữ liệu
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ tiêu đề và nội dung câu hỏi.");
            doGet(request, response);
            return;
        }
        
        // Tạo đối tượng Question
        Question question = new Question();
        question.setUser(user);
        question.setTitle(title.trim());
        question.setContent(content.trim());
        
        // Thiết lập loại câu hỏi nếu có
        if (categoryId != null && !categoryId.isEmpty()) {
            QuestionCategory category = categoryDAO.getCategoryById(Integer.parseInt(categoryId));
            question.setCategory(category);
        }
        
        // Thiết lập ngành học nếu có
        if (majorId != null && !majorId.isEmpty()) {
            Major major = majorDAO.getMajorById(Integer.parseInt(majorId));
            question.setMajor(major);
        }
        
        // Thiết lập các giá trị mặc định
        question.setStatus("pending");
        question.setViewCount(0);
        question.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        question.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Lưu câu hỏi vào database
        int questionId = questionDAO.createQuestion(question);
        
        if (questionId > 0) {
            // Chuyển hướng đến trang chi tiết câu hỏi
            response.sendRedirect(request.getContextPath() + "/questions/detail?id=" + questionId);
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo câu hỏi. Vui lòng thử lại.");
            doGet(request, response);
        }
    }
} 