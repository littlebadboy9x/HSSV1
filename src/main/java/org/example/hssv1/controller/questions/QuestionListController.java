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
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    public void init() {
        questionDAO = new QuestionDAO();
        categoryDAO = new QuestionCategoryDAO();
        majorDAO = new MajorDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String categoryIdStr = request.getParameter("category");
        String majorIdStr = request.getParameter("major");
        String statusStr = request.getParameter("status");
        String keyword = request.getParameter("keyword");
        // TODO: Add parameters for pagination (e.g., page, pageSize) and sorting (e.g., sortBy)
        
        List<Question> questions;

        // Initial fetch based on keyword or all
        if (keyword != null && !keyword.trim().isEmpty()) {
            questions = questionDAO.findByKeyword(keyword.trim());
        } else {
            questions = questionDAO.getAllQuestions();
        }
        if (questions == null) questions = new ArrayList<>(); // Ensure not null

        // Apply filters sequentially
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                Long catId = Long.parseLong(categoryIdStr);
                questions = questions.stream()
                                   .filter(q -> q.getCategory() != null && catId.equals(q.getCategory().getId()))
                                   .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Log or handle invalid categoryId, for now, filter is skipped
                System.err.println("Invalid category ID format: " + categoryIdStr);
            }
        }

        if (majorIdStr != null && !majorIdStr.isEmpty()) {
            try {
                Long majId = Long.parseLong(majorIdStr);
                questions = questions.stream()
                                   .filter(q -> q.getMajor() != null && majId.equals(q.getMajor().getId()))
                                   .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                System.err.println("Invalid major ID format: " + majorIdStr);
            }
        }

        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                Question.QuestionStatus statusEnum = Question.QuestionStatus.valueOf(statusStr.toUpperCase());
                questions = questions.stream()
                                   .filter(q -> statusEnum.equals(q.getStatus()))
                                   .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid status value: " + statusStr);
            }
        }
        
        // TODO: Implement pagination logic here if questions list is large
        // TODO: Implement sorting logic here based on sortBy parameter

        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        List<Major> majors = majorDAO.getAllMajors();
        
        request.setAttribute("questions", questions);
        request.setAttribute("categories", categories == null ? new ArrayList<>() : categories);
        request.setAttribute("majors", majors == null ? new ArrayList<>() : majors);
        request.setAttribute("selectedCategory", categoryIdStr);
        request.setAttribute("selectedMajor", majorIdStr);
        request.setAttribute("selectedStatus", statusStr);
        request.setAttribute("keyword", keyword);
        
        request.getRequestDispatcher("/WEB-INF/views/questions/list.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
} 