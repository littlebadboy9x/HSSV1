<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Ngành học Mới - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>Thêm Ngành học Mới</h1>
            <a href="${pageContext.request.contextPath}/admin/majors" class="btn btn-secondary">
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
                <h5 class="card-title mb-0">Thông tin ngành học mới</h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/admin/majors" method="post">
                    <input type="hidden" name="action" value="create">
                    
                    <div class="form-group">
                        <label for="name">Tên ngành học <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="name" name="name" required 
                               maxlength="100" value="${param.name}" 
                               placeholder="Nhập tên ngành học">
                        <small class="form-text text-muted">Tên ngành học phải là duy nhất và không quá 100 ký tự.</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="code">Mã ngành học <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="code" name="code" required 
                               maxlength="20" value="${param.code}" 
                               placeholder="Nhập mã ngành học (ví dụ: KTPM, CNTT, HTTT)">
                        <small class="form-text text-muted">Mã ngành học phải là duy nhất và không quá 20 ký tự.</small>
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
                        <small class="form-text text-muted">Chọn khoa/đơn vị quản lý ngành học này.</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Mô tả</label>
                        <textarea class="form-control" id="description" name="description" 
                                  rows="4" placeholder="Nhập mô tả ngành học">${param.description}</textarea>
                        <small class="form-text text-muted">Mô tả ngắn gọn về ngành học này.</small>
                    </div>
                    
                    <div class="form-group text-right">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Lưu ngành học
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/majors" class="btn btn-secondary">
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