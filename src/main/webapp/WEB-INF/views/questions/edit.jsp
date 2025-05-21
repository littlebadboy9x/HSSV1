<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa câu hỏi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <!-- Thêm CKEditor 4 thay vì CKEditor 5 -->
    <script src="https://cdn.ckeditor.com/4.16.0/standard/ckeditor.js"></script>
    <style>
        .ck-editor__editable {
            min-height: 300px;
        }
    </style>
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Trang chủ</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/questions">Câu hỏi</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/questions/view?id=${question.id}">Chi tiết</a></li>
                <li class="breadcrumb-item active" aria-current="page">Chỉnh sửa</li>
            </ol>
        </nav>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                ${errorMessage}
            </div>
        </c:if>
        
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0">Chỉnh sửa câu hỏi</h4>
            </div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/questions/edit">
                    <input type="hidden" name="questionId" value="${question.id}">
                    
                    <div class="form-group">
                        <label for="title">Tiêu đề câu hỏi <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="title" name="title" value="${empty title ? question.title : title}" required>
                        <small class="form-text text-muted">Tiêu đề ngắn gọn mô tả câu hỏi của bạn (tối đa 255 ký tự)</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="content">Nội dung chi tiết <span class="text-danger">*</span></label>
                        <textarea class="form-control" id="content" name="content" rows="8" required>${empty content ? question.content : content}</textarea>
                        <small class="form-text text-muted">Mô tả chi tiết câu hỏi của bạn</small>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="categoryId">Danh mục <span class="text-danger">*</span></label>
                                <select class="form-control" id="categoryId" name="categoryId" required>
                                    <option value="">-- Chọn danh mục --</option>
                                    <c:forEach items="${categories}" var="category">
                                        <c:choose>
                                            <c:when test="${not empty categoryId && categoryId eq category.id}">
                                                <option value="${category.id}" selected>${category.name}</option>
                                            </c:when>
                                            <c:when test="${empty categoryId && question.category.id eq category.id}">
                                                <option value="${category.id}" selected>${category.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${category.id}">${category.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="majorId">Ngành học</label>
                                <select class="form-control" id="majorId" name="majorId">
                                    <option value="">-- Chọn ngành học (không bắt buộc) --</option>
                                    <c:forEach items="${majors}" var="major">
                                        <c:choose>
                                            <c:when test="${not empty majorId && majorId eq major.id}">
                                                <option value="${major.id}" selected>${major.name}</option>
                                            </c:when>
                                            <c:when test="${empty majorId && question.major != null && question.major.id eq major.id}">
                                                <option value="${major.id}" selected>${major.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${major.id}">${major.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                                <small class="form-text text-muted">Chọn ngành học liên quan đến câu hỏi (nếu có)</small>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-group form-check">
                        <input type="checkbox" class="form-check-input" id="isAnonymous" name="isAnonymous" ${question.anonymous || isAnonymous ? 'checked' : ''}>
                        <label class="form-check-label" for="isAnonymous">Đặt câu hỏi ẩn danh (không hiển thị tên của bạn)</label>
                    </div>
                    
                    <div class="text-center mt-4">
                        <a href="${pageContext.request.contextPath}/questions/view?id=${question.id}" class="btn btn-secondary mr-2">Hủy</a>
                        <button type="submit" class="btn btn-primary">Cập nhật câu hỏi</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp"/>
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://kit.fontawesome.com/a076d05399.js"></script>
    
    <script>
        // Khởi tạo CKEditor 4 cho textarea
        CKEDITOR.replace('content', {
            language: 'vi',
            height: 300,
            removeButtons: 'Save,NewPage,Preview,Print,Templates,PasteFromWord'
        });
    </script>
</body>
</html> 
