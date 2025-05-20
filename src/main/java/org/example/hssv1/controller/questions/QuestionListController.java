package org.example.hssv1.controller.questions;

import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.dao.QuestionCategoryDAO;
import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.model.Major;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý hiển thị danh sách câu hỏi
 */
@WebServlet("/questions")
public class QuestionListController extends HttpServlet {
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
        
        // Lấy các tham số lọc
        String categoryId = request.getParameter("category");
        String majorId = request.getParameter("major");
        String status = request.getParameter("status");
        String keyword = request.getParameter("keyword");
        
        List<Question> questions;
        
        // Lọc câu hỏi theo điều kiện
        if (categoryId != null && !categoryId.isEmpty()) {
            questions = questionDAO.getQuestionsByCategoryId(Integer.parseInt(categoryId));
        } else if (majorId != null && !majorId.isEmpty()) {
            questions = questionDAO.getQuestionsByMajorId(Integer.parseInt(majorId));
        } else if (status != null && !status.isEmpty()) {
            questions = questionDAO.getQuestionsByStatus(status);
        } else if (keyword != null && !keyword.isEmpty()) {
            questions = questionDAO.searchQuestions(keyword);
        } else {
            questions = questionDAO.getAllQuestions();
        }
        
        // Lấy danh sách loại câu hỏi và ngành học cho bộ lọc
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        List<Major> majors = majorDAO.getAllMajors();
        
        // Đặt các thuộc tính vào request
        request.setAttribute("questions", questions);
        request.setAttribute("categories", categories);
        request.setAttribute("majors", majors);
        request.setAttribute("selectedCategory", categoryId);
        request.setAttribute("selectedMajor", majorId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("keyword", keyword);
        
        // Forward đến trang danh sách câu hỏi
        request.getRequestDispatcher("/WEB-INF/views/questions/list.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
} 