package org.example.hssv1.controller.admin;

import org.example.hssv1.dao.UserDAO;
import org.example.hssv1.dao.AdvisorProfileDAO;
import org.example.hssv1.dao.DepartmentDAO;
import org.example.hssv1.dao.MajorDAO;
import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.AdvisorProfile;
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
 * Controller xử lý quản lý người dùng
 */
@WebServlet("/admin/users")
public class UserManagementController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private AdvisorProfileDAO advisorProfileDAO;
    private DepartmentDAO departmentDAO;
    private MajorDAO majorDAO;
    
    @Override
    public void init() {
        userDAO = new UserDAO();
        advisorProfileDAO = new AdvisorProfileDAO();
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
        action = (action == null) ? "list" : action.trim(); // Default to list and trim

        switch (action) {
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteUserAction(request, response); // Renamed for clarity
                break;
            case "view":
                viewUser(request, response);
                break;
            case "create":
                showCreateForm(request, response);
                break;
            case "list": // Explicitly handle list
            default: // Default to listUsers if action is unknown
                listUsers(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // Set character encoding for POST
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null || action.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users"); // Redirect if no action
            return;
        }
        action = action.trim();

        switch (action) {
            case "create":
                createUserAction(request, response); // Renamed for clarity
                break;
            case "update":
                updateUserAction(request, response); // Renamed for clarity
                break;
            case "updatePassword": // Changed from "password" for clarity
                changePasswordAction(request, response); // Renamed for clarity
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/users?error=unknown_action");
                break;
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
                Long id = Long.parseLong(idStr);
                CustomUser user = userDAO.findById(id); // Use findById(Long)
                if (user != null) {
                    request.setAttribute("user", user);
                    AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(id); // Use findByUserId(Long)
                    request.setAttribute("advisorProfile", advisorProfile);
                    List<Department> departments = departmentDAO.getAllDepartments();
                    request.setAttribute("departments", departments);
                    List<Major> majors = majorDAO.getAllMajors();
                    request.setAttribute("majors", majors);
                    request.setAttribute("userTypes", CustomUser.UserType.values());
                    request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
                    request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID người dùng không hợp lệ: " + idStr);
            }
        }
        // If id is null, empty, invalid, or user not found, redirect to list with potential error message
        if (request.getAttribute("errorMessage") == null) {
             request.setAttribute("errorMessage", "Không tìm thấy người dùng.");
        }
        listUsers(request, response); 
    }
    
    /**
     * Xem chi tiết người dùng
     */
    private void viewUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                Long id = Long.parseLong(idStr);
                CustomUser user = userDAO.findById(id); // Use findById(Long)
                if (user != null) {
                    request.setAttribute("user", user);
                    AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(id); // Use findByUserId(Long)
                    request.setAttribute("advisorProfile", advisorProfile);
                    request.getRequestDispatcher("/WEB-INF/views/admin/user-view.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID người dùng không hợp lệ: " + idStr);
            }
        }
        if (request.getAttribute("errorMessage") == null) {
             request.setAttribute("errorMessage", "Không tìm thấy người dùng.");
        }
        listUsers(request, response);
    }
    
    /**
     * Hiển thị form tạo người dùng mới
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);
        List<Major> majors = majorDAO.getAllMajors();
        request.setAttribute("majors", majors);
        request.setAttribute("userTypes", CustomUser.UserType.values());
        request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
        // Clear any previous error message that might have been set by a failed create attempt
        // request.removeAttribute("errorMessage"); 
        // No, keep errorMessage if it was set by createUserAction and forwarded here
        request.getRequestDispatcher("/WEB-INF/views/admin/user-create.jsp").forward(request, response);
    }
    
    /**
     * Tạo người dùng mới
     */
    private void createUserAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String userTypeStr = request.getParameter("userType");
        
        String studentId = request.getParameter("studentId");
        String schoolYear = request.getParameter("schoolYear");
        String phoneNumber = request.getParameter("phoneNumber");
        String className = request.getParameter("className");

        boolean isStaff = "on".equals(request.getParameter("isStaff")) || "true".equals(request.getParameter("isStaff"));
        boolean isSuperuser = "on".equals(request.getParameter("isSuperuser")) || "true".equals(request.getParameter("isSuperuser"));
        boolean isAdvisor = "on".equals(request.getParameter("isAdvisor")) || "true".equals(request.getParameter("isAdvisor"));

        // Validate required fields
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() || // Password is required for new user
            fullName == null || fullName.trim().isEmpty() ||
            userTypeStr == null || userTypeStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin bắt buộc (Tên đăng nhập, Email, Mật khẩu, Họ tên, Loại người dùng).");
            showCreateForm(request, response); // Forward back to the form with error
            return;
        }

        // Check uniqueness
        if (userDAO.findByUsername(username.trim()) != null) {
            request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            showCreateForm(request, response);
            return;
        }
        if (userDAO.findByEmail(email.trim()) != null) {
            request.setAttribute("errorMessage", "Email đã tồn tại.");
            showCreateForm(request, response);
            return;
        }

        CustomUser newUser = new CustomUser();
        newUser.setUsername(username.trim());
        newUser.setEmail(email.trim());
        newUser.setPassword(password.trim()); // DAO will hash this plain password
        newUser.setFullName(fullName.trim());
        try {
            newUser.setUserType(CustomUser.UserType.valueOf(userTypeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Loại người dùng không hợp lệ.");
            showCreateForm(request, response);
            return;
        }
        
        // Set optional fields
        if (studentId != null && !studentId.trim().isEmpty()) newUser.setStudentId(studentId.trim());
        if (schoolYear != null && !schoolYear.trim().isEmpty()) newUser.setSchoolYear(schoolYear.trim());
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) newUser.setPhoneNumber(phoneNumber.trim());
        if (className != null && !className.trim().isEmpty()) newUser.setClassName(className.trim());

        newUser.setStaff(isStaff);
        newUser.setSuperuser(isSuperuser);
        // newUser.setActive(true); // isActive is not a field in CustomUser, remove if not added

        if (userDAO.saveUser(newUser)) { // saveUser in DAO now handles password hashing
            if (isAdvisor) {
                AdvisorProfile profile = new AdvisorProfile();
                profile.setUser(newUser); // Link with the newly created user
                
                String departmentIdStr = request.getParameter("departmentId");
                String majorIdStr = request.getParameter("majorId");
                String advisorRoleStr = request.getParameter("advisorRole");

                if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
                    try {
                        Department dept = departmentDAO.findById(Long.parseLong(departmentIdStr));
                        profile.setDepartment(dept);
                    } catch (NumberFormatException e) { 
                        // Log error or handle, for now, it means no department is set
                        System.err.println("Invalid Department ID format: " + departmentIdStr);
                    }
                }
                if (majorIdStr != null && !majorIdStr.isEmpty()){
                     try {
                        Major major = majorDAO.findById(Long.parseLong(majorIdStr));
                        profile.setMajor(major);
                    } catch (NumberFormatException e) { 
                        System.err.println("Invalid Major ID format: " + majorIdStr);
                    }
                }
                // Set Advisor Role
                if (advisorRoleStr != null && !advisorRoleStr.isEmpty()){
                    try {
                        profile.setRole(AdvisorProfile.AdvisorRole.valueOf(advisorRoleStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                         profile.setRole(AdvisorProfile.AdvisorRole.ADVISOR); // Default role if invalid string
                         System.err.println("Invalid Advisor Role string: " + advisorRoleStr + ". Defaulting to ADVISOR.");
                    }
                } else {
                     profile.setRole(AdvisorProfile.AdvisorRole.ADVISOR); // Default role if not provided
                }
                // Add other fields like bio, title from request if they exist in form
                // profile.setBio(request.getParameter("advisorBio"));
                // profile.setTitle(request.getParameter("advisorTitle"));

                advisorProfileDAO.saveProfile(profile);
            }
            response.sendRedirect(request.getContextPath() + "/admin/users?success=create");
        } else {
            request.setAttribute("errorMessage", "Lỗi khi tạo người dùng trong cơ sở dữ liệu.");
            showCreateForm(request, response);
        }
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    private void updateUserAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID người dùng không được để trống.");
            listUsers(request, response); // Or redirect to an error page
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID người dùng không hợp lệ: " + idStr);
            listUsers(request, response);
            return;
        }

        CustomUser user = userDAO.findById(userId);
        if (user == null) {
            request.setAttribute("errorMessage", "Không tìm thấy người dùng với ID: " + userId);
            listUsers(request, response);
            return;
        }

        // Get updated data from form
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String userTypeStr = request.getParameter("userType");
        String studentId = request.getParameter("studentId");
        String schoolYear = request.getParameter("schoolYear");
        String phoneNumber = request.getParameter("phoneNumber");
        String className = request.getParameter("className");
        boolean isStaff = "on".equals(request.getParameter("isStaff")) || "true".equals(request.getParameter("isStaff"));
        boolean isSuperuser = "on".equals(request.getParameter("isSuperuser")) || "true".equals(request.getParameter("isSuperuser"));
        boolean isAdvisor = "on".equals(request.getParameter("isAdvisor")) || "true".equals(request.getParameter("isAdvisor"));

        // Validate required fields
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty() ||
            userTypeStr == null || userTypeStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Tên đăng nhập, Email, Họ tên, Loại người dùng không được để trống.");
            request.setAttribute("user", user); // Pass back the current user object to repopulate form
            // Also need to pass advisorProfile, departments, majors, userTypes, advisorRoles for the form
            AdvisorProfile currentProfile = advisorProfileDAO.findByUserId(userId);
            request.setAttribute("advisorProfile", currentProfile);
            request.setAttribute("departments", departmentDAO.getAllDepartments());
            request.setAttribute("majors", majorDAO.getAllMajors());
            request.setAttribute("userTypes", CustomUser.UserType.values());
            request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
            request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
            return;
        }

        // Check for unique constraints if username/email changed
        if (!user.getUsername().equals(username.trim())) {
            if (userDAO.findByUsername(username.trim()) != null) {
                request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                request.setAttribute("user", user); 
                AdvisorProfile currentProfile = advisorProfileDAO.findByUserId(userId);
                request.setAttribute("advisorProfile", currentProfile);
                request.setAttribute("departments", departmentDAO.getAllDepartments());
                request.setAttribute("majors", majorDAO.getAllMajors());
                request.setAttribute("userTypes", CustomUser.UserType.values());
                request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
                request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
                return;
            }
        }
        if (!user.getEmail().equals(email.trim())) {
            if (userDAO.findByEmail(email.trim()) != null) {
                request.setAttribute("errorMessage", "Email đã tồn tại.");
                request.setAttribute("user", user);
                AdvisorProfile currentProfile = advisorProfileDAO.findByUserId(userId);
                request.setAttribute("advisorProfile", currentProfile);
                request.setAttribute("departments", departmentDAO.getAllDepartments());
                request.setAttribute("majors", majorDAO.getAllMajors());
                request.setAttribute("userTypes", CustomUser.UserType.values());
                request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
                request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
                return;
            }
        }

        // Update user fields
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setFullName(fullName.trim());
        try {
             user.setUserType(CustomUser.UserType.valueOf(userTypeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Loại người dùng không hợp lệ.");
            request.setAttribute("user", user);
            AdvisorProfile currentProfile = advisorProfileDAO.findByUserId(userId);
            request.setAttribute("advisorProfile", currentProfile);
            request.setAttribute("departments", departmentDAO.getAllDepartments());
            request.setAttribute("majors", majorDAO.getAllMajors());
            request.setAttribute("userTypes", CustomUser.UserType.values());
            request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
            request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
            return;
        }
       
        if (studentId != null && !studentId.trim().isEmpty()) user.setStudentId(studentId.trim()); else user.setStudentId(null);
        if (schoolYear != null && !schoolYear.trim().isEmpty()) user.setSchoolYear(schoolYear.trim()); else user.setSchoolYear(null);
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) user.setPhoneNumber(phoneNumber.trim()); else user.setPhoneNumber(null);
        if (className != null && !className.trim().isEmpty()) user.setClassName(className.trim()); else user.setClassName(null);

        user.setStaff(isStaff);
        user.setSuperuser(isSuperuser);
        // user.setActive(true); // isActive is not a field in CustomUser

        AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(userId);

        if (isAdvisor) {
            if (advisorProfile == null) {
                advisorProfile = new AdvisorProfile();
                advisorProfile.setUser(user); // Link to the existing user
            }
            String departmentIdStr = request.getParameter("departmentId");
            String majorIdStr = request.getParameter("majorId");
            String advisorRoleStr = request.getParameter("advisorRole");

            if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
                try {
                    Department dept = departmentDAO.findById(Long.parseLong(departmentIdStr));
                    advisorProfile.setDepartment(dept);
                } catch (NumberFormatException e) { 
                    advisorProfile.setDepartment(null); // Or handle error more explicitly
                     System.err.println("Invalid Department ID format for update: " + departmentIdStr);
                }
            } else {
                advisorProfile.setDepartment(null); // Clear department if not provided or empty string
            }
            
            if (majorIdStr != null && !majorIdStr.isEmpty()){
                 try {
                    Major major = majorDAO.findById(Long.parseLong(majorIdStr));
                    advisorProfile.setMajor(major);
                } catch (NumberFormatException e) { 
                    advisorProfile.setMajor(null);
                    System.err.println("Invalid Major ID format for update: " + majorIdStr);
                }
            } else {
                 advisorProfile.setMajor(null); // Clear major if not provided or empty string
            }

            if (advisorRoleStr != null && !advisorRoleStr.isEmpty()){
                try {
                    advisorProfile.setRole(AdvisorProfile.AdvisorRole.valueOf(advisorRoleStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                     advisorProfile.setRole(AdvisorProfile.AdvisorRole.ADVISOR); // Default role
                     System.err.println("Invalid Advisor Role string for update: " + advisorRoleStr + ". Defaulting to ADVISOR.");
                }
            }  else {
                 advisorProfile.setRole(AdvisorProfile.AdvisorRole.ADVISOR); // Default if not provided
            }
            // Update other advisor fields like bio, title if they are in the form
            // advisorProfile.setBio(request.getParameter("advisorBio"));
            // advisorProfile.setTitle(request.getParameter("advisorTitle"));
            
            // Save or update AdvisorProfile
            if (advisorProfile.getId() == null) { // It's a new profile for this user
                advisorProfileDAO.saveProfile(advisorProfile);
            } else { // It's an existing profile to update
                advisorProfileDAO.updateProfile(advisorProfile);
            }
            user.setAdvisorProfile(advisorProfile); // Ensure user object has the latest profile reference
        } else {
            // If user is no longer an advisor, delete their AdvisorProfile if it exists
            if (advisorProfile != null) {
                advisorProfileDAO.deleteProfile(advisorProfile.getId());
                user.setAdvisorProfile(null); // Remove the association from the user object
            }
        }
        
        // Finally, update the user object itself
        if (userDAO.updateUser(user)) {
            // Update session if the edited user is the logged-in user
            HttpSession session = request.getSession(false);
            if (session != null) {
                CustomUser loggedInUser = (CustomUser) session.getAttribute("user");
                if (loggedInUser != null && loggedInUser.getId().equals(userId)) {
                    // Re-fetch from DB to get the most up-to-date state including associations
                    CustomUser updatedLoggedInUser = userDAO.findById(userId);
                    session.setAttribute("user", updatedLoggedInUser);
                    session.setAttribute("advisorProfile", updatedLoggedInUser.getAdvisorProfile()); 
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/users?action=view&id=" + userId + "&success=update");
        } else {
            request.setAttribute("errorMessage", "Lỗi khi cập nhật người dùng.");
            request.setAttribute("user", user); // Pass back the user object to repopulate form
            // Re-fetch profile for the form as it might have changed or been created
            AdvisorProfile currentProfile = advisorProfileDAO.findByUserId(userId); 
            request.setAttribute("advisorProfile", currentProfile);
            request.setAttribute("departments", departmentDAO.getAllDepartments());
            request.setAttribute("majors", majorDAO.getAllMajors());
            request.setAttribute("userTypes", CustomUser.UserType.values());
            request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
            request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
        }
    }
    
    /**
     * Đổi mật khẩu người dùng
     */
    private void changePasswordAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (idStr == null || idStr.trim().isEmpty() || 
            newPassword == null || newPassword.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID người dùng, mật khẩu mới và xác nhận mật khẩu không được để trống.");
            // To re-show the edit form, we need to load user data again
            // This part might be tricky if changePassword is on a separate form or part of user-edit form
            // Assuming it's part of user-edit, or a dedicated password change page needs this info.
            if (idStr != null && !idStr.isEmpty()) {
                 try {
                    Long userId = Long.parseLong(idStr);
                    CustomUser user = userDAO.findById(userId);
                    request.setAttribute("user", user);
                    AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(userId);
                    request.setAttribute("advisorProfile", advisorProfile);
                    request.setAttribute("departments", departmentDAO.getAllDepartments());
                    request.setAttribute("majors", majorDAO.getAllMajors());
                    request.setAttribute("userTypes", CustomUser.UserType.values());
                    request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
                } catch (NumberFormatException e) { /* User might not be found, but error is about password */ }
            }
            request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
             if (idStr != null && !idStr.isEmpty()) {
                 try {
                    Long userId = Long.parseLong(idStr);
                    CustomUser user = userDAO.findById(userId);
                    request.setAttribute("user", user);
                    AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(userId);
                    request.setAttribute("advisorProfile", advisorProfile);
                    request.setAttribute("departments", departmentDAO.getAllDepartments());
                    request.setAttribute("majors", majorDAO.getAllMajors());
                    request.setAttribute("userTypes", CustomUser.UserType.values());
                    request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
                } catch (NumberFormatException e) { /* User might not be found, but error is about password */ }
            }
            request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
            return;
        }

        try {
            Long userId = Long.parseLong(idStr);
            if (userDAO.updatePassword(userId, newPassword.trim())) { // Password hashed in DAO
                response.sendRedirect(request.getContextPath() + "/admin/users?action=edit&id=" + userId + "&success=password_changed");
            } else {
                request.setAttribute("errorMessage", "Không thể thay đổi mật khẩu. Người dùng không tồn tại hoặc lỗi hệ thống.");
                CustomUser user = userDAO.findById(userId); // Re-fetch for form
                request.setAttribute("user", user);
                AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(userId);
                request.setAttribute("advisorProfile", advisorProfile);
                request.setAttribute("departments", departmentDAO.getAllDepartments());
                request.setAttribute("majors", majorDAO.getAllMajors());
                request.setAttribute("userTypes", CustomUser.UserType.values());
                request.setAttribute("advisorRoles", AdvisorProfile.AdvisorRole.values());
                request.getRequestDispatcher("/WEB-INF/views/admin/user-edit.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID người dùng không hợp lệ: " + idStr);
            listUsers(request, response); // Or redirect to error page
        }
    }
    
    /**
     * Xóa người dùng
     */
    private void deleteUserAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=missing_id_delete");
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=invalid_id_delete");
            return;
        }

        // Prevent deleting oneself
        HttpSession currentSession = request.getSession(false);
        if (currentSession != null) {
            CustomUser loggedInUser = (CustomUser) currentSession.getAttribute("user");
            if (loggedInUser != null && loggedInUser.getId().equals(userId)) {
                 response.sendRedirect(request.getContextPath() + "/admin/users?error=delete_self");
                 return;
            }
        }
        
        CustomUser userToDelete = userDAO.findById(userId);
        if (userToDelete == null) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=user_not_found_delete");
            return;
        }

        // If user has an advisor profile, delete it first.
        // The relation CustomUser <-> AdvisorProfile is OneToOne with cascade ALL and orphanRemoval=true
        // on AdvisorProfile field in CustomUser. So, removing user should cascade to AdvisorProfile.
        // However, explicit deletion of AdvisorProfile first can be safer or if cascade rules are different.
        // Let's test cascade first. If issues, we can add explicit delete.
        // AdvisorProfile advisorProfile = advisorProfileDAO.findByUserId(userId);
        // if (advisorProfile != null) {
        //     advisorProfileDAO.deleteProfile(advisorProfile.getId());
        // }
        // No, the cascade is from User to Profile. If Profile is deleted, user.advisorProfile needs nulling.
        // If User is deleted, Profile should be deleted by cascade.
        // If we want to be super safe, or if cascade isn't behaving as expected for some reason:
        AdvisorProfile advisorProfile = userToDelete.getAdvisorProfile(); // Get from user object
        if (advisorProfile != null) {
            // To ensure foreign key constraints are not violated if we delete profile first,
            // then set user's reference to null and update user before deleting profile.
            // However, since CustomUser is the owning side (mappedBy on AdvisorProfile in CustomUser),
            // and AdvisorProfile has user_id, deleting User should be fine by cascade.
            // For direct profile deletion IF NOT DELETING USER:
            // userToDelete.setAdvisorProfile(null);
            // userDAO.updateUser(userToDelete); // Persist the nullification
            // advisorProfileDAO.deleteProfile(advisorProfile.getId());
            // For now, relying on cascade delete from User to AdvisorProfile when User is deleted.
        }

        if (userDAO.deleteUser(userId)) {
            response.sendRedirect(request.getContextPath() + "/admin/users?success=delete");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=delete_failed");
        }
    }
} 