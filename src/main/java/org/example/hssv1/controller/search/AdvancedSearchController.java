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
    public void init() throws ServletException {
        questionDAO = new QuestionDAO();
        categoryDAO = new QuestionCategoryDAO();
        majorDAO = new MajorDAO();
        departmentDAO = new DepartmentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy tham số tìm kiếm
        String keyword = request.getParameter("keyword");
        String categoryId = request.getParameter("category");
        String majorId = request.getParameter("major");
        String departmentId = request.getParameter("department");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");
        
        // Lấy danh sách loại câu hỏi, ngành học và khoa cho bộ lọc
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        List<Major> majors = majorDAO.getAllMajors();
        List<Department> departments = departmentDAO.getAllDepartments();
        
        // Đặt các thuộc tính vào request
        request.setAttribute("categories", categories);
        request.setAttribute("majors", majors);
        request.setAttribute("departments", departments);
        request.setAttribute("selectedCategory", categoryId);
        request.setAttribute("selectedMajor", majorId);
        request.setAttribute("selectedDepartment", departmentId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("keyword", keyword);
        request.setAttribute("sortBy", sortBy);
        
        // Nếu có tham số tìm kiếm, thực hiện tìm kiếm
        if (keyword != null || categoryId != null || majorId != null || 
            departmentId != null || status != null) {
            
            List<Question> searchResults = performSearch(keyword, categoryId, majorId, departmentId, status, sortBy);
            request.setAttribute("searchResults", searchResults);
            request.setAttribute("resultCount", searchResults.size());
        }
        
        // Forward đến trang tìm kiếm
        request.getRequestDispatcher("/WEB-INF/views/search/advanced-search.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        doGet(request, response);
    }
    
    /**
     * Thực hiện tìm kiếm dựa trên các tham số
     */
    private List<Question> performSearch(String keyword, String categoryId, String majorId, 
                                        String departmentId, String status, String sortBy) {
        
        List<Question> results = new ArrayList<>();
        
        // Tìm kiếm theo từ khóa
        if (keyword != null && !keyword.trim().isEmpty()) {
            results = questionDAO.searchQuestions(keyword.trim());
        } else {
            results = questionDAO.getAllQuestions();
        }
        
        // Lọc theo loại câu hỏi
        if (categoryId != null && !categoryId.isEmpty()) {
            try {
                int catId = Integer.parseInt(categoryId);
                results = filterByCategory(results, catId);
            } catch (NumberFormatException e) {
                // Không làm gì, bỏ qua bộ lọc này
            }
        }
        
        // Lọc theo ngành học
        if (majorId != null && !majorId.isEmpty()) {
            try {
                int majId = Integer.parseInt(majorId);
                results = filterByMajor(results, majId);
            } catch (NumberFormatException e) {
                // Không làm gì, bỏ qua bộ lọc này
            }
        }
        
        // Lọc theo khoa
        if (departmentId != null && !departmentId.isEmpty()) {
            try {
                int depId = Integer.parseInt(departmentId);
                results = filterByDepartment(results, depId);
            } catch (NumberFormatException e) {
                // Không làm gì, bỏ qua bộ lọc này
            }
        }
        
        // Lọc theo trạng thái
        if (status != null && !status.isEmpty()) {
            results = filterByStatus(results, status);
        }
        
        // Sắp xếp kết quả
        if (sortBy != null && !sortBy.isEmpty()) {
            sortResults(results, sortBy);
        }
        
        return results;
    }
    
    /**
     * Lọc câu hỏi theo loại
     */
    private List<Question> filterByCategory(List<Question> questions, int categoryId) {
        List<Question> filtered = new ArrayList<>();
        
        for (Question question : questions) {
            if (question.getCategory() != null && question.getCategory().getId() == categoryId) {
                filtered.add(question);
            }
        }
        
        return filtered;
    }
    
    /**
     * Lọc câu hỏi theo ngành
     */
    private List<Question> filterByMajor(List<Question> questions, int majorId) {
        List<Question> filtered = new ArrayList<>();
        
        for (Question question : questions) {
            if (question.getMajor() != null && question.getMajor().getId() == majorId) {
                filtered.add(question);
            }
        }
        
        return filtered;
    }
    
    /**
     * Lọc câu hỏi theo khoa
     */
    private List<Question> filterByDepartment(List<Question> questions, int departmentId) {
        List<Question> filtered = new ArrayList<>();
        
        for (Question question : questions) {
            if (question.getMajor() != null && question.getMajor().getDepartment() != null && 
                question.getMajor().getDepartment().getId() == departmentId) {
                filtered.add(question);
            }
        }
        
        return filtered;
    }
    
    /**
     * Lọc câu hỏi theo trạng thái
     */
    private List<Question> filterByStatus(List<Question> questions, String status) {
        List<Question> filtered = new ArrayList<>();
        
        for (Question question : questions) {
            if (status.equals(question.getStatus())) {
                filtered.add(question);
            }
        }
        
        return filtered;
    }
    
    /**
     * Sắp xếp kết quả
     */
    private void sortResults(List<Question> questions, String sortBy) {
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
                // Mặc định sắp xếp theo thời gian tạo mới nhất
                questions.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
                break;
        }
    }
} 