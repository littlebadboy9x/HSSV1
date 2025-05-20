package org.example.hssv1.controller.accounts;

import org.example.hssv1.dao.AdvisorProfileDAO;
import org.example.hssv1.dao.UserDAO;
import org.example.hssv1.model.AdvisorProfile;
import org.example.hssv1.model.CustomUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    public void init() {
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
        CustomUser user = userDAO.authenticateByUsername(username, password);
        
        if (user != null) {
            // Tạo phiên đăng nhập
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            boolean sessionIsAdmin = user.isSuperuser();
            boolean sessionIsAdvisor = false;
            
            AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(user.getId());
            if (advisorProfile != null) {
                session.setAttribute("advisorProfile", advisorProfile);
                sessionIsAdvisor = true;
                if (advisorProfile.getRole() == AdvisorProfile.AdvisorRole.ADMIN) {
                    sessionIsAdmin = true;
                }
            } else {
                session.removeAttribute("advisorProfile");
            }
            
            session.setAttribute("isAdmin", sessionIsAdmin);
            session.setAttribute("isAdvisor", sessionIsAdvisor);
            
            // Thiết lập thời gian phiên nếu chọn "Ghi nhớ đăng nhập"
            if (rememberMe != null && rememberMe.equalsIgnoreCase("on")) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 ngày
            }
            
            // Chuyển hướng dựa vào vai trò người dùng
            String intendedURL = (String) session.getAttribute("intendedURL");
            if (intendedURL != null && !intendedURL.isEmpty()) {
                session.removeAttribute("intendedURL");
                response.sendRedirect(intendedURL);
            } else if (sessionIsAdmin) {
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } else if (sessionIsAdvisor) {
                response.sendRedirect(request.getContextPath() + "/advisor/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } else {
            // Đăng nhập thất bại, hiển thị thông báo lỗi
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác.");
            request.getRequestDispatcher("/WEB-INF/views/accounts/login.jsp").forward(request, response);
        }
    }
} 