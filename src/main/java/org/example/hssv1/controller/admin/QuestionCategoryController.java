package org.example.hssv1.controller.admin;

import org.example.hssv1.dao.QuestionCategoryDAO;
import org.example.hssv1.model.QuestionCategory;
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
 * Controller quản lý danh mục câu hỏi
 */
@WebServlet("/admin/categories")
public class QuestionCategoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QuestionCategoryDAO categoryDAO;
    
    @Override
    public void init() {
        categoryDAO = new QuestionCategoryDAO();
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
                deleteCategoryAction(request, response);
                break;
            case "create":
                showCreateForm(request, response);
                break;
            case "list":
            default:
                listCategories(request, response);
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
            response.sendRedirect(request.getContextPath() + "/admin/categories");
            return;
        }
        action = action.trim();

        switch (action) {
            case "create":
                createCategoryAction(request, response);
                break;
            case "update":
                updateCategoryAction(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=unknown_action");
                break;
        }
    }
    
    /**
     * Hiển thị danh sách danh mục
     */
    private void listCategories(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/admin/category-list.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa danh mục
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Long id = Long.parseLong(idStr);
                QuestionCategory category = categoryDAO.findById(id);
                if (category != null) {
                    request.setAttribute("category", category);
                    request.getRequestDispatcher("/WEB-INF/views/admin/category-edit.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID danh mục không hợp lệ: " + idStr);
            }
        }
        if (request.getAttribute("errorMessage") == null) {
             request.setAttribute("errorMessage", "Không tìm thấy danh mục.");
        }
        listCategories(request, response);
    }
    
    /**
     * Hiển thị form tạo danh mục mới
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/category-create.jsp").forward(request, response);
    }
    
    /**
     * Tạo danh mục mới
     */
    private void createCategoryAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        
        // Xác thực dữ liệu
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Tên danh mục không được để trống.");
            showCreateForm(request, response);
            return;
        }
        
        // Kiểm tra tên danh mục đã tồn tại
        if (categoryDAO.findByName(name.trim()) != null) {
            request.setAttribute("errorMessage", "Tên danh mục đã tồn tại.");
            showCreateForm(request, response);
            return;
        }
        
        // Tạo đối tượng danh mục mới
        QuestionCategory category = new QuestionCategory();
        category.setName(name.trim());
        category.setDescription(description != null ? description.trim() : "");
        
        // Lưu danh mục vào CSDL
        boolean saveSuccess = categoryDAO.saveCategory(category);
        
        if (saveSuccess) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?message=create_success");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tạo danh mục. Vui lòng thử lại.");
            showCreateForm(request, response);
        }
    }
    
    /**
     * Cập nhật danh mục
     */
    private void updateCategoryAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?error=missing_id");
            return;
        }
        
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Tên danh mục không được để trống.");
            showEditForm(request, response);
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            
            // Kiểm tra danh mục tồn tại
            QuestionCategory category = categoryDAO.findById(id);
            if (category == null) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=category_not_found");
                return;
            }
            
            // Kiểm tra tên danh mục đã tồn tại
            QuestionCategory existingCategory = categoryDAO.findByName(name.trim());
            if (existingCategory != null && !existingCategory.getId().equals(id)) {
                request.setAttribute("errorMessage", "Tên danh mục đã tồn tại.");
                request.setAttribute("category", category); // Giữ lại thông tin đã nhập
                request.getRequestDispatcher("/WEB-INF/views/admin/category-edit.jsp").forward(request, response);
                return;
            }
            
            // Cập nhật thông tin danh mục
            category.setName(name.trim());
            category.setDescription(description != null ? description.trim() : "");
            
            // Lưu cập nhật vào CSDL
            boolean updateSuccess = categoryDAO.updateCategory(category);
            
            if (updateSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?message=update_success");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật danh mục. Vui lòng thử lại.");
                request.setAttribute("category", category);
                request.getRequestDispatcher("/WEB-INF/views/admin/category-edit.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?error=invalid_id");
        }
    }
    
    /**
     * Xóa danh mục
     */
    private void deleteCategoryAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?error=missing_id");
            return;
        }
        
        try {
            Long id = Long.parseLong(idStr);
            
            boolean deleteSuccess = categoryDAO.deleteCategory(id);
            
            if (deleteSuccess) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?message=delete_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/categories?error=invalid_id");
        }
    }
} 