<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Cố vấn Mới - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>Thêm Cố vấn Mới</h1>
            <a href="${pageContext.request.contextPath}/admin/advisors" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách
            </a>
        </div>
        
        <c:if test="${errorMessage != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <strong>Lỗi!</strong> ${errorMessage}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
        
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5 class="card-title mb-0">Thông tin cố vấn mới</h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/admin/advisors" method="post">
                    <input type="hidden" name="action" value="create">
                    
                    <h5 class="border-bottom pb-2 mb-4">Thông tin người dùng</h5>
                    
                    <div class="form-group">
                        <label for="userId">Người dùng <span class="text-danger">*</span></label>
                        <select class="form-control" id="userId" name="userId" required>
                            <option value="">-- Chọn người dùng --</option>
                            <c:forEach var="user" items="${users}">
                                <option value="${user.id}" ${param.userId eq user.id ? 'selected' : ''}>
                                    ${user.fullName} (${user.email})
                                </option>
                            </c:forEach>
                        </select>
                        <small class="form-text text-muted">Chọn người dùng để gán vai trò cố vấn.</small>
                    </div>
                    
                    <h5 class="border-bottom pb-2 mb-4 mt-5">Thông tin chuyên môn</h5>
                    
                    <div class="form-group">
                        <label for="title">Chức danh <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="title" name="title" required 
                               value="${param.title}" 
                               placeholder="Nhập chức danh (ví dụ: Giảng viên, Tiến sĩ, Phó Giáo sư)">
                    </div>
                    
                    <div class="form-group">
                        <label for="departmentId">Khoa/Đơn vị <span class="text-danger">*</span></label>
                        <select class="form-control" id="departmentId" name="departmentId" required>
                            <option value="">-- Chọn khoa/đơn vị --</option>
                            <c:forEach var="department" items="${departments}">
                                <option value="${department.id}" ${param.departmentId eq department.id ? 'selected' : ''}>
                                    ${department.name} (${department.code})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="majorId">Ngành học</label>
                        <select class="form-control" id="majorId" name="majorId">
                            <option value="">-- Chọn ngành học (nếu có) --</option>
                            <c:forEach var="major" items="${majors}">
                                <option value="${major.id}" ${param.majorId eq major.id ? 'selected' : ''}>
                                    ${major.name} (${major.code})
                                </option>
                            </c:forEach>
                        </select>
                        <small class="form-text text-muted">Có thể để trống nếu cố vấn không thuộc ngành cụ thể.</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="expertise">Lĩnh vực chuyên môn</label>
                        <input type="text" class="form-control" id="expertise" name="expertise" 
                               value="${param.expertise}" 
                               placeholder="Nhập lĩnh vực chuyên môn, ngăn cách bởi dấu phẩy">
                    </div>
                    
                    <div class="form-group">
                        <label for="bio">Giới thiệu</label>
                        <textarea class="form-control" id="bio" name="bio" 
                                  rows="4" placeholder="Giới thiệu ngắn gọn về cố vấn">${param.bio}</textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="phone">Số điện thoại</label>
                        <input type="text" class="form-control" id="phone" name="phone" 
                               value="${param.phone}" 
                               placeholder="Nhập số điện thoại liên hệ">
                    </div>
                    
                    <h5 class="border-bottom pb-2 mb-4 mt-5">Cài đặt vai trò</h5>
                    
                    <div class="form-group">
                        <label for="role">Vai trò <span class="text-danger">*</span></label>
                        <select class="form-control" id="role" name="role" required>
                            <c:forEach var="advisorRole" items="${advisorRoles}">
                                <option value="${advisorRole}" ${param.role eq advisorRole ? 'selected' : (advisorRole eq 'ADVISOR' ? 'selected' : '')}>
                                    <c:choose>
                                        <c:when test="${advisorRole eq 'ADMIN'}">Admin</c:when>
                                        <c:otherwise>Cố vấn</c:otherwise>
                                    </c:choose>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <div class="custom-control custom-switch">
                            <input type="checkbox" class="custom-control-input" id="available" name="available" 
                                   ${empty param.available or param.available eq 'on' or param.available eq 'true' ? 'checked' : ''}>
                            <label class="custom-control-label" for="available">Đang hoạt động</label>
                        </div>
                        <small class="form-text text-muted">Tắt nếu cố vấn tạm thời không tiếp nhận câu hỏi.</small>
                    </div>
                    
                    <div class="form-group text-right mt-5">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Lưu thông tin
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/advisors" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Hủy
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 