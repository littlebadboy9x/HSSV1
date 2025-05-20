<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt câu hỏi - Hệ thống Tư vấn Sinh viên</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <!-- Thêm CKEditor -->
    <script src="https://cdn.ckeditor.com/ckeditor5/27.1.0/classic/ckeditor.js"></script>
</head>
<body>
    <jsp:include page="../includes/header.jsp"/>
    
    <div class="container mt-4">
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <div class="card">
                    <div class="card-header">
                        <h3 class="mb-0">Đặt câu hỏi</h3>
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
                                <input type="text" class="form-control" id="title" name="title" required
                                       placeholder="Nhập tiêu đề ngắn gọn và rõ ràng">
                            </div>
                            
                            <div class="form-group">
                                <label for="content">Nội dung chi tiết <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="content" name="content" rows="10" required
                                          placeholder="Mô tả chi tiết vấn đề của bạn"></textarea>
                            </div>
                            
                            <div class="form-row">
                                <div class="form-group col-md-6">
                                    <label for="category">Loại câu hỏi</label>
                                    <select class="form-control" id="category" name="category">
                                        <option value="">-- Chọn loại câu hỏi --</option>
                                        <c:forEach items="${categories}" var="category">
                                            <option value="${category.id}">${category.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                
                                <div class="form-group col-md-6">
                                    <label for="major">Ngành học liên quan</label>
                                    <select class="form-control" id="major" name="major">
                                        <option value="">-- Chọn ngành học --</option>
                                        <c:forEach items="${majors}" var="major">
                                            <option value="${major.id}">${major.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            
                            <div class="text-right">
                                <a href="${pageContext.request.contextPath}/questions" class="btn btn-secondary">
                                    Hủy bỏ
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-paper-plane"></i> Gửi câu hỏi
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
    <script src="https://kit.fontawesome.com/a076d05399.js"></script>
    
    <script>
        // Khởi tạo CKEditor cho textarea
        ClassicEditor
            .create(document.querySelector('#content'))
            .catch(error => {
                console.error(error);
            });
    </script>
</body>
</html> 