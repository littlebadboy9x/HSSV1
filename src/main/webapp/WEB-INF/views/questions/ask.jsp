<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt câu hỏi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <!-- CKEditor CDN -->
    <script src="https://cdn.ckeditor.com/4.16.0/standard/ckeditor.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    
    <div class="container mt-4">
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Đặt câu hỏi</h4>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                ${errorMessage}
                            </div>
                        </c:if>
                        
                        <form method="post" action="${pageContext.request.contextPath}/questions/ask">
                            <div class="form-group">
                                <label for="title">Tiêu đề câu hỏi <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="title" name="title" 
                                    value="${title}" required 
                                    placeholder="Nhập tiêu đề ngắn gọn về câu hỏi của bạn"
                                    maxlength="255">
                                <small class="form-text text-muted">
                                    Tiêu đề nên ngắn gọn và mô tả rõ vấn đề bạn đang gặp phải.
                                </small>
                            </div>
                            
                            <div class="form-group">
                                <label for="categoryId">Danh mục <span class="text-danger">*</span></label>
                                <select class="form-control" id="categoryId" name="categoryId" required>
                                    <option value="">-- Chọn danh mục --</option>
                                    <c:forEach var="category" items="${categories}">
                                        <option value="${category.id}" 
                                            ${categoryId == category.id ? 'selected' : ''}>
                                            ${category.name}
                                        </option>
                                    </c:forEach>
                                </select>
                                <small class="form-text text-muted">
                                    Chọn danh mục phù hợp với câu hỏi của bạn.
                                </small>
                            </div>
                            
                            <div class="form-group">
                                <label for="majorId">Ngành học liên quan</label>
                                <select class="form-control" id="majorId" name="majorId">
                                    <option value="">-- Chọn ngành học (nếu có) --</option>
                                    <c:forEach var="major" items="${majors}">
                                        <option value="${major.id}" 
                                            ${majorId == major.id ? 'selected' : ''}>
                                            ${major.name}
                                        </option>
                                    </c:forEach>
                                </select>
                                <small class="form-text text-muted">
                                    Nếu câu hỏi liên quan đến một ngành học cụ thể, hãy chọn ngành đó.
                                </small>
                            </div>
                            
                            <div class="form-group">
                                <label for="content">Nội dung câu hỏi <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="content" name="content" rows="8" required>${content}</textarea>
                                <small class="form-text text-muted">
                                    Mô tả chi tiết vấn đề bạn đang gặp phải. Càng chi tiết, cơ hội nhận được câu trả lời hữu ích càng cao.
                                </small>
                            </div>
                            
                            <div class="form-group form-check">
                                <input type="checkbox" class="form-check-input" id="isAnonymous" name="isAnonymous" 
                                    ${isAnonymous ? 'checked' : ''}>
                                <label class="form-check-label" for="isAnonymous">Đăng câu hỏi ẩn danh</label>
                                <small class="form-text text-muted d-block">
                                    Nếu bạn chọn ẩn danh, người khác sẽ không thấy thông tin của bạn.
                                </small>
                            </div>
                            
                            <div class="form-group text-center mt-4">
                                <button type="submit" class="btn btn-primary px-5">Đăng câu hỏi</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../includes/footer.jsp" />
    
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    
    <script>
        // Khởi tạo CKEditor cho textarea nội dung
        CKEDITOR.replace('content', {
            language: 'vi',
            height: 300,
            filebrowserUploadUrl: '${pageContext.request.contextPath}/upload',
            removeButtons: 'Save,NewPage,Preview,Print,Templates,PasteFromWord'
        });
    </script>
</body>
</html> 
