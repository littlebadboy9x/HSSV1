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
import java.util.stream.Collectors;

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
    public void init() {
        questionDAO = new QuestionDAO();
        answerDAO = new AnswerDAO();
        departmentDAO = new DepartmentDAO();
        majorDAO = new MajorDAO();
        categoryDAO = new QuestionCategoryDAO();
    }
    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            CustomUser loggedInUser = (CustomUser) session.getAttribute("user");
            if (loggedInUser != null) {
                if (loggedInUser.isSuperuser()) {
                    return true;
                }
                AdvisorProfile profile = loggedInUser.getAdvisorProfile();
                return profile != null && profile.getRole() == AdvisorProfile.AdvisorRole.ADMIN;
            }
        }
        return false;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }
        
        String type = request.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "overview"; 
        } else {
            type = type.trim();
        }
        
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
            case "overview":
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
        List<Question> allQuestions = questionDAO.getAllQuestions();
        int totalQuestions = allQuestions.size();
        
        long answeredCount = allQuestions.stream()
                .filter(q -> q.getStatus() == Question.QuestionStatus.ANSWERED || q.getStatus() == Question.QuestionStatus.CLOSED)
                .count();
        
        long pendingCount = allQuestions.stream()
                .filter(q -> q.getStatus() == Question.QuestionStatus.PENDING)
                .count();
        
        double responseRate = totalQuestions > 0 ? (double) answeredCount / totalQuestions * 100 : 0;
        
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("answeredQuestions", answeredCount);
        request.setAttribute("pendingQuestions", pendingCount);
        request.setAttribute("responseRate", responseRate);
    }
    
    /**
     * Xử lý thống kê theo khoa
     */
    private void processDepartmentStatistics(HttpServletRequest request) {
        List<Department> departments = departmentDAO.getAllDepartments();
        Map<String, Long> departmentStats = new LinkedHashMap<>();
        
        if (departments != null) {
            for (Department department : departments) {
                List<Question> questionsInDept = questionDAO.findByDepartmentId(department.getId());
                departmentStats.put(department.getName(), questionsInDept != null ? (long)questionsInDept.size() : 0L);
            }
        }
        request.setAttribute("departmentStats", departmentStats);
    }
    
    /**
     * Xử lý thống kê theo ngành
     */
    private void processMajorStatistics(HttpServletRequest request) {
        List<Major> majors = majorDAO.getAllMajors();
        Map<String, Long> majorStats = new LinkedHashMap<>();
        
        if (majors != null) {
            for (Major major : majors) {
                List<Question> questionsInMajor = questionDAO.findByMajorId(major.getId());
                majorStats.put(major.getName() + (major.getDepartment()!=null ? " (" +major.getDepartment().getName() + ")" : ""), 
                               questionsInMajor != null ? (long)questionsInMajor.size() : 0L);
            }
        }
        request.setAttribute("majorStats", majorStats);
    }
    
    /**
     * Xử lý thống kê theo loại câu hỏi
     */
    private void processCategoryStatistics(HttpServletRequest request) {
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        Map<String, Long> categoryStats = new LinkedHashMap<>();
        
        if (categories != null) {
            for (QuestionCategory category : categories) {
                List<Question> questionsInCategory = questionDAO.findByCategoryId(category.getId());
                categoryStats.put(category.getName(), questionsInCategory != null ? (long)questionsInCategory.size() : 0L);
            }
        }
        request.setAttribute("categoryStats", categoryStats);
    }
    
    /**
     * Xử lý thống kê theo trạng thái
     */
    private void processStatusStatistics(HttpServletRequest request) {
        Map<String, Long> statusStats = new LinkedHashMap<>();
        
        List<Question> allQuestions = questionDAO.getAllQuestions();

        long answeredCount = allQuestions.stream()
            .filter(q -> q.getStatus() == Question.QuestionStatus.ANSWERED)
            .count();
        statusStats.put("Đã trả lời (Answered)", answeredCount);

        long pendingCount = allQuestions.stream()
            .filter(q -> q.getStatus() == Question.QuestionStatus.PENDING)
            .count();
        statusStats.put("Đang chờ (Pending)", pendingCount);

        long closedCount = allQuestions.stream()
            .filter(q -> q.getStatus() == Question.QuestionStatus.CLOSED)
            .count();
        statusStats.put("Đã đóng (Closed)", closedCount);
        
        request.setAttribute("statusStats", statusStats);
    }
} 