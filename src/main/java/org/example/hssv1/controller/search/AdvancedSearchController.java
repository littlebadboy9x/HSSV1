package org.example.hssv1.controller.search;

import org.example.hssv1.dao.*;
import org.example.hssv1.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller xử lý tìm kiếm nâng cao
 */
@WebServlet("/search")
public class AdvancedSearchController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    private QuestionCategoryDAO categoryDAO;
    private MajorDAO majorDAO;
    private DepartmentDAO departmentDAO;
    
    @Override
    public void init() {
        questionDAO = new QuestionDAO();
        categoryDAO = new QuestionCategoryDAO();
        majorDAO = new MajorDAO();
        departmentDAO = new DepartmentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        String categoryIdStr = request.getParameter("category");
        String majorIdStr = request.getParameter("major");
        String departmentIdStr = request.getParameter("department");
        String statusStr = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");
        
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        List<Major> majors = majorDAO.getAllMajors();
        List<Department> departments = departmentDAO.getAllDepartments();
        
        request.setAttribute("categories", categories);
        request.setAttribute("majors", majors);
        request.setAttribute("departments", departments);
        request.setAttribute("selectedCategory", categoryIdStr);
        request.setAttribute("selectedMajor", majorIdStr);
        request.setAttribute("selectedDepartment", departmentIdStr);
        request.setAttribute("selectedStatus", statusStr);
        request.setAttribute("keyword", keyword);
        request.setAttribute("sortBy", sortBy);
        
        List<Question> searchResults = performSearch(keyword, categoryIdStr, majorIdStr, departmentIdStr, statusStr, sortBy);
        request.setAttribute("searchResults", searchResults);
        request.setAttribute("resultCount", searchResults != null ? searchResults.size() : 0);
        
        request.getRequestDispatcher("/WEB-INF/views/search/advanced-search.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    private List<Question> performSearch(String keyword, String categoryIdStr, String majorIdStr, 
                                        String departmentIdStr, String statusStr, String sortBy) {
        
        List<Question> results;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            results = questionDAO.findByKeyword(keyword.trim());
        } else {
            results = questionDAO.getAllQuestions();
        }
        
        if (results == null) results = new ArrayList<>();

        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                Long catId = Long.parseLong(categoryIdStr);
                results = filterByCategory(results, catId);
            } catch (NumberFormatException e) {
                // Ignore if categoryId is not a valid Long
            }
        }
        
        if (majorIdStr != null && !majorIdStr.isEmpty()) {
            try {
                Long majId = Long.parseLong(majorIdStr);
                results = filterByMajor(results, majId);
            } catch (NumberFormatException e) {
                // Ignore if majorId is not a valid Long
            }
        }
        
        if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
            try {
                Long depId = Long.parseLong(departmentIdStr);
                results = filterByDepartment(results, depId);
            } catch (NumberFormatException e) {
                // Ignore if departmentId is not a valid Long
            }
        }
        
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                Question.QuestionStatus status = Question.QuestionStatus.valueOf(statusStr.toUpperCase());
                results = filterByStatus(results, status);
            } catch (IllegalArgumentException e) {
                // Ignore if status is not a valid enum constant
            }
        }
        
        if (sortBy != null && !sortBy.isEmpty()) {
            sortResults(results, sortBy);
        }
        
        return results;
    }
    
    private List<Question> filterByCategory(List<Question> questions, Long categoryId) {
        return questions.stream()
                .filter(q -> q.getCategory() != null && categoryId.equals(q.getCategory().getId()))
                .collect(Collectors.toList());
    }
    
    private List<Question> filterByMajor(List<Question> questions, Long majorId) {
        return questions.stream()
                .filter(q -> q.getMajor() != null && majorId.equals(q.getMajor().getId()))
                .collect(Collectors.toList());
    }
    
    private List<Question> filterByDepartment(List<Question> questions, Long departmentId) {
        return questions.stream()
                .filter(q -> q.getMajor() != null && q.getMajor().getDepartment() != null && 
                             departmentId.equals(q.getMajor().getDepartment().getId()))
                .collect(Collectors.toList());
    }
    
    private List<Question> filterByStatus(List<Question> questions, Question.QuestionStatus status) {
        return questions.stream()
                .filter(q -> status.equals(q.getStatus()))
                .collect(Collectors.toList());
    }
    
    private void sortResults(List<Question> questions, String sortBy) {
        if (questions == null) return;
        switch (sortBy) {
            case "newest":
                questions.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
                break;
            case "oldest":
                questions.sort((q1, q2) -> q1.getCreatedAt().compareTo(q2.getCreatedAt()));
                break;
            case "popular":
                questions.sort((q1, q2) -> Integer.compare(q2.getViewCount(), q1.getViewCount()));
                break;
            default:
                questions.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
                break;
        }
    }
} 