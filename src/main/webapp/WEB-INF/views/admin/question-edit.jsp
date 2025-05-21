<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa câu hỏi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <!-- CKEditor 4 -->
    <script src="https://cdn.ckeditor.com/4.16.0/standard/ckeditor.js"></script>
</head>
<body>
    <jsp:include page="../includes/admin-header.jsp"/>
    
    <div class="container-fluid mt-4">
        <div class="row">
            <div class="col-md-2">
                <jsp:include page="../includes/admin-sidebar.jsp"/>
            </div>
            
            <div class="col-md-10">
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${errorMessage}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </c:if>
                
                <!-- Breadcrumb -->
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/questions">Quản lý câu hỏi</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Chỉnh sửa câu hỏi #${question.id}</li>
                    </ol>
                </nav>
                
                <!-- Form chỉnh sửa câu hỏi -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="fas fa-edit mr-2"></i>Chỉnh sửa câu hỏi</h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/admin/questions" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="edit">
                            <input type="hidden" name="id" value="${question.id}">
                            
                            <div class="form-group">
                                <label for="title">Tiêu đề <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="title" name="title" value="${question.title}" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="content">Nội dung <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="content" name="content" rows="10" required>${question.content}</textarea>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="categoryId">Danh mục <span class="text-danger">*</span></label>
                                        <select class="form-control" id="categoryId" name="categoryId" required>
                                            <option value="">-- Chọn danh mục --</option>
                                            <c:forEach items="${categories}" var="category">
                                                <option value="${category.id}" ${question.category.id == category.id ? 'selected' : ''}>${category.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="majorId">Ngành học</label>
                                        <select class="form-control" id="majorId" name="majorId">
                                            <option value="">-- Chọn ngành học --</option>
                                            <c:forEach items="${majors}" var="major">
                                                <option value="${major.id}" ${question.major != null && question.major.id == major.id ? 'selected' : ''}>${major.name}</option>
                                            </c:forEach>
                                        </select>
                                        <small class="form-text text-muted">Để trống nếu không liên quan đến ngành học cụ thể</small>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label>Trạng thái</label>
                                <div>
                                    <div class="custom-control custom-radio custom-control-inline">
                                        <input type="radio" id="statusPending" name="status" value="PENDING" class="custom-control-input" ${question.status == 'PENDING' ? 'checked' : ''}>
                                        <label class="custom-control-label" for="statusPending">Chờ trả lời</label>
                                    </div>
                                    <div class="custom-control custom-radio custom-control-inline">
                                        <input type="radio" id="statusAnswered" name="status" value="ANSWERED" class="custom-control-input" ${question.status == 'ANSWERED' ? 'checked' : ''}>
                                        <label class="custom-control-label" for="statusAnswered">Đã trả lời</label>
                                    </div>
                                    <div class="custom-control custom-radio custom-control-inline">
                                        <input type="radio" id="statusClosed" name="status" value="CLOSED" class="custom-control-input" ${question.status == 'CLOSED' ? 'checked' : ''}>
                                        <label class="custom-control-label" for="statusClosed">Đã đóng</label>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <div class="custom-control custom-checkbox">
                                    <input type="checkbox" class="custom-control-input" id="anonymous" name="anonymous" value="true" ${question.anonymous ? 'checked' : ''}>
                                    <label class="custom-control-label" for="anonymous">Hiển thị ẩn danh (không hiển thị thông tin người đặt câu hỏi)</label>
                                </div>
                            </div>
                            
                            <div class="form-group text-right">
                                <a href="${pageContext.request.contextPath}/admin/questions?action=view&id=${question.id}" class="btn btn-secondary mr-2">
                                    <i class="fas fa-times mr-1"></i>Hủy
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save mr-1"></i>Lưu thay đổi
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    
    <script>
        $(document).ready(function() {
            // Khởi tạo CKEditor
            CKEDITOR.replace('content', {
                language: 'vi',
                height: 300,
                removeButtons: 'Save,NewPage,Preview,Print,Templates,PasteFromWord'
            });
        });
    </script>
</body>
</html> 