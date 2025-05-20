package com.hssv.controller.questions;

import com.hssv.dao.QuestionCategoryDAO;
import com.hssv.dao.MajorDAO;
import com.hssv.dao.QuestionDAO;
import com.hssv.model.Question;
import com.hssv.model.QuestionCategory;
import com.hssv.model.Major;
import com.hssv.model.CustomUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/questions/ask")
public class QuestionCreateController extends HttpServlet {
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
        CustomUser user = (CustomUser) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy danh sách loại câu hỏi và ngành học
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        List<Major> majors = majorDAO.getAllMajors();
        
        request.setAttribute("categories", categories);
        request.setAttribute("majors", majors);
        
        request.getRequestDispatcher("/WEB-INF/views/questions/ask.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        CustomUser user = (CustomUser) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy dữ liệu từ form
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryIdStr = request.getParameter("categoryId");
        String majorIdStr = request.getParameter("majorId");
        
        // Validate dữ liệu
        if (title == null || title.trim().isEmpty() || 
            content == null || content.trim().isEmpty() ||
            categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc");
            doGet(request, response);
            return;
        }
        
        try {
            // Tạo đối tượng Question
            Question question = new Question();
            question.setUser(user);
            question.setTitle(title.trim());
            
            // Xử lý nội dung để hiển thị xuống dòng
            content = content.trim().replace("\n", "<br>");
            question.setContent(content);
            
            question.setStatus("pending");
            
            // Set category
            int categoryId = Integer.parseInt(categoryIdStr);
            QuestionCategory category = categoryDAO.getCategoryById(categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Loại câu hỏi không hợp lệ");
            }
            question.setCategory(category);
            
            // Set major nếu có
            if (majorIdStr != null && !majorIdStr.trim().isEmpty()) {
                int majorId = Integer.parseInt(majorIdStr);
                Major major = majorDAO.getMajorById(majorId);
                if (major != null) {
                    question.setMajor(major);
                }
            }
            
            // Lưu câu hỏi
            if (questionDAO.createQuestion(question)) {
                response.sendRedirect(request.getContextPath() + "/questions/" + question.getId());
                return;
            } else {
                throw new Exception("Không thể lưu câu hỏi");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ");
            doGet(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại sau");
            doGet(request, response);
        }
    }
} 