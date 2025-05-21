package org.example.hssv1.controller.admin;

import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.dao.AnswerDAO;
import org.example.hssv1.dao.QuestionCategoryDAO;
import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.dao.AdvisorProfileDAO;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.Answer;
import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.model.Major;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.AdvisorProfile;
import org.example.hssv1.model.Question.QuestionStatus;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Controller xử lý quản lý câu hỏi cho admin và cố vấn
 */
@WebServlet("/admin/questions")
public class QuestionManagementController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;
    private QuestionCategoryDAO categoryDAO;
    private MajorDAO majorDAO;
    private AdvisorProfileDAO advisorProfileDAO;
    
    @Override
    public void init() {
        questionDAO = new QuestionDAO();
        answerDAO = new AnswerDAO();
        categoryDAO = new QuestionCategoryDAO();
        majorDAO = new MajorDAO();
        advisorProfileDAO = new AdvisorProfileDAO();
    }
    
    /**
     * Kiểm tra quyền truy cập cho admin hoặc cố vấn
     */
    private boolean checkPermission(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirectUrl=admin/questions");
            return false;
        }
        
        CustomUser currentUser = (CustomUser) session.getAttribute("user");
        AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(currentUser.getId());
        
        if (advisorProfile == null && !currentUser.isSuperuser()) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }
        
        session.setAttribute("user", currentUser);
        return true;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkPermission(request, response)) {
            return;
        }
        
        HttpSession session = request.getSession();
        CustomUser currentUser = (CustomUser) session.getAttribute("user");
        AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(currentUser.getId());
        
        // Xác định hành động từ tham số
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "view":
                viewQuestion(request, response, currentUser, advisorProfile);
                break;
            case "edit":
                editQuestion(request, response, currentUser, advisorProfile);
                break;
            case "update-status":
                updateQuestionStatus(request, response, currentUser, advisorProfile);
                break;
            case "delete":
                deleteQuestion(request, response, currentUser, advisorProfile);
                break;
            case "list":
            default:
                listQuestions(request, response, currentUser, advisorProfile);
                break;
        }
    }
    
    /**
     * Hiển thị danh sách câu hỏi
     */
    private void listQuestions(HttpServletRequest request, HttpServletResponse response, 
                              CustomUser currentUser, AdvisorProfile advisorProfile)
            throws ServletException, IOException {
        
        // Lọc theo quyền: Admin thấy tất cả, cố vấn chỉ thấy câu hỏi của ngành mình
        List<Question> questions;
        if (currentUser.isSuperuser() || (advisorProfile != null && advisorProfile.getRole() == AdvisorProfile.AdvisorRole.ADMIN)) {
            questions = questionDAO.getAllQuestions();
        } else if (advisorProfile != null) {
            questions = questionDAO.findByDepartmentId(advisorProfile.getDepartment().getId());
        } else {
            questions = new ArrayList<>();
        }
        
        // Lọc theo trạng thái nếu có
        String statusFilter = request.getParameter("status");
        if (statusFilter != null && !statusFilter.isEmpty()) {
            try {
                QuestionStatus status = QuestionStatus.valueOf(statusFilter.toUpperCase());
                List<Question> filteredQuestions = new ArrayList<>();
                for (Question q : questions) {
                    if (q.getStatus() == status) {
                        filteredQuestions.add(q);
                    }
                }
                questions = filteredQuestions;
            } catch (IllegalArgumentException e) {
                // Trạng thái không hợp lệ, không lọc
            }
        }
        
        request.setAttribute("questions", questions);
        request.setAttribute("categories", categoryDAO.getAllCategories());
        request.setAttribute("majors", majorDAO.getAllMajors());
        request.getRequestDispatcher("/WEB-INF/views/admin/question-list.jsp").forward(request, response);
    }
    
    /**
     * Xem chi tiết câu hỏi
     */
    private void viewQuestion(HttpServletRequest request, HttpServletResponse response,
                             CustomUser currentUser, AdvisorProfile advisorProfile)
            throws ServletException, IOException {
        
        String questionIdStr = request.getParameter("id");
        if (questionIdStr == null || questionIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/questions");
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                request.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra quyền xem: Admin xem được tất cả, cố vấn chỉ xem được câu hỏi thuộc ngành mình
            if (!currentUser.isSuperuser() && advisorProfile != null && 
                advisorProfile.getRole() != AdvisorProfile.AdvisorRole.ADMIN && 
                question.getMajor() != null && question.getMajor().getDepartment() != null &&
                !question.getMajor().getDepartment().getId().equals(advisorProfile.getDepartment().getId())) {
                
                request.setAttribute("errorMessage", "Bạn không có quyền xem câu hỏi này.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Lấy danh sách câu trả lời
            List<Answer> answers = answerDAO.findByQuestionId(questionId);
            
            request.setAttribute("question", question);
            request.setAttribute("answers", answers);
            request.getRequestDispatcher("/WEB-INF/views/admin/question-view.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Chỉnh sửa câu hỏi
     */
    private void editQuestion(HttpServletRequest request, HttpServletResponse response,
                             CustomUser currentUser, AdvisorProfile advisorProfile)
            throws ServletException, IOException {
        
        String questionIdStr = request.getParameter("id");
        if (questionIdStr == null || questionIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/questions");
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                request.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra quyền chỉnh sửa: Admin sửa được tất cả, cố vấn chỉ sửa được câu hỏi thuộc ngành mình
            if (!currentUser.isSuperuser() && advisorProfile != null && 
                advisorProfile.getRole() != AdvisorProfile.AdvisorRole.ADMIN && 
                question.getMajor() != null && question.getMajor().getDepartment() != null &&
                !question.getMajor().getDepartment().getId().equals(advisorProfile.getDepartment().getId())) {
                
                request.setAttribute("errorMessage", "Bạn không có quyền chỉnh sửa câu hỏi này.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            request.setAttribute("question", question);
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.setAttribute("majors", majorDAO.getAllMajors());
            request.getRequestDispatcher("/WEB-INF/views/admin/question-edit.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Cập nhật trạng thái câu hỏi
     */
    private void updateQuestionStatus(HttpServletRequest request, HttpServletResponse response,
                                    CustomUser currentUser, AdvisorProfile advisorProfile)
            throws ServletException, IOException {
        
        String questionIdStr = request.getParameter("id");
        String statusStr = request.getParameter("status");
        
        if (questionIdStr == null || statusStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/questions");
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                request.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra quyền cập nhật: Admin cập nhật được tất cả, cố vấn chỉ cập nhật được câu hỏi thuộc ngành mình
            if (!currentUser.isSuperuser() && advisorProfile != null && 
                advisorProfile.getRole() != AdvisorProfile.AdvisorRole.ADMIN && 
                question.getMajor() != null && question.getMajor().getDepartment() != null &&
                !question.getMajor().getDepartment().getId().equals(advisorProfile.getDepartment().getId())) {
                
                request.setAttribute("errorMessage", "Bạn không có quyền cập nhật câu hỏi này.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            QuestionStatus newStatus;
            try {
                newStatus = QuestionStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Trạng thái không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            question.setStatus(newStatus);
            question.setUpdatedAt(new Date());
            
            if (questionDAO.updateQuestion(question)) {
                response.sendRedirect(request.getContextPath() + "/admin/questions");
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật trạng thái câu hỏi.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Xóa câu hỏi
     */
    private void deleteQuestion(HttpServletRequest request, HttpServletResponse response,
                               CustomUser currentUser, AdvisorProfile advisorProfile)
            throws ServletException, IOException {
        
        String questionIdStr = request.getParameter("id");
        
        if (questionIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/questions");
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                request.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra quyền xóa: Chỉ admin mới được xóa câu hỏi
            if (!currentUser.isSuperuser() && (advisorProfile == null || advisorProfile.getRole() != AdvisorProfile.AdvisorRole.ADMIN)) {
                request.setAttribute("errorMessage", "Bạn không có quyền xóa câu hỏi.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            if (questionDAO.deleteQuestion(questionId)) {
                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "Đã xóa câu hỏi thành công.");
                response.sendRedirect(request.getContextPath() + "/admin/questions");
            } else {
                request.setAttribute("errorMessage", "Không thể xóa câu hỏi.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!checkPermission(request, response)) {
            return;
        }
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        
        if ("edit".equals(action)) {
            processEditQuestion(request, response);
        } else if ("answer".equals(action)) {
            processAnswerQuestion(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/questions");
        }
    }
    
    /**
     * Xử lý lưu chỉnh sửa câu hỏi
     */
    private void processEditQuestion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String questionIdStr = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryIdStr = request.getParameter("categoryId");
        String majorIdStr = request.getParameter("majorId");
        
        if (questionIdStr == null || title == null || title.trim().isEmpty() || 
            content == null || content.trim().isEmpty() || categoryIdStr == null) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            if (questionIdStr != null) {
                request.getRequestDispatcher("/admin/questions?action=edit&id=" + questionIdStr).forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/questions");
            }
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                request.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            question.setTitle(title.trim());
            question.setContent(content.trim());
            question.setUpdatedAt(new Date());
            
            // Cập nhật danh mục
            try {
                Long categoryId = Long.parseLong(categoryIdStr);
                QuestionCategory category = categoryDAO.findById(categoryId);
                if (category == null) {
                    request.setAttribute("errorMessage", "Danh mục câu hỏi không tồn tại.");
                    request.getRequestDispatcher("/admin/questions?action=edit&id=" + questionId).forward(request, response);
                    return;
                }
                question.setCategory(category);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID danh mục không hợp lệ.");
                request.getRequestDispatcher("/admin/questions?action=edit&id=" + questionId).forward(request, response);
                return;
            }
            
            // Cập nhật ngành học (nếu có)
            if (majorIdStr != null && !majorIdStr.trim().isEmpty()) {
                try {
                    Long majorId = Long.parseLong(majorIdStr);
                    Major major = majorDAO.findById(majorId);
                    question.setMajor(major);
                } catch (NumberFormatException e) {
                    // Không cập nhật ngành học nếu ID không hợp lệ
                }
            } else {
                question.setMajor(null);
            }
            
            if (questionDAO.updateQuestion(question)) {
                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "Câu hỏi đã được cập nhật thành công.");
                response.sendRedirect(request.getContextPath() + "/admin/questions?action=view&id=" + questionId);
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật câu hỏi.");
                request.getRequestDispatcher("/admin/questions?action=edit&id=" + questionId).forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Xử lý trả lời câu hỏi
     */
    private void processAnswerQuestion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        CustomUser currentUser = (CustomUser) session.getAttribute("user");
        
        String questionIdStr = request.getParameter("questionId");
        String content = request.getParameter("content");
        
        if (questionIdStr == null || content == null || content.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập nội dung câu trả lời.");
            if (questionIdStr != null) {
                request.getRequestDispatcher("/admin/questions?action=view&id=" + questionIdStr).forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/questions");
            }
            return;
        }
        
        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = questionDAO.findById(questionId);
            
            if (question == null) {
                request.setAttribute("errorMessage", "Câu hỏi không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setUser(currentUser);
            answer.setContent(content.trim());
            
            if (answerDAO.saveAnswer(answer)) {
                // Cập nhật trạng thái câu hỏi thành đã trả lời
                question.setStatus(QuestionStatus.ANSWERED);
                questionDAO.updateQuestion(question);
                
                session.setAttribute("successMessage", "Câu trả lời đã được gửi thành công.");
                response.sendRedirect(request.getContextPath() + "/admin/questions?action=view&id=" + questionId);
            } else {
                request.setAttribute("errorMessage", "Không thể lưu câu trả lời.");
                request.getRequestDispatcher("/admin/questions?action=view&id=" + questionId).forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID câu hỏi không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
} 
