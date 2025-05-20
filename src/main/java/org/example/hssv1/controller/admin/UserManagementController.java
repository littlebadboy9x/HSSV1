package org.example.hssv1.controller.admin;

import org.example.hssv1.dao.UserDAO;
import org.example.hssv1.dao.AdvisorProfileDAO;
import org.example.hssv1.dao.DepartmentDAO;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.AdvisorProfile;
import org.example.hssv1.model.Department;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Controller xử lý quản lý người dùng
 */
@WebServlet("/admin/users")
public class UserManagementController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private AdvisorProfileDAO advisorProfileDAO;
    private DepartmentDAO departmentDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        advisorProfileDAO = new AdvisorProfileDAO();
        departmentDAO = new DepartmentDAO();
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
        
        // Xử lý các hành động
        String action = request.getParameter("action");
        
        if (action == null) {
            // Hiển thị danh sách người dùng
            listUsers(request, response);
        } else if (action.equals("edit")) {
            // Hiển thị form chỉnh sửa người dùng
            showEditForm(request, response);
        } else if (action.equals("delete")) {
            // Xóa người dùng
            deleteUser(request, response);
        } else if (action.equals("view")) {
            // Xem chi tiết người dùng
            viewUser(request, response);
        } else if (action.equals("create")) {
            // Hiển thị form tạo người dùng mới
            showCreateForm(request, response);
        } else {
            listUsers(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Kiểm tra quyền admin
        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Xử lý các hành động
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else if (action.equals("create")) {
            // Tạo người dùng mới
            createUser(request, response);
        } else if (action.equals("update")) {
            // Cập nhật thông tin người dùng
            updateUser(request, response);
        } else if (action.equals("password")) {
            // Đổi mật khẩu người dùng
            changePassword(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }
    
    /**
     * Hiển thị danh sách người dùng
     */
    private void listUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<CustomUser> users = userDAO.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/views/admin/user-list.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa người dùng
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                CustomUser user = userDAO.getUserById(id);
                
                if (user != null) {
                    request.setAttribute("user", user);
                    
                    // Kiểm tra xem người dùng có phải là cố vấn không
                    AdvisorProfile advisorProfile = advisorProfileDAO.getByUserId(id);
                    request.setAttribute("advisorProfile", advisorProfile);
                    
                    // Lấy danh sách khoa cho form
                    List<Department> departments = departmentDAO.getAllDepartments();
                    request.setAttribute("departments", departments);
                    
                    request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Không làm gì, chuyển hướng về trang danh sách
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    /**
     * Xem chi tiết người dùng
     */
    private void viewUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                CustomUser user = userDAO.getUserById(id);
                
                if (user != null) {
                    request.setAttribute("user", user);
                    
                    // Kiểm tra xem người dùng có phải là cố vấn không
                    AdvisorProfile advisorProfile = advisorProfileDAO.getByUserId(id);
                    request.setAttribute("advisorProfile", advisorProfile);
                    
                    request.getRequestDispatcher("/WEB-INF/views/admin/user-view.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Không làm gì, chuyển hướng về trang danh sách
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    /**
     * Hiển thị form tạo người dùng mới
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy danh sách khoa cho form
        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);
        
        request.getRequestDispatcher("/WEB-INF/views/admin/user-create.jsp").forward(request, response);
    }
    
    /**
     * Tạo người dùng mới
     */
    private void createUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy dữ liệu từ form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String userType = request.getParameter("userType");
        String isAdvisorStr = request.getParameter("isAdvisor");
        
        // Validate dữ liệu
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            userType == null || userType.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showCreateForm(request, response);
            return;
        }
        
        // Kiểm tra username đã tồn tại chưa
        CustomUser existingUser = userDAO.getUserByUsername(username);
        if (existingUser != null) {
            request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
            showCreateForm(request, response);
            return;
        }
        
        // Tạo đối tượng người dùng mới
        CustomUser newUser = new CustomUser();
        newUser.setUsername(username.trim());
        newUser.setPassword(password.trim()); // Trong thực tế nên mã hóa password
        newUser.setEmail(email.trim());
        newUser.setFirstName(firstName.trim());
        newUser.setLastName(lastName.trim());
        newUser.setUserType(userType);
        newUser.setActive(true);
        newUser.setStaff(false);
        newUser.setSuperuser(false);
        newUser.setDateJoined(new Timestamp(System.currentTimeMillis()));
        
        // Lưu người dùng vào database
        int userId = userDAO.createUser(newUser);
        
        if (userId > 0) {
            // Nếu là cố vấn, tạo hồ sơ cố vấn
            boolean isAdvisor = "on".equals(isAdvisorStr);
            
            if (isAdvisor) {
                String departmentIdStr = request.getParameter("departmentId");
                String phone = request.getParameter("phone");
                String bio = request.getParameter("bio");
                
                AdvisorProfile advisorProfile = new AdvisorProfile();
                advisorProfile.setUser(newUser);
                advisorProfile.setPhone(phone);
                advisorProfile.setBio(bio);
                advisorProfile.setAvailable(true);
                
                // Thiết lập khoa nếu có
                if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
                    try {
                        int departmentId = Integer.parseInt(departmentIdStr);
                        Department department = departmentDAO.getDepartmentById(departmentId);
                        advisorProfile.setDepartment(department);
                    } catch (NumberFormatException e) {
                        // Không làm gì, bỏ qua khoa
                    }
                }
                
                advisorProfileDAO.createAdvisorProfile(advisorProfile);
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo người dùng. Vui lòng thử lại.");
            showCreateForm(request, response);
        }
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy dữ liệu từ form
        String idStr = request.getParameter("id");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String userType = request.getParameter("userType");
        String isActiveStr = request.getParameter("isActive");
        
        // Validate dữ liệu
        if (idStr == null || idStr.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            userType == null || userType.trim().isEmpty()) {
            
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            CustomUser user = userDAO.getUserById(id);
            
            if (user != null) {
                // Cập nhật thông tin người dùng
                user.setEmail(email.trim());
                user.setFirstName(firstName.trim());
                user.setLastName(lastName.trim());
                user.setUserType(userType);
                user.setActive("on".equals(isActiveStr));
                
                boolean success = userDAO.updateUser(user);
                
                // Cập nhật thông tin cố vấn nếu có
                String departmentIdStr = request.getParameter("departmentId");
                String phone = request.getParameter("phone");
                String bio = request.getParameter("bio");
                String isAvailableStr = request.getParameter("isAvailable");
                
                AdvisorProfile advisorProfile = advisorProfileDAO.getByUserId(id);
                
                if (advisorProfile != null) {
                    advisorProfile.setPhone(phone);
                    advisorProfile.setBio(bio);
                    advisorProfile.setAvailable("on".equals(isAvailableStr));
                    
                    // Cập nhật khoa nếu có
                    if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
                        try {
                            int departmentId = Integer.parseInt(departmentIdStr);
                            Department department = departmentDAO.getDepartmentById(departmentId);
                            advisorProfile.setDepartment(department);
                        } catch (NumberFormatException e) {
                            // Không làm gì, giữ nguyên khoa cũ
                        }
                    }
                    
                    advisorProfileDAO.updateAdvisorProfile(advisorProfile);
                }
            }
        } catch (NumberFormatException e) {
            // Không làm gì, chuyển hướng về trang danh sách
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    /**
     * Đổi mật khẩu người dùng
     */
    private void changePassword(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy dữ liệu từ form
        String idStr = request.getParameter("id");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate dữ liệu
        if (idStr == null || idStr.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showEditForm(request, response);
            return;
        }
        
        // Kiểm tra mật khẩu xác nhận
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            showEditForm(request, response);
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            boolean success = userDAO.updatePassword(id, password);
            
            if (success) {
                request.setAttribute("successMessage", "Đổi mật khẩu thành công.");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi đổi mật khẩu.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID người dùng không hợp lệ.");
        }
        
        showEditForm(request, response);
    }
    
    /**
     * Xóa người dùng
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                
                // Kiểm tra xem người dùng có phải là cố vấn không
                AdvisorProfile advisorProfile = advisorProfileDAO.getByUserId(id);
                if (advisorProfile != null) {
                    // Xóa hồ sơ cố vấn trước
                    advisorProfileDAO.deleteAdvisorProfile(advisorProfile.getId());
                }
                
                // Xóa người dùng
                userDAO.deleteUser(id);
            } catch (NumberFormatException e) {
                // Không làm gì, chuyển hướng về trang danh sách
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
} 