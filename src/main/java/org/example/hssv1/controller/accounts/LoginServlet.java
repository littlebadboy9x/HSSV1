package org.example.hssv1.controller.accounts;

import org.example.hssv1.dao.AdvisorProfileDAO;
import org.example.hssv1.dao.UserDAO;
import org.example.hssv1.model.AdvisorProfile;
import org.example.hssv1.model.CustomUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet xử lý đăng nhập người dùng
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UserDAO userDAO;
    private AdvisorProfileDAO advisorProfileDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        advisorProfileDAO = new AdvisorProfileDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Nếu người dùng đã đăng nhập, chuyển hướng đến trang chủ
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Hiển thị trang đăng nhập
        request.getRequestDispatcher("/WEB-INF/views/accounts/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Đảm bảo sử dụng UTF-8 cho request
        request.setCharacterEncoding("UTF-8");
        
        // Lấy thông tin đăng nhập từ form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        // Xác thực người dùng
        CustomUser user = userDAO.authenticate(username, password);
        
        if (user != null) {
            // Tạo phiên đăng nhập
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            // Kiểm tra xem người dùng có phải là tư vấn viên không
            AdvisorProfile advisorProfile = advisorProfileDAO.getByUserId(user.getId());
            if (advisorProfile != null) {
                session.setAttribute("advisorProfile", advisorProfile);
                session.setAttribute("isAdvisor", true);
                String role = advisorProfileDAO.getRole(advisorProfile.getId());
                session.setAttribute("isAdmin", "admin".equals(role));
            } else {
                session.setAttribute("isAdvisor", false);
                session.setAttribute("isAdmin", false);
            }
            
            // Thiết lập thời gian phiên nếu chọn "Ghi nhớ đăng nhập"
            if (rememberMe != null) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 ngày
            }
            
            // Chuyển hướng dựa vào vai trò người dùng
            if ((Boolean) session.getAttribute("isAdmin")) {
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } else if ((Boolean) session.getAttribute("isAdvisor")) {
                response.sendRedirect(request.getContextPath() + "/advisor/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } else {
            // Đăng nhập thất bại, hiển thị thông báo lỗi
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác");
            request.getRequestDispatcher("/WEB-INF/views/accounts/login.jsp").forward(request, response);
        }
    }
} 