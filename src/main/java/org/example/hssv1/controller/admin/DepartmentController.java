package org.example.hssv1.controller.admin;

import org.example.hssv1.dao.DepartmentDAO;
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
 * Controller quản lý Khoa/Đơn vị
 */
@WebServlet("/admin/departments")
public class DepartmentController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DepartmentDAO departmentDAO;
    
    @Override
    public void init() {
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
                deleteDepartmentAction(request, response);
                break;
            case "create":
                showCreateForm(request, response);
                break;
            case "list":
            default:
                listDepartments(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }
        action = action.trim();

        switch (action) {
            case "create":
                createDepartmentAction(request, response);
                break;
            case "update":
                updateDepartmentAction(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/departments?error=unknown_action");
                break;
        }
    }
    
    /**
     * Hiển thị danh sách khoa/đơn vị
     */
    private void listDepartments(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("/WEB-INF/views/admin/department-list.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa khoa/đơn vị
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Long id = Long.parseLong(idStr);
                Department department = departmentDAO.findById(id);
                if (department != null) {
                    request.setAttribute("department", department);
                    request.getRequestDispatcher("/WEB-INF/views/admin/department-edit.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID khoa/đơn vị không hợp lệ: " + idStr);
            }
        }
        if (request.getAttribute("errorMessage") == null) {
             request.setAttribute("errorMessage", "Không tìm thấy khoa/đơn vị.");
        }
        listDepartments(request, response);
    }
    
    /**
     * Hiển thị form tạo khoa/đơn vị mới
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/department-create.jsp").forward(request, response);
    }
    
    /**
     * Tạo khoa/đơn vị mới
     */
    private void createDepartmentAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String description = request.getParameter("description");
        
        // Xác thực dữ liệu
        if (name == null || name.trim().isEmpty() || 
            code == null || code.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showCreateForm(request, response);
            return;
        }
        
        // Kiểm tra khoa/đơn vị đã tồn tại
        if (departmentDAO.findByName(name.trim()) != null) {
            request.setAttribute("errorMessage", "Tên khoa/đơn vị đã tồn tại.");
            showCreateForm(request, response);
            return;
        }
        
        // Tạo đối tượng khoa/đơn vị mới
        Department department = new Department();
        department.setName(name.trim());
        department.setCode(code.trim());
        department.setDescription(description != null ? description.trim() : "");
        
        // Lưu khoa/đơn vị vào CSDL
        boolean saveSuccess = departmentDAO.saveDepartment(department);
        
        if (saveSuccess) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?message=create_success");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo khoa/đơn vị. Vui lòng thử lại.");
            showCreateForm(request, response);
        }
    }
    
    /**
     * Cập nhật khoa/đơn vị
     */
    private void updateDepartmentAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String description = request.getParameter("description");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=missing_id");
            return;
        }
        
        if (name == null || name.trim().isEmpty() || 
            code == null || code.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc.");
            showEditForm(request, response);
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            
            // Kiểm tra khoa/đơn vị tồn tại
            Department department = departmentDAO.findById(id);
            if (department == null) {
                response.sendRedirect(request.getContextPath() + "/admin/departments?error=department_not_found");
                return;
            }
            
            // Kiểm tra tên khoa/đơn vị đã tồn tại
            Department existingDepartment = departmentDAO.findByName(name.trim());
            if (existingDepartment != null && !existingDepartment.getId().equals(id)) {
                request.setAttribute("errorMessage", "Tên khoa/đơn vị đã tồn tại.");
                request.setAttribute("department", department);
                request.getRequestDispatcher("/WEB-INF/views/admin/department-edit.jsp").forward(request, response);
                return;
            }
            
            // Cập nhật thông tin khoa/đơn vị
            department.setName(name.trim());
            department.setCode(code.trim());
            department.setDescription(description != null ? description.trim() : "");
            
            // Lưu cập nhật vào CSDL
            boolean updateSuccess = departmentDAO.updateDepartment(department);
            
            if (updateSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/departments?message=update_success");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật khoa/đơn vị. Vui lòng thử lại.");
                request.setAttribute("department", department);
                request.getRequestDispatcher("/WEB-INF/views/admin/department-edit.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=invalid_id");
        }
    }
    
    /**
     * Xóa khoa/đơn vị
     */
    private void deleteDepartmentAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=missing_id");
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            
            boolean deleteSuccess = departmentDAO.deleteDepartment(id);
            
            if (deleteSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/departments?message=delete_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/departments?error=delete_failed&info=department_has_majors_or_advisors");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=invalid_id");
        }
    }
} 