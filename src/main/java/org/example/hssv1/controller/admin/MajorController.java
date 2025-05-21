package org.example.hssv1.controller.admin;

import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.dao.DepartmentDAO;
import org.example.hssv1.model.Major;
import org.example.hssv1.model.Department;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.AdvisorProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller quản lý ngành học
 */
@WebServlet("/admin/majors")
public class MajorController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MajorDAO majorDAO;
    private DepartmentDAO departmentDAO;
    
    @Override
    public void init() {
        majorDAO = new MajorDAO();
        departmentDAO = new DepartmentDAO();
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
                deleteMajorAction(request, response);
                break;
            case "create":
                showCreateForm(request, response);
                break;
            case "list":
            default:
                listMajors(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admin/majors");
            return;
        }
        action = action.trim();

        switch (action) {
            case "create":
                createMajorAction(request, response);
                break;
            case "update":
                updateMajorAction(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/majors?error=unknown_action");
                break;
        }
    }
    
    /**
     * Hiển thị danh sách ngành học
     */
    private void listMajors(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Major> majors = majorDAO.getAllMajors();
        request.setAttribute("majors", majors);
        request.getRequestDispatcher("/WEB-INF/views/admin/major-list.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa ngành học
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Long id = Long.parseLong(idStr);
                Major major = majorDAO.findById(id);
                if (major != null) {
                    request.setAttribute("major", major);
                    List<Department> departments = departmentDAO.getAllDepartments();
                    request.setAttribute("departments", departments);
                    request.getRequestDispatcher("/WEB-INF/views/admin/major-edit.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID ngành học không hợp lệ: " + idStr);
            }
        }
        if (request.getAttribute("errorMessage") == null) {
             request.setAttribute("errorMessage", "Không tìm thấy ngành học.");
        }
        listMajors(request, response);
    }
    
    /**
     * Hiển thị form tạo ngành học mới
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("/WEB-INF/views/admin/major-create.jsp").forward(request, response);
    }
    
    /**
     * Tạo ngành học mới
     */
    private void createMajorAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String description = request.getParameter("description");
        String departmentIdStr = request.getParameter("departmentId");
        
        // Xác thực dữ liệu
        if (name == null || name.trim().isEmpty() || 
            code == null || code.trim().isEmpty() ||
            departmentIdStr == null || departmentIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showCreateForm(request, response);
            return;
        }
        
        // Kiểm tra ngành học đã tồn tại
        if (majorDAO.findByName(name.trim()) != null) {
            request.setAttribute("errorMessage", "Tên ngành học đã tồn tại.");
            showCreateForm(request, response);
            return;
        }
        
        try {
            Long departmentId = Long.parseLong(departmentIdStr);
            Department department = departmentDAO.findById(departmentId);
            
            if (department == null) {
                request.setAttribute("errorMessage", "Khoa/Đơn vị không tồn tại.");
                showCreateForm(request, response);
                return;
            }
            
            // Tạo đối tượng ngành học mới
            Major major = new Major();
            major.setName(name.trim());
            major.setCode(code.trim());
            major.setDescription(description != null ? description.trim() : "");
            major.setDepartment(department);
            
            // Lưu ngành học vào CSDL
            boolean saveSuccess = majorDAO.saveMajor(major);
            
            if (saveSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/majors?message=create_success");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo ngành học. Vui lòng thử lại.");
                showCreateForm(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID Khoa/Đơn vị không hợp lệ.");
            showCreateForm(request, response);
        }
    }
    
    /**
     * Cập nhật ngành học
     */
    private void updateMajorAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String description = request.getParameter("description");
        String departmentIdStr = request.getParameter("departmentId");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/majors?error=missing_id");
            return;
        }
        
        if (name == null || name.trim().isEmpty() || 
            code == null || code.trim().isEmpty() ||
            departmentIdStr == null || departmentIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showEditForm(request, response);
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            Long departmentId = Long.parseLong(departmentIdStr);
            
            // Kiểm tra ngành học tồn tại
            Major major = majorDAO.findById(id);
            if (major == null) {
                response.sendRedirect(request.getContextPath() + "/admin/majors?error=major_not_found");
                return;
            }
            
            // Kiểm tra khoa tồn tại
            Department department = departmentDAO.findById(departmentId);
            if (department == null) {
                request.setAttribute("errorMessage", "Khoa/Đơn vị không tồn tại.");
                request.setAttribute("major", major);
                List<Department> departments = departmentDAO.getAllDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/WEB-INF/views/admin/major-edit.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra tên ngành học đã tồn tại
            Major existingMajor = majorDAO.findByName(name.trim());
            if (existingMajor != null && !existingMajor.getId().equals(id)) {
                request.setAttribute("errorMessage", "Tên ngành học đã tồn tại.");
                request.setAttribute("major", major);
                List<Department> departments = departmentDAO.getAllDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/WEB-INF/views/admin/major-edit.jsp").forward(request, response);
                return;
            }
            
            // Cập nhật thông tin ngành học
            major.setName(name.trim());
            major.setCode(code.trim());
            major.setDescription(description != null ? description.trim() : "");
            major.setDepartment(department);
            
            // Lưu cập nhật vào CSDL
            boolean updateSuccess = majorDAO.updateMajor(major);
            
            if (updateSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/majors?message=update_success");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật ngành học. Vui lòng thử lại.");
                request.setAttribute("major", major);
                List<Department> departments = departmentDAO.getAllDepartments();
                request.setAttribute("departments", departments);
                request.getRequestDispatcher("/WEB-INF/views/admin/major-edit.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/majors?error=invalid_id");
        }
    }
    
    /**
     * Xóa ngành học
     */
    private void deleteMajorAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/majors?error=missing_id");
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            
            boolean deleteSuccess = majorDAO.deleteMajor(id);
            
            if (deleteSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/majors?message=delete_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/majors?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/majors?error=invalid_id");
        }
    }
} 