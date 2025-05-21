package org.example.hssv1.controller.questions;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.hssv1.dao.QuestionDAO;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.Question;

import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý hiển thị danh sách câu hỏi của người dùng
 */
@WebServlet("/questions/my-questions")
public class MyQuestionsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionDAO questionDAO;
    
    @Override
    public void init() {
        questionDAO = new QuestionDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra người dùng đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirectUrl=questions/my-questions");
            return;
        }
        
        // Lấy thông tin người dùng hiện tại
        CustomUser currentUser = (CustomUser) session.getAttribute("user");
        
        // Lấy danh sách câu hỏi của người dùng
        List<Question> userQuestions = questionDAO.findByUser(currentUser);
        request.setAttribute("questions", userQuestions);
        
        // Thông báo thành công nếu có
        String successParam = request.getParameter("success");
        if (successParam != null) {
            switch (successParam) {
                case "create":
                    request.setAttribute("successMessage", "Câu hỏi đã được đăng thành công!");
                    break;
                case "update":
                    request.setAttribute("successMessage", "Cập nhật câu hỏi thành công!");
                    break;
                case "delete":
                    request.setAttribute("successMessage", "Xóa câu hỏi thành công!");
                    break;
                default:
                    break;
            }
        }
        
        // Hiển thị trang danh sách câu hỏi của tôi
        request.getRequestDispatcher("/WEB-INF/views/questions/my-questions.jsp").forward(request, response);
    }
} 
