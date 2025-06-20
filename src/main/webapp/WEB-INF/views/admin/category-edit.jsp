<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa Danh mục Câu hỏi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>Chỉnh sửa Danh mục Câu hỏi</h1>
            <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">
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
        
        <c:if test="${category != null}">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h5 class="card-title mb-0">Thông tin danh mục</h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/admin/categories" method="post">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${category.id}">
                        
                        <div class="form-group">
                            <label for="name">Tên danh mục <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" required 
                                   maxlength="100" value="${category.name}" 
                                   placeholder="Nhập tên danh mục">
                            <small class="form-text text-muted">Tên danh mục phải là duy nhất và không quá 100 ký tự.</small>
                        </div>
                        
                        <div class="form-group">
                            <label for="description">Mô tả</label>
                            <textarea class="form-control" id="description" name="description" 
                                      rows="4" placeholder="Nhập mô tả danh mục">${category.description}</textarea>
                            <small class="form-text text-muted">Mô tả ngắn gọn về danh mục câu hỏi này.</small>
                        </div>
                        
                        <div class="form-group text-right">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Lưu thay đổi
                            </button>
                            <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">
                                <i class="fas fa-times"></i> Hủy
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
        
        <c:if test="${category == null}">
            <div class="alert alert-warning" role="alert">
                <i class="fas fa-exclamation-triangle"></i> Không tìm thấy danh mục với ID được cung cấp.
                <br>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-sm btn-warning mt-2">
                    Quay lại danh sách danh mục
                </a>
            </div>
        </c:if>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 