package org.example.hssv1.controller.statistics;

import org.example.hssv1.dao.*;
import org.example.hssv1.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Controller xử lý hiển thị thống kê
 */
@WebServlet("/admin/statistics")
public class StatisticsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;
    private DepartmentDAO departmentDAO;
    private MajorDAO majorDAO;
    private QuestionCategoryDAO categoryDAO;
    
    @Override
    public void init() throws ServletException {
        questionDAO = new QuestionDAO();
        answerDAO = new AnswerDAO();
        departmentDAO = new DepartmentDAO();
        majorDAO = new MajorDAO();
        categoryDAO = new QuestionCategoryDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền admin
        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Lấy tham số thống kê
        String type = request.getParameter("type");
        if (type == null) {
            type = "overview"; // Mặc định là tổng quan
        }
        
        // Xử lý các loại thống kê
        switch (type) {
            case "by_department":
                processDepartmentStatistics(request);
                break;
            case "by_major":
                processMajorStatistics(request);
                break;
            case "by_category":
                processCategoryStatistics(request);
                break;
            case "by_status":
                processStatusStatistics(request);
                break;
            default:
                processOverviewStatistics(request);
                break;
        }
        
        request.setAttribute("statisticsType", type);
        request.getRequestDispatcher("/WEB-INF/views/admin/statistics.jsp").forward(request, response);
    }
    
    /**
     * Xử lý thống kê tổng quan
     */
    private void processOverviewStatistics(HttpServletRequest request) {
        // Tổng số câu hỏi
        List<Question> allQuestions = questionDAO.getAllQuestions();
        int totalQuestions = allQuestions.size();
        
        // Số câu hỏi đã trả lời
        long answeredQuestions = allQuestions.stream()
                .filter(q -> "answered".equals(q.getStatus()))
                .count();
        
        // Số câu hỏi đang chờ
        long pendingQuestions = allQuestions.stream()
                .filter(q -> "pending".equals(q.getStatus()))
                .count();
        
        // Tỷ lệ trả lời
        double responseRate = totalQuestions > 0 ? (double) answeredQuestions / totalQuestions * 100 : 0;
        
        // Đặt thuộc tính vào request
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("answeredQuestions", answeredQuestions);
        request.setAttribute("pendingQuestions", pendingQuestions);
        request.setAttribute("responseRate", responseRate);
    }
    
    /**
     * Xử lý thống kê theo khoa
     */
    private void processDepartmentStatistics(HttpServletRequest request) {
        List<Department> departments = departmentDAO.getAllDepartments();
        Map<Department, Integer> departmentStats = new HashMap<>();
        
        for (Department department : departments) {
            // Đếm số câu hỏi liên quan đến các ngành thuộc khoa
            List<Major> majors = majorDAO.getMajorsByDepartmentId(department.getId());
            int count = 0;
            
            for (Major major : majors) {
                List<Question> questions = questionDAO.getQuestionsByMajorId(major.getId());
                count += questions.size();
            }
            
            departmentStats.put(department, count);
        }
        
        request.setAttribute("departmentStats", departmentStats);
    }
    
    /**
     * Xử lý thống kê theo ngành
     */
    private void processMajorStatistics(HttpServletRequest request) {
        List<Major> majors = majorDAO.getAllMajors();
        Map<Major, Integer> majorStats = new HashMap<>();
        
        for (Major major : majors) {
            List<Question> questions = questionDAO.getQuestionsByMajorId(major.getId());
            majorStats.put(major, questions.size());
        }
        
        request.setAttribute("majorStats", majorStats);
    }
    
    /**
     * Xử lý thống kê theo loại câu hỏi
     */
    private void processCategoryStatistics(HttpServletRequest request) {
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        Map<QuestionCategory, Integer> categoryStats = new HashMap<>();
        
        for (QuestionCategory category : categories) {
            List<Question> questions = questionDAO.getQuestionsByCategoryId(category.getId());
            categoryStats.put(category, questions.size());
        }
        
        request.setAttribute("categoryStats", categoryStats);
    }
    
    /**
     * Xử lý thống kê theo trạng thái
     */
    private void processStatusStatistics(HttpServletRequest request) {
        List<Question> answeredQuestions = questionDAO.getQuestionsByStatus("answered");
        List<Question> pendingQuestions = questionDAO.getQuestionsByStatus("pending");
        
        Map<String, Integer> statusStats = new HashMap<>();
        statusStats.put("answered", answeredQuestions.size());
        statusStats.put("pending", pendingQuestions.size());
        
        request.setAttribute("statusStats", statusStats);
    }
} 