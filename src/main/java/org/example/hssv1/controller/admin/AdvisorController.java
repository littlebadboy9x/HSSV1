package org.example.hssv1.controller.admin;

import org.example.hssv1.dao.AdvisorProfileDAO;
import org.example.hssv1.dao.UserDAO;
import org.example.hssv1.dao.DepartmentDAO;
import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.model.AdvisorProfile;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.Department;
import org.example.hssv1.model.Major;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller quản lý Cố vấn
 */
@WebServlet("/admin/advisors")
public class AdvisorController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdvisorProfileDAO advisorProfileDAO;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private MajorDAO majorDAO;
    
    @Override
    public void init() {
        advisorProfileDAO = new AdvisorProfileDAO();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        majorDAO = new MajorDAO();
    }
    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            CustomUser loggedInUser = (CustomUser) session.getAttribute("user");
            if (loggedInUser != null) {
                // Check isSuperuser flag first, then AdvisorProfile for ADMIN role
                if (loggedInUser.isSuperuser()) {
                    return true;
                }
                AdvisorProfile profile = loggedInUser.getAdvisorProfile();
                if (profile != null) {
                    return profile.getRole() == AdvisorProfile.AdvisorRole.ADMIN;
                }
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

        String action = request.getParameter("action");
        action = (action == null) ? "list" : action.trim();

        switch (action) {
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteAdvisorAction(request, response);
                break;
            case "create":
                showCreateForm(request, response);
                break;
            case "view":
                viewAdvisor(request, response);
                break;
            case "list":
            default:
                listAdvisors(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null || action.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/advisors");
            return;
        }
        action = action.trim();

        switch (action) {
            case "create":
                createAdvisorAction(request, response);
                break;
            case "update":
                updateAdvisorAction(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/advisors?error=unknown_action");
                break;
        }
    }
    
    /**
     * Hiển thị danh sách cố vấn
     */
    private void listAdvisors(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<AdvisorProfile> advisors = advisorProfileDAO.findAll();
        request.setAttribute("advisors", advisors);
        request.getRequestDispatcher("/WEB-INF/views/admin/advisor-list.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị thông tin chi tiết cố vấn
     */
    private void viewAdvisor(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Long id = Long.parseLong(idStr);
                AdvisorProfile advisor = advisorProfileDAO.findById(id);
                if (advisor != null) {
                    request.setAttribute("advisor", advisor);
                    request.getRequestDispatcher("/WEB-INF/views/admin/advisor-view.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID cố vấn không hợp lệ: " + idStr);
            }
        }
        if (request.getAttribute("errorMessage") == null) {
             request.setAttribute("errorMessage", "Không tìm thấy cố vấn.");
        }
        listAdvisors(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa cố vấn
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Long id = Long.parseLong(idStr);
                AdvisorProfile advisor = advisorProfileDAO.findById(id);
                if (advisor != null) {
                    request.setAttribute("advisor", advisor);
                    List<Department> departments = departmentDAO.getAllDepartments();
                    request.setAttribute("departments", departments);
                    List<Major> majors = majorDAO.getAllMajors();
                    request.setAttribute("majors", majors);
                    request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
                    request.getRequestDispatcher("/WEB-INF/views/admin/advisor-edit.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID cố vấn không hợp lệ: " + idStr);
            }
        }
        if (request.getAttribute("errorMessage") == null) {
             request.setAttribute("errorMessage", "Không tìm thấy cố vấn.");
        }
        listAdvisors(request, response);
    }
    
    /**
     * Hiển thị form tạo cố vấn mới
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<CustomUser> users = userDAO.getAllUsers();
        List<Department> departments = departmentDAO.getAllDepartments();
        List<Major> majors = majorDAO.getAllMajors();
        
        request.setAttribute("users", users);
        request.setAttribute("departments", departments);
        request.setAttribute("majors", majors);
        request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
        
        request.getRequestDispatcher("/WEB-INF/views/admin/advisor-create.jsp").forward(request, response);
    }
    
    /**
     * Tạo cố vấn mới
     */
    private void createAdvisorAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String departmentIdStr = request.getParameter("departmentId");
        String majorIdStr = request.getParameter("majorId");
        String title = request.getParameter("title");
        String bio = request.getParameter("bio");
        String expertise = request.getParameter("expertise");
        String phone = request.getParameter("phone");
        String roleStr = request.getParameter("role");
        boolean available = "on".equals(request.getParameter("available")) || "true".equals(request.getParameter("available"));
        
        // Xác thực dữ liệu
        if (userIdStr == null || userIdStr.trim().isEmpty() ||
            departmentIdStr == null || departmentIdStr.trim().isEmpty() ||
            title == null || title.trim().isEmpty() ||
            roleStr == null || roleStr.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showCreateForm(request, response);
            return;
        }
        
        try {
            Long userId = Long.parseLong(userIdStr);
            Long departmentId = Long.parseLong(departmentIdStr);
            Long majorId = null;
            if (majorIdStr != null && !majorIdStr.trim().isEmpty()) {
                majorId = Long.parseLong(majorIdStr);
            }
            
            // Kiểm tra người dùng tồn tại
            CustomUser user = userDAO.findById(userId);
            if (user == null) {
                request.setAttribute("errorMessage", "Người dùng không tồn tại.");
                showCreateForm(request, response);
                return;
            }
            
            // Kiểm tra người dùng đã có profile cố vấn chưa
            AdvisorProfile existingProfile = advisorProfileDAO.findByUserId(userId);
            if (existingProfile != null) {
                request.setAttribute("errorMessage", "Người dùng đã có hồ sơ cố vấn.");
                showCreateForm(request, response);
                return;
            }
            
            // Kiểm tra khoa tồn tại
            Department department = departmentDAO.findById(departmentId);
            if (department == null) {
                request.setAttribute("errorMessage", "Khoa/Đơn vị không tồn tại.");
                showCreateForm(request, response);
                return;
            }
            
            // Kiểm tra ngành học tồn tại nếu được chọn
            Major major = null;
            if (majorId != null) {
                major = majorDAO.findById(majorId);
                if (major == null) {
                    request.setAttribute("errorMessage", "Ngành học không tồn tại.");
                    showCreateForm(request, response);
                    return;
                }
            }
            
            // Chuyển đổi vai trò
            AdvisorProfile.AdvisorRole role;
            try {
                role = AdvisorProfile.AdvisorRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Vai trò không hợp lệ.");
                showCreateForm(request, response);
                return;
            }
            
            // Tạo đối tượng hồ sơ cố vấn mới
            AdvisorProfile advisorProfile = new AdvisorProfile();
            advisorProfile.setUser(user);
            advisorProfile.setDepartment(department);
            advisorProfile.setMajor(major);
            advisorProfile.setTitle(title.trim());
            advisorProfile.setBio(bio != null ? bio.trim() : "");
            advisorProfile.setExpertise(expertise != null ? expertise.trim() : "");
            advisorProfile.setPhone(phone != null ? phone.trim() : "");
            advisorProfile.setRole(role);
            advisorProfile.setAvailable(available);
            
            // Lưu hồ sơ cố vấn vào CSDL
            boolean saveSuccess = advisorProfileDAO.saveProfile(advisorProfile);
            
            if (saveSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/advisors?message=create_success");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo hồ sơ cố vấn. Vui lòng thử lại.");
                showCreateForm(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID không hợp lệ.");
            showCreateForm(request, response);
        }
    }
    
    /**
     * Cập nhật hồ sơ cố vấn
     */
    private void updateAdvisorAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String departmentIdStr = request.getParameter("departmentId");
        String majorIdStr = request.getParameter("majorId");
        String title = request.getParameter("title");
        String bio = request.getParameter("bio");
        String expertise = request.getParameter("expertise");
        String phone = request.getParameter("phone");
        String roleStr = request.getParameter("role");
        boolean available = "on".equals(request.getParameter("available")) || "true".equals(request.getParameter("available"));
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/advisors?error=missing_id");
            return;
        }
        
        if (departmentIdStr == null || departmentIdStr.trim().isEmpty() ||
            title == null || title.trim().isEmpty() ||
            roleStr == null || roleStr.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showEditForm(request, response);
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            Long departmentId = Long.parseLong(departmentIdStr);
            Long majorId = null;
            if (majorIdStr != null && !majorIdStr.trim().isEmpty()) {
                majorId = Long.parseLong(majorIdStr);
            }
            
            // Kiểm tra hồ sơ cố vấn tồn tại
            AdvisorProfile advisorProfile = advisorProfileDAO.findById(id);
            if (advisorProfile == null) {
                response.sendRedirect(request.getContextPath() + "/admin/advisors?error=advisor_not_found");
                return;
            }
            
            // Kiểm tra khoa tồn tại
            Department department = departmentDAO.findById(departmentId);
            if (department == null) {
                request.setAttribute("errorMessage", "Khoa/Đơn vị không tồn tại.");
                request.setAttribute("advisor", advisorProfile);
                showEditForm(request, response);
                return;
            }
            
            // Kiểm tra ngành học tồn tại nếu được chọn
            Major major = null;
            if (majorId != null) {
                major = majorDAO.findById(majorId);
                if (major == null) {
                    request.setAttribute("errorMessage", "Ngành học không tồn tại.");
                    request.setAttribute("advisor", advisorProfile);
                    showEditForm(request, response);
                    return;
                }
            }
            
            // Chuyển đổi vai trò
            AdvisorProfile.AdvisorRole role;
            try {
                role = AdvisorProfile.AdvisorRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Vai trò không hợp lệ.");
                request.setAttribute("advisor", advisorProfile);
                showEditForm(request, response);
                return;
            }
            
            // Cập nhật thông tin hồ sơ cố vấn
            advisorProfile.setDepartment(department);
            advisorProfile.setMajor(major);
            advisorProfile.setTitle(title.trim());
            advisorProfile.setBio(bio != null ? bio.trim() : "");
            advisorProfile.setExpertise(expertise != null ? expertise.trim() : "");
            advisorProfile.setPhone(phone != null ? phone.trim() : "");
            advisorProfile.setRole(role);
            advisorProfile.setAvailable(available);
            
            // Lưu cập nhật vào CSDL
            boolean updateSuccess = advisorProfileDAO.updateProfile(advisorProfile);
            
            if (updateSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/advisors?message=update_success");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật hồ sơ cố vấn. Vui lòng thử lại.");
                request.setAttribute("advisor", advisorProfile);
                showEditForm(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/advisors?error=invalid_id");
        }
    }
    
    /**
     * Xóa hồ sơ cố vấn
     */
    private void deleteAdvisorAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/advisors?error=missing_id");
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            
            boolean deleteSuccess = advisorProfileDAO.deleteProfile(id);
            
            if (deleteSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/advisors?message=delete_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/advisors?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/advisors?error=invalid_id");
        }
    }
} 